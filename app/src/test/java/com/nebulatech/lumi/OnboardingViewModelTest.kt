package com.nebulatech.lumi

import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.EmptyResult
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.model.Cycle
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.PastCycleInput
import com.nebulatech.lumi.data.model.User
import com.nebulatech.lumi.data.model.UserProfile
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.UserRepository
import com.nebulatech.lumi.onboarding.ManualPastCycle
import com.nebulatech.lumi.onboarding.OnboardingAction
import com.nebulatech.lumi.onboarding.OnboardingGoal
import com.nebulatech.lumi.onboarding.OnboardingViewModel
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeCycleRepository: FakeCycleRepository
    private lateinit var viewModel: OnboardingViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeUserRepository = FakeUserRepository()
        fakeCycleRepository = FakeCycleRepository()
        viewModel = OnboardingViewModel(fakeUserRepository, fakeCycleRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state defaults to 28 days cycle length and 5 days period duration`() {
        val state = viewModel.state.value
        assertEquals(28, state.cycleLength)
        assertEquals(5, state.periodDuration)
        assertNull(state.customPastCycles)
    }

    @Test
    fun `UpdateCustomPastCycles automatically adjusts cycle length and period duration based on past cycles`() {
        // User enters 3 past cycles with 30, 31, and 29 days length (average 30)
        // and 6, 5, 7 days flow (average 6)
        val pastCycles = listOf(
            ManualPastCycle(startDate = LocalDate.of(2026, 7, 2), cycleLength = 30, periodDuration = 6),
            ManualPastCycle(startDate = LocalDate.of(2026, 6, 1), cycleLength = 31, periodDuration = 5),
            ManualPastCycle(startDate = LocalDate.of(2026, 5, 3), cycleLength = 29, periodDuration = 7)
        )

        viewModel.onAction(OnboardingAction.UpdateCustomPastCycles(pastCycles))

        val state = viewModel.state.value
        assertEquals(30, state.cycleLength)
        assertEquals(6, state.periodDuration)
        assertEquals(pastCycles, state.customPastCycles)
    }

    @Test
    fun `UpdateCustomPastCycles with null reverts customPastCycles while retaining state`() {
        val pastCycles = listOf(
            ManualPastCycle(startDate = LocalDate.of(2026, 7, 2), cycleLength = 32, periodDuration = 4),
            ManualPastCycle(startDate = LocalDate.of(2026, 6, 1), cycleLength = 32, periodDuration = 4),
            ManualPastCycle(startDate = LocalDate.of(2026, 4, 30), cycleLength = 32, periodDuration = 4)
        )
        viewModel.onAction(OnboardingAction.UpdateCustomPastCycles(pastCycles))
        assertEquals(32, viewModel.state.value.cycleLength)

        // User clicks "Auto-calculate instead"
        viewModel.onAction(OnboardingAction.UpdateCustomPastCycles(null))
        assertNull(viewModel.state.value.customPastCycles)
    }

    @Test
    fun `persistAndNavigate saves profile with auto-adjusted cycle length and seeds manual cycles`() = runTest {
        val pastCycles = listOf(
            ManualPastCycle(startDate = LocalDate.of(2026, 7, 2), cycleLength = 31, periodDuration = 6),
            ManualPastCycle(startDate = LocalDate.of(2026, 6, 1), cycleLength = 30, periodDuration = 6),
            ManualPastCycle(startDate = LocalDate.of(2026, 5, 2), cycleLength = 30, periodDuration = 6)
        )

        viewModel.onAction(OnboardingAction.SelectGoal(OnboardingGoal.TRACK_CYCLE))
        viewModel.onAction(OnboardingAction.UpdateFirstDayOfLastPeriod(LocalDate.of(2026, 8, 2)))
        viewModel.onAction(OnboardingAction.UpdateCustomPastCycles(pastCycles))

        // Navigate through steps to completion
        viewModel.onAction(OnboardingAction.ClickContinue) // WELCOME -> SELECT_GOAL
        viewModel.onAction(OnboardingAction.ClickContinue) // SELECT_GOAL -> CORE_DATA
        viewModel.onAction(OnboardingAction.ClickContinue) // CORE_DATA -> HEALTH_PROFILE
        viewModel.onAction(OnboardingAction.ClickContinue) // HEALTH_PROFILE -> persistAndNavigate

        advanceUntilIdle()

        // Verify profile was saved with average cycle length = 30
        assertNotNull(fakeUserRepository.savedProfile)
        assertEquals(30, fakeUserRepository.savedProfile?.cycleLength)
        assertEquals(6, fakeUserRepository.savedProfile?.periodDuration)

        // Verify manual past cycles were seeded in repository
        assertEquals(3, fakeCycleRepository.seededManualPastCycles?.size)
        assertEquals(6, fakeCycleRepository.seededCurrentPeriodDuration)
    }

    // ── Test Fakes ────────────────────────────────────────────────────────────

    private class FakeUserRepository : UserRepository {
        var savedProfile: UserProfile? = null

        override fun getCurrentUser(): Flow<User?> = flowOf(User(id = "user-1", name = "Test User", createdAt = "now", updatedAt = "now"))
        override suspend fun getOrCreateUser(name: String): Result<User, DataError.Local> =
            Result.Success(User(id = "user-1", name = name, createdAt = "now", updatedAt = "now"))
        override suspend fun getUserProfile(userId: String): Result<UserProfile?, DataError.Local> =
            Result.Success(savedProfile)
        override fun getUserProfileFlow(userId: String): Flow<UserProfile?> = flowOf(savedProfile)
        override suspend fun saveUserProfile(profile: UserProfile): EmptyResult<DataError.Local> {
            savedProfile = profile
            return Result.Success(Unit)
        }
        override suspend fun updateEmailAndAuth(userId: String, email: String, supabaseUid: String): EmptyResult<DataError.Local> =
            Result.Success(Unit)
        override suspend fun clearAllData(): EmptyResult<DataError.Local> = Result.Success(Unit)
    }

    private class FakeCycleRepository : CycleRepository {
        var seededManualPastCycles: List<PastCycleInput>? = null
        var seededCurrentPeriodDuration: Int? = null

        override suspend fun seedManualHistoricalCycles(
            userId: String,
            currentCycleStartDate: LocalDate,
            pastCycles: List<PastCycleInput>,
            currentPeriodDuration: Int
        ): EmptyResult<DataError.Local> {
            seededManualPastCycles = pastCycles
            seededCurrentPeriodDuration = currentPeriodDuration
            return Result.Success(Unit)
        }

        override suspend fun seedHistoricalCycles(
            userId: String,
            currentCycleStartDate: LocalDate,
            cycleLength: Int,
            periodDuration: Int,
            numberOfPastCycles: Int
        ): EmptyResult<DataError.Local> = Result.Success(Unit)

        override fun getAllCycles(userId: String): Flow<List<Cycle>> = flowOf(emptyList())
        override fun getCurrentCycle(userId: String): Flow<Cycle?> = flowOf(null)
        override fun getLastNCycles(userId: String, n: Int): Flow<List<Cycle>> = flowOf(emptyList())
        override suspend fun startNewCycle(userId: String, startDate: LocalDate): Result<Cycle, DataError.Local> =
            Result.Error(DataError.Local.UNKNOWN)
        override suspend fun updateCycle(cycle: Cycle): EmptyResult<DataError.Local> = Result.Success(Unit)
        override fun getCycleDay(userId: String, targetDate: LocalDate): Flow<Int> = flowOf(1)
        override fun getAverageCycleLength(userId: String): Flow<Int> = flowOf(28)
        override fun getAveragePeriodLength(userId: String): Flow<Int> = flowOf(5)
        override fun getCurrentPhase(userId: String, targetDate: LocalDate): Flow<CyclePhase> =
            flowOf(CyclePhase.FOLLICULAR)
    }
}
