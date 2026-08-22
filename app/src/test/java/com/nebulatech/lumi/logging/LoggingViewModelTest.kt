package com.nebulatech.lumi.logging

import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.EmptyResult
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.model.BbtReading
import com.nebulatech.lumi.data.model.BbtSource
import com.nebulatech.lumi.data.model.Cycle
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.DailyLog
import com.nebulatech.lumi.data.model.FlowIntensityType
import com.nebulatech.lumi.data.model.LhIntensityType
import com.nebulatech.lumi.data.model.LhTest
import com.nebulatech.lumi.data.model.MoodType
import com.nebulatech.lumi.data.model.PastCycleInput
import com.nebulatech.lumi.data.repository.BbtRepository
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.LhTestRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class LoggingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeDailyLogRepository: FakeDailyLogRepository
    private lateinit var fakeBbtRepository: FakeBbtRepository
    private lateinit var fakeLhTestRepository: FakeLhTestRepository
    private lateinit var fakeCycleRepository: FakeCycleRepository
    private lateinit var viewModel: LoggingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeDailyLogRepository = FakeDailyLogRepository()
        fakeBbtRepository = FakeBbtRepository()
        fakeLhTestRepository = FakeLhTestRepository()
        fakeCycleRepository = FakeCycleRepository()

        viewModel = LoggingViewModel(
            dailyLogRepository = fakeDailyLogRepository,
            bbtRepository = fakeBbtRepository,
            lhTestRepository = fakeLhTestRepository,
            cycleRepository = fakeCycleRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveFlowLog with custom past date saves to database on that date and starts new cycle on that date when period due`() = runTest {
        val customLogDate = LocalDate.of(2026, 8, 20)
        fakeCycleRepository.currentPhase = CyclePhase.PERIOD_PREDICTED
        fakeCycleRepository.cycleDay = 29
        fakeCycleRepository.avgCycleLength = 28

        viewModel.onAction(
            LoggingAction.SaveFlowLog(
                flow = FlowIntensity.MEDIUM,
                mood = MoodItem.CALM,
                symptoms = setOf("Cramps", "Bloating"),
                logDate = customLogDate
            )
        )

        advanceUntilIdle()

        // 1. Verify that a new cycle was started on the exact custom date chosen by user
        assertEquals(customLogDate, fakeCycleRepository.startedCycleDate)

        // 2. Verify flow log was saved with the custom date
        assertNotNull(fakeDailyLogRepository.savedFlowLogRecord)
        assertEquals(customLogDate, fakeDailyLogRepository.savedFlowLogRecord?.date)
        assertEquals(FlowIntensityType.MEDIUM, fakeDailyLogRepository.savedFlowLogRecord?.flowIntensity)
        assertEquals(MoodType.CALM, fakeDailyLogRepository.savedFlowLogRecord?.mood)
        assertEquals(setOf("Cramps", "Bloating"), fakeDailyLogRepository.savedFlowLogRecord?.symptoms)
    }

    @Test
    fun `saveFlowLog during mid cycle saves log on custom date without starting new cycle`() = runTest {
        val customLogDate = LocalDate.of(2026, 8, 15)
        fakeCycleRepository.currentPhase = CyclePhase.FOLLICULAR
        fakeCycleRepository.cycleDay = 10
        fakeCycleRepository.avgCycleLength = 28

        viewModel.onAction(
            LoggingAction.SaveFlowLog(
                flow = FlowIntensity.LIGHT,
                mood = MoodItem.ENERGETIC,
                symptoms = setOf("Headache"),
                logDate = customLogDate
            )
        )

        advanceUntilIdle()

        // 1. Verify startNewCycle was NOT called
        assertEquals(null, fakeCycleRepository.startedCycleDate)

        // 2. Verify flow log was saved on the custom date
        assertEquals(customLogDate, fakeDailyLogRepository.savedFlowLogRecord?.date)
        assertEquals(FlowIntensityType.LIGHT, fakeDailyLogRepository.savedFlowLogRecord?.flowIntensity)
    }

    // ── Test Fakes ────────────────────────────────────────────────────────────

    private class FakeDailyLogRepository : DailyLogRepository {
        data class SavedFlowRecord(
            val userId: String,
            val date: LocalDate,
            val flowIntensity: FlowIntensityType?,
            val mood: MoodType?,
            val symptoms: Set<String>,
            val cycleId: String?,
            val cycleDay: Int?,
            val cyclePhase: CyclePhase?
        )

        var savedFlowLogRecord: SavedFlowRecord? = null

        override fun getLogForDate(userId: String, date: LocalDate): Flow<DailyLog?> = flowOf(null)

        override suspend fun getOrCreateLogForDate(
            userId: String,
            date: LocalDate,
            cycleId: String?,
            cycleDay: Int?,
            cyclePhase: CyclePhase?
        ): Result<DailyLog, DataError.Local> = Result.Error(DataError.Local.UNKNOWN)

        override suspend fun saveFlowLog(
            userId: String,
            date: LocalDate,
            flowIntensity: FlowIntensityType?,
            mood: MoodType?,
            symptoms: Set<String>,
            cycleId: String?,
            cycleDay: Int?,
            cyclePhase: CyclePhase?
        ): EmptyResult<DataError.Local> {
            savedFlowLogRecord = SavedFlowRecord(
                userId, date, flowIntensity, mood, symptoms, cycleId, cycleDay, cyclePhase
            )
            return Result.Success(Unit)
        }

        override fun getLogsInRange(
            userId: String,
            fromDate: LocalDate,
            toDate: LocalDate
        ): Flow<List<DailyLog>> = flowOf(emptyList())

        override suspend fun getSymptomFrequency(
            userId: String,
            days: Int
        ): Result<Map<String, Int>, DataError.Local> = Result.Success(emptyMap())
    }

    private class FakeCycleRepository : CycleRepository {
        var startedCycleDate: LocalDate? = null
        var currentPhase: CyclePhase = CyclePhase.FOLLICULAR
        var cycleDay: Int = 1
        var avgCycleLength: Int = 28

        override fun getCurrentCycle(userId: String): Flow<Cycle?> = flowOf(
            Cycle(
                id = "cycle-1",
                userId = userId,
                cycleNumber = 1,
                startDate = "2026-08-01",
                endDate = null,
                cycleLength = null,
                periodLength = 5,
                ovulationDate = null,
                isCurrent = true,
                isRegular = true,
                notes = null,
                createdAt = "now",
                updatedAt = "now"
            )
        )

        override fun getAllCycles(userId: String): Flow<List<Cycle>> = flowOf(emptyList())
        override fun getLastNCycles(userId: String, n: Int): Flow<List<Cycle>> = flowOf(emptyList())

        override suspend fun startNewCycle(
            userId: String,
            startDate: LocalDate
        ): Result<Cycle, DataError.Local> {
            startedCycleDate = startDate
            return Result.Success(
                Cycle(
                    id = "cycle-new",
                    userId = userId,
                    cycleNumber = 2,
                    startDate = startDate.toString(),
                    endDate = null,
                    cycleLength = null,
                    periodLength = 5,
                    ovulationDate = null,
                    isCurrent = true,
                    isRegular = true,
                    notes = null,
                    createdAt = "now",
                    updatedAt = "now"
                )
            )
        }

        override suspend fun seedHistoricalCycles(
            userId: String,
            currentCycleStartDate: LocalDate,
            cycleLength: Int,
            periodDuration: Int,
            numberOfPastCycles: Int
        ): EmptyResult<DataError.Local> = Result.Success(Unit)

        override suspend fun seedManualHistoricalCycles(
            userId: String,
            currentCycleStartDate: LocalDate,
            pastCycles: List<PastCycleInput>,
            currentPeriodDuration: Int
        ): EmptyResult<DataError.Local> = Result.Success(Unit)

        override suspend fun updateCycle(cycle: Cycle): EmptyResult<DataError.Local> = Result.Success(Unit)
        override fun getCycleDay(userId: String, targetDate: LocalDate): Flow<Int> = flowOf(cycleDay)
        override fun getAverageCycleLength(userId: String): Flow<Int> = flowOf(avgCycleLength)
        override fun getAveragePeriodLength(userId: String): Flow<Int> = flowOf(5)
        override fun getCurrentPhase(userId: String, targetDate: LocalDate): Flow<CyclePhase> = flowOf(currentPhase)
    }

    private class FakeBbtRepository : BbtRepository {
        override fun getBbtForDate(userId: String, date: LocalDate): Flow<BbtReading?> = flowOf(null)
        override fun getBbtForCycle(cycleId: String): Flow<List<BbtReading>> = flowOf(emptyList())
        override fun getBbtInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<BbtReading>> = flowOf(emptyList())
        override suspend fun saveBbtReading(
            userId: String,
            date: LocalDate,
            temperature: Double,
            temperatureUnit: String,
            readingTime: String?,
            cycleId: String?,
            cycleDay: Int?,
            disturbedSleep: Boolean,
            feverIllness: Boolean,
            source: BbtSource
        ): Result<BbtReading, DataError.Local> = Result.Error(DataError.Local.UNKNOWN)
    }

    private class FakeLhTestRepository : LhTestRepository {
        override fun getLhTestForDate(userId: String, date: LocalDate): Flow<LhTest?> = flowOf(null)
        override fun getLhTestsForCycle(cycleId: String): Flow<List<LhTest>> = flowOf(emptyList())
        override fun getLhTestsInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<LhTest>> = flowOf(emptyList())
        override suspend fun saveLhTest(
            userId: String,
            date: LocalDate,
            intensity: LhIntensityType,
            testBrand: String?,
            photoLocalPath: String?,
            cycleId: String?,
            cycleDay: Int?
        ): Result<LhTest, DataError.Local> = Result.Error(DataError.Local.UNKNOWN)
    }
}
