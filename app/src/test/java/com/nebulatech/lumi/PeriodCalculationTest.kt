package com.nebulatech.lumi

import com.nebulatech.lumi.data.local.dao.CycleDao
import com.nebulatech.lumi.data.local.dao.DailyLogDao
import com.nebulatech.lumi.data.local.dao.UserProfileDao
import com.nebulatech.lumi.data.local.entity.CycleEntity
import com.nebulatech.lumi.data.local.entity.DailyLogEntity
import com.nebulatech.lumi.data.local.entity.UserProfileEntity
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.FlowIntensityType
import com.nebulatech.lumi.data.model.PastCycleInput
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.RoomCycleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class PeriodCalculationTest {

    private lateinit var fakeCycleDao: FakeCycleDao
    private lateinit var fakeUserProfileDao: FakeUserProfileDao
    private lateinit var fakeDailyLogDao: FakeDailyLogDao
    private lateinit var cycleRepository: RoomCycleRepository

    private val testUserId = "test-user-123"

    @Before
    fun setUp() {
        fakeCycleDao = FakeCycleDao()
        fakeUserProfileDao = FakeUserProfileDao()
        fakeDailyLogDao = FakeDailyLogDao()
        cycleRepository = RoomCycleRepository(
            cycleDao = fakeCycleDao,
            userProfileDao = fakeUserProfileDao,
            dailyLogDao = fakeDailyLogDao
        )
    }

    // ── SCENARIO 1: Single Entry Baseline ─────────────────────────────────────
    @Test
    fun `scenario 1 - single entry uses entered cycle length and flow duration`() = runBlocking {
        val startDate = LocalDate.of(2026, 8, 1)
        val enteredCycleLength = 30
        val enteredFlowLength = 4

        // Seed baseline historical cycles with entered values
        val seedResult = cycleRepository.seedHistoricalCycles(
            userId = testUserId,
            currentCycleStartDate = startDate,
            cycleLength = enteredCycleLength,
            periodDuration = enteredFlowLength,
            numberOfPastCycles = 3
        )
        assertTrue(seedResult is com.nebulatech.lumi.core.domain.Result.Success)

        // Save user profile with entered values
        fakeUserProfileDao.insertOrUpdate(
            UserProfileEntity(
                id = "p-1",
                userId = testUserId,
                cycleLength = enteredCycleLength,
                periodDuration = enteredFlowLength,
                primaryGoal = "TRACK_CYCLE",
                trackingStartedDate = "2026-08-01T00:00:00Z",
                updatedAt = "2026-08-01T00:00:00Z"
            )
        )

        // 1. Average cycle length should match entered value
        val avgCycleLength = cycleRepository.getAverageCycleLength(testUserId).first()
        assertEquals(30, avgCycleLength)

        // 2. Average period flow length should match entered value
        val avgFlowLength = cycleRepository.getAveragePeriodLength(testUserId).first()
        assertEquals(4, avgFlowLength)

        // 3. Current active cycle should be created with correct start date and period length
        val currentCycle = cycleRepository.getCurrentCycle(testUserId).first()
        assertNotNull(currentCycle)
        assertEquals("2026-08-01", currentCycle?.startDate)
        assertEquals(4, currentCycle?.periodLength)
        assertTrue(currentCycle?.isCurrent == true)

        // 4. Phase calculation on cycle day 1-4 is Menstruation, 5-12 is Follicular, etc.
        // Day 2 (Aug 2) -> Menstruation
        val phaseDay2 = cycleRepository.getCurrentPhase(testUserId, startDate.plusDays(1)).first()
        assertEquals(CyclePhase.MENSTRUATION, phaseDay2)

        // Day 5 (Aug 5) -> Follicular
        val phaseDay5 = cycleRepository.getCurrentPhase(testUserId, startDate.plusDays(4)).first()
        assertEquals(CyclePhase.FOLLICULAR, phaseDay5)

        // Day 15 (Aug 15) -> Fertile Window (Ovulation = 30 - 14 = Day 16, Window = 13..17)
        val phaseDay15 = cycleRepository.getCurrentPhase(testUserId, startDate.plusDays(14)).first()
        assertEquals(CyclePhase.FERTILE_WINDOW, phaseDay15)
    }

    // ── SCENARIO 2: 3-Month Historical Manual Entry ───────────────────────────
    @Test
    fun `scenario 2 - previous 3 months data calculates average cycle and flow length`() = runBlocking {
        val startDate = LocalDate.of(2026, 8, 1)

        // Past 3 cycles:
        // Cycle 1 (most recent past): 28 days length, 4 days flow
        // Cycle 2: 30 days length, 5 days flow
        // Cycle 3 (oldest): 32 days length, 6 days flow
        val pastCycles = listOf(
            PastCycleInput(startDate = LocalDate.of(2026, 7, 4), cycleLength = 28, periodDuration = 4),
            PastCycleInput(startDate = LocalDate.of(2026, 6, 4), cycleLength = 30, periodDuration = 5),
            PastCycleInput(startDate = LocalDate.of(2026, 5, 3), cycleLength = 32, periodDuration = 6)
        )

        val expectedAvgCycleLength = (28 + 30 + 32) / 3 // = 30
        val expectedAvgPeriodLength = (4 + 5 + 6) / 3 // = 5

        val seedResult = cycleRepository.seedManualHistoricalCycles(
            userId = testUserId,
            currentCycleStartDate = startDate,
            pastCycles = pastCycles,
            currentPeriodDuration = expectedAvgPeriodLength
        )
        assertTrue(seedResult is com.nebulatech.lumi.core.domain.Result.Success)

        // 1. Verify average cycle length across the 3 historical cycles
        val avgCycleLength = cycleRepository.getAverageCycleLength(testUserId).first()
        assertEquals(30, avgCycleLength)

        // 2. Verify average period flow length across the 3 historical cycles
        val avgPeriodLength = cycleRepository.getAveragePeriodLength(testUserId).first()
        assertEquals(5, avgPeriodLength)

        // 3. Verify that 3 past cycles + 1 current cycle were created
        val allCycles = cycleRepository.getAllCycles(testUserId).first()
        assertEquals(4, allCycles.size)

        val activeCycle = allCycles.first { it.isCurrent }
        assertEquals("2026-08-01", activeCycle.startDate)
        assertEquals(5, activeCycle.periodLength)
    }

    // ── SCENARIO 3: Dynamic Flow Logging and Subsequent Cycle Recalculation ────
    @Test
    fun `scenario 3 - logging flow in current cycle and starting next cycle recalculates averages`() = runBlocking {
        val cycle1Start = LocalDate.of(2026, 8, 1)
        val now = "2026-08-01T00:00:00Z"

        // Seed with 1 past baseline cycle (30 days cycle, 5 days flow)
        cycleRepository.seedHistoricalCycles(
            userId = testUserId,
            currentCycleStartDate = cycle1Start,
            cycleLength = 30,
            periodDuration = 5,
            numberOfPastCycles = 1
        )

        // Initial averages before logging:
        assertEquals(30, cycleRepository.getAverageCycleLength(testUserId).first())
        assertEquals(5, cycleRepository.getAveragePeriodLength(testUserId).first())

        val currentCycle = cycleRepository.getCurrentCycle(testUserId).first()
        assertNotNull(currentCycle)

        // User logs flow on 3 consecutive days in Cycle 1
        fakeDailyLogDao.insertOrUpdate(
            DailyLogEntity(
                id = "log-1",
                userId = testUserId,
                cycleId = currentCycle!!.id,
                logDate = "2026-08-01",
                flowIntensity = FlowIntensityType.HEAVY.name,
                createdAt = now,
                updatedAt = now
            )
        )
        fakeDailyLogDao.insertOrUpdate(
            DailyLogEntity(
                id = "log-2",
                userId = testUserId,
                cycleId = currentCycle.id,
                logDate = "2026-08-02",
                flowIntensity = FlowIntensityType.MEDIUM.name,
                createdAt = now,
                updatedAt = now
            )
        )
        fakeDailyLogDao.insertOrUpdate(
            DailyLogEntity(
                id = "log-3",
                userId = testUserId,
                cycleId = currentCycle.id,
                logDate = "2026-08-03",
                flowIntensity = FlowIntensityType.LIGHT.name,
                createdAt = now,
                updatedAt = now
            )
        )

        // 26 days later (Aug 27), user starts a new cycle (Cycle 2)
        val cycle2Start = LocalDate.of(2026, 8, 27)
        val startResult = cycleRepository.startNewCycle(testUserId, cycle2Start)
        assertTrue(startResult is com.nebulatech.lumi.core.domain.Result.Success)

        // Verify Cycle 1 was closed with:
        // - cycleLength = 26 days (from Aug 1 to Aug 26)
        // - periodLength = 3 days (actual flow logs logged in Cycle 1)
        val allCycles = cycleRepository.getAllCycles(testUserId).first()
        val closedCycle1 = allCycles.first { it.id == currentCycle.id }
        assertEquals(false, closedCycle1.isCurrent)
        assertEquals(26, closedCycle1.cycleLength)
        assertEquals(3, closedCycle1.periodLength)

        // Now in Cycle 2, averages should be updated with the newly completed cycle:
        // Cycle Lengths: [30 (baseline), 26 (cycle 1)] -> Average = (30 + 26) / 2 = 28 days
        val newAvgCycleLength = cycleRepository.getAverageCycleLength(testUserId).first()
        assertEquals(28, newAvgCycleLength)

        // Flow Lengths: [5 (baseline), 3 (cycle 1)] -> Average = (5 + 3) / 2 = 4 days
        val newAvgPeriodLength = cycleRepository.getAveragePeriodLength(testUserId).first()
        assertEquals(4, newAvgPeriodLength)
    }

    // ── Phase Calculation Companion Test ──────────────────────────────────────
    @Test
    fun `calculatePhase boundaries are mathematically precise`() {
        val cycleLength = 28
        val periodLength = 5
        // ovulationDay = 28 - 14 = 14
        // fertileStart = maxOf(14 - 3, 5 + 1) = 11
        // fertileEnd = 14 + 1 = 15
        // lateLutealStart = 28 - 6 = 22

        assertEquals(CyclePhase.MENSTRUATION, RoomCycleRepository.calculatePhase(1, cycleLength, periodLength))
        assertEquals(CyclePhase.MENSTRUATION, RoomCycleRepository.calculatePhase(5, cycleLength, periodLength))
        assertEquals(CyclePhase.FOLLICULAR, RoomCycleRepository.calculatePhase(6, cycleLength, periodLength))
        assertEquals(CyclePhase.FOLLICULAR, RoomCycleRepository.calculatePhase(10, cycleLength, periodLength))
        assertEquals(CyclePhase.FERTILE_WINDOW, RoomCycleRepository.calculatePhase(11, cycleLength, periodLength))
        assertEquals(CyclePhase.FERTILE_WINDOW, RoomCycleRepository.calculatePhase(15, cycleLength, periodLength))
        assertEquals(CyclePhase.LUTEAL, RoomCycleRepository.calculatePhase(16, cycleLength, periodLength))
        assertEquals(CyclePhase.LUTEAL, RoomCycleRepository.calculatePhase(21, cycleLength, periodLength))
        assertEquals(CyclePhase.LATE_LUTEAL, RoomCycleRepository.calculatePhase(22, cycleLength, periodLength))
        assertEquals(CyclePhase.LATE_LUTEAL, RoomCycleRepository.calculatePhase(28, cycleLength, periodLength))
        assertEquals(CyclePhase.PERIOD_PREDICTED, RoomCycleRepository.calculatePhase(29, cycleLength, periodLength))
    }

    // ── Fakes ─────────────────────────────────────────────────────────────────

    private class FakeCycleDao : CycleDao {
        private val cycles = MutableStateFlow<List<CycleEntity>>(emptyList())

        override suspend fun insertOrUpdate(cycle: CycleEntity) {
            val current = cycles.value.toMutableList()
            val index = current.indexOfFirst { it.id == cycle.id }
            if (index >= 0) {
                current[index] = cycle
            } else {
                current.add(cycle)
            }
            cycles.value = current
        }

        override suspend fun update(cycle: CycleEntity) {
            insertOrUpdate(cycle)
        }

        override fun getCurrentCycleFlow(userId: String): Flow<CycleEntity?> {
            return cycles.map { list ->
                list.filter { it.userId == userId && it.isCurrent }
                    .maxByOrNull { it.startDate }
            }
        }

        override suspend fun getCurrentCycle(userId: String): CycleEntity? {
            return cycles.value.filter { it.userId == userId && it.isCurrent }
                .maxByOrNull { it.startDate }
        }

        override suspend fun getCycleById(cycleId: String): CycleEntity? {
            return cycles.value.firstOrNull { it.id == cycleId }
        }

        override fun getCycleByIdFlow(cycleId: String): Flow<CycleEntity?> {
            return cycles.map { list -> list.firstOrNull { it.id == cycleId } }
        }

        override fun getLastNCyclesFlow(userId: String, n: Int): Flow<List<CycleEntity>> {
            return cycles.map { list ->
                list.filter { it.userId == userId }
                    .sortedByDescending { it.startDate }
                    .take(n)
            }
        }

        override suspend fun getLastNCycles(userId: String, n: Int): List<CycleEntity> {
            return cycles.value.filter { it.userId == userId }
                .sortedByDescending { it.startDate }
                .take(n)
        }

        override fun getAllCyclesFlow(userId: String): Flow<List<CycleEntity>> {
            return cycles.map { list ->
                list.filter { it.userId == userId }
                    .sortedByDescending { it.startDate }
            }
        }

        override suspend fun getAllCycles(userId: String): List<CycleEntity> {
            return cycles.value.filter { it.userId == userId }
                .sortedByDescending { it.startDate }
        }

        override suspend fun markAllNotCurrent(userId: String, updatedAt: String): Int {
            var count = 0
            val updated = cycles.value.map {
                if (it.userId == userId && it.isCurrent) {
                    count++
                    it.copy(isCurrent = false, updatedAt = updatedAt)
                } else it
            }
            cycles.value = updated
            return count
        }

        override suspend fun closeCycle(
            cycleId: String,
            endDate: String,
            cycleLength: Int,
            periodLength: Int?,
            isRegular: Boolean?,
            updatedAt: String
        ): Int {
            val current = cycles.value.toMutableList()
            val index = current.indexOfFirst { it.id == cycleId }
            if (index >= 0) {
                current[index] = current[index].copy(
                    endDate = endDate,
                    cycleLength = cycleLength,
                    periodLength = periodLength,
                    isCurrent = false,
                    isRegular = isRegular,
                    updatedAt = updatedAt
                )
                cycles.value = current
                return 1
            }
            return 0
        }

        override suspend fun deleteCycle(cycleId: String): Int {
            val initialSize = cycles.value.size
            cycles.value = cycles.value.filterNot { it.id == cycleId }
            return initialSize - cycles.value.size
        }
    }

    private class FakeUserProfileDao : UserProfileDao {
        private val profiles = MutableStateFlow<Map<String, UserProfileEntity>>(emptyMap())

        override suspend fun insertOrUpdate(profile: UserProfileEntity) {
            profiles.value = profiles.value + (profile.userId to profile)
        }

        override fun getProfileFlow(userId: String): Flow<UserProfileEntity?> {
            return profiles.map { it[userId] }
        }

        override suspend fun getProfile(userId: String): UserProfileEntity? {
            return profiles.value[userId]
        }

        override suspend fun updateCycleSettings(
            userId: String,
            cycleLength: Int,
            periodDuration: Int,
            primaryGoal: String,
            updatedAt: String
        ): Int {
            val p = profiles.value[userId] ?: return 0
            profiles.value = profiles.value + (userId to p.copy(
                cycleLength = cycleLength,
                periodDuration = periodDuration,
                primaryGoal = primaryGoal,
                updatedAt = updatedAt
            ))
            return 1
        }

        override suspend fun updateNotifications(userId: String, enabled: Boolean, updatedAt: String): Int {
            val p = profiles.value[userId] ?: return 0
            profiles.value = profiles.value + (userId to p.copy(notificationsEnabled = enabled, updatedAt = updatedAt))
            return 1
        }

        override suspend fun deleteProfile(userId: String): Int {
            val had = profiles.value.containsKey(userId)
            profiles.value = profiles.value - userId
            return if (had) 1 else 0
        }
    }

    private class FakeDailyLogDao : DailyLogDao {
        private val logs = MutableStateFlow<List<DailyLogEntity>>(emptyList())

        override suspend fun insertOrUpdate(log: DailyLogEntity) {
            val current = logs.value.toMutableList()
            val index = current.indexOfFirst { it.id == log.id }
            if (index >= 0) {
                current[index] = log
            } else {
                current.add(log)
            }
            logs.value = current
        }

        override suspend fun getLogForDate(userId: String, date: String): DailyLogEntity? {
            return logs.value.firstOrNull { it.userId == userId && it.logDate == date }
        }

        override fun getLogForDateFlow(userId: String, date: String): Flow<DailyLogEntity?> {
            return logs.map { list -> list.firstOrNull { it.userId == userId && it.logDate == date } }
        }

        override fun getLogsInRangeFlow(userId: String, from: String, to: String): Flow<List<DailyLogEntity>> {
            return logs.map { list ->
                list.filter { it.userId == userId && it.logDate in from..to }
            }
        }

        override suspend fun getLogsInRange(userId: String, from: String, to: String): List<DailyLogEntity> {
            return logs.value.filter { it.userId == userId && it.logDate in from..to }
        }

        override fun getLogsForCycleFlow(cycleId: String): Flow<List<DailyLogEntity>> {
            return logs.map { list -> list.filter { it.cycleId == cycleId } }
        }

        override suspend fun getLogsForCycle(cycleId: String): List<DailyLogEntity> {
            return logs.value.filter { it.cycleId == cycleId }
        }

        override suspend fun deleteLog(logId: String): Int {
            val initial = logs.value.size
            logs.value = logs.value.filterNot { it.id == logId }
            return initial - logs.value.size
        }
    }
}
