package com.nebulatech.lumi.data.repository

import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.EmptyResult
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.local.dao.CycleDao
import com.nebulatech.lumi.data.local.dao.UserProfileDao
import com.nebulatech.lumi.data.mapper.toCycle
import com.nebulatech.lumi.data.mapper.toCycleEntity
import com.nebulatech.lumi.data.model.Cycle
import com.nebulatech.lumi.data.model.CyclePhase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.UUID

interface CycleRepository {
    fun getCurrentCycle(userId: String): Flow<Cycle?>
    fun getAllCycles(userId: String): Flow<List<Cycle>>
    fun getLastNCycles(userId: String, n: Int = 6): Flow<List<Cycle>>
    suspend fun startNewCycle(userId: String, startDate: LocalDate): Result<Cycle, DataError.Local>
    suspend fun seedHistoricalCycles(
        userId: String,
        currentCycleStartDate: LocalDate,
        cycleLength: Int,
        periodDuration: Int,
        numberOfPastCycles: Int = 3
    ): EmptyResult<DataError.Local>
    suspend fun updateCycle(cycle: Cycle): EmptyResult<DataError.Local>
    fun getCycleDay(userId: String, targetDate: LocalDate = LocalDate.now()): Flow<Int>
    fun getAverageCycleLength(userId: String): Flow<Int>
    fun getAveragePeriodLength(userId: String): Flow<Int>
    fun getCurrentPhase(userId: String, targetDate: LocalDate = LocalDate.now()): Flow<CyclePhase>
}

class RoomCycleRepository(
    private val cycleDao: CycleDao,
    private val userProfileDao: UserProfileDao
) : CycleRepository {

    override fun getCurrentCycle(userId: String): Flow<Cycle?> {
        return cycleDao.getCurrentCycleFlow(userId).map { it?.toCycle() }
    }

    override fun getAllCycles(userId: String): Flow<List<Cycle>> {
        return cycleDao.getAllCyclesFlow(userId).map { list -> list.map { it.toCycle() } }
    }

    override fun getLastNCycles(userId: String, n: Int): Flow<List<Cycle>> {
        return cycleDao.getLastNCyclesFlow(userId, n).map { list -> list.map { it.toCycle() } }
    }

    override suspend fun startNewCycle(userId: String, startDate: LocalDate): Result<Cycle, DataError.Local> {
        return try {
            val now = Instant.now().toString()
            val currentCycle = cycleDao.getCurrentCycle(userId)
            
            if (currentCycle != null) {
                val cycleStartDate = LocalDate.parse(currentCycle.startDate)
                val cycleLength = ChronoUnit.DAYS.between(cycleStartDate, startDate).toInt()
                val prevEndDate = startDate.minusDays(1).toString()
                cycleDao.closeCycle(
                    cycleId = currentCycle.id,
                    endDate = prevEndDate,
                    cycleLength = maxOf(cycleLength, 1),
                    periodLength = currentCycle.periodLength ?: 5,
                    isRegular = true,
                    updatedAt = now
                )
            } else {
                cycleDao.markAllNotCurrent(userId, now)
            }

            val cycleCount = cycleDao.getAllCycles(userId).size
            val newCycle = Cycle(
                id = UUID.randomUUID().toString(),
                userId = userId,
                cycleNumber = cycleCount + 1,
                startDate = startDate.toString(),
                endDate = null,
                cycleLength = null,
                periodLength = null,
                ovulationDate = null,
                isCurrent = true,
                isRegular = null,
                notes = null,
                createdAt = now,
                updatedAt = now
            )
            cycleDao.insertOrUpdate(newCycle.toCycleEntity())
            Result.Success(newCycle)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun seedHistoricalCycles(
        userId: String,
        currentCycleStartDate: LocalDate,
        cycleLength: Int,
        periodDuration: Int,
        numberOfPastCycles: Int
    ): EmptyResult<DataError.Local> {
        return try {
            val now = Instant.now().toString()
            val safeCycleLength = if (cycleLength in 20..45) cycleLength else 28
            val safePeriodLength = if (periodDuration in 2..10) periodDuration else 5

            // Generate past completed cycles backwards
            for (i in numberOfPastCycles downTo 1) {
                val cycleNum = numberOfPastCycles - i + 1
                val cycleStart = currentCycleStartDate.minusDays((safeCycleLength * i).toLong())
                val cycleEnd = currentCycleStartDate.minusDays((safeCycleLength * (i - 1) + 1).toLong())
                val ovulationDay = maxOf(safeCycleLength - 14, safePeriodLength + 1)
                val ovulationDate = cycleStart.plusDays(ovulationDay.toLong()).toString()

                val historicalCycle = Cycle(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    cycleNumber = cycleNum,
                    startDate = cycleStart.toString(),
                    endDate = cycleEnd.toString(),
                    cycleLength = safeCycleLength,
                    periodLength = safePeriodLength,
                    ovulationDate = ovulationDate,
                    isCurrent = false,
                    isRegular = true,
                    notes = "Historical baseline cycle",
                    createdAt = now,
                    updatedAt = now
                )
                cycleDao.insertOrUpdate(historicalCycle.toCycleEntity())
            }

            // Create current active cycle with correct cycleNumber
            val currentCycle = Cycle(
                id = UUID.randomUUID().toString(),
                userId = userId,
                cycleNumber = numberOfPastCycles + 1,
                startDate = currentCycleStartDate.toString(),
                endDate = null,
                cycleLength = null,
                periodLength = safePeriodLength,
                ovulationDate = null,
                isCurrent = true,
                isRegular = true,
                notes = null,
                createdAt = now,
                updatedAt = now
            )
            cycleDao.markAllNotCurrent(userId, now)
            cycleDao.insertOrUpdate(currentCycle.toCycleEntity())

            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun updateCycle(cycle: Cycle): EmptyResult<DataError.Local> {
        return try {
            cycleDao.insertOrUpdate(cycle.copy(updatedAt = Instant.now().toString()).toCycleEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getCycleDay(userId: String, targetDate: LocalDate): Flow<Int> {
        return getCurrentCycle(userId).map { cycle ->
            if (cycle != null) {
                val startDate = LocalDate.parse(cycle.startDate)
                val days = ChronoUnit.DAYS.between(startDate, targetDate).toInt() + 1
                maxOf(days, 1)
            } else {
                1
            }
        }
    }

    override fun getAverageCycleLength(userId: String): Flow<Int> {
        return combine(
            getLastNCycles(userId, 6),
            userProfileDao.getProfileFlow(userId)
        ) { recentCycles, profile ->
            val completedCycles = recentCycles.filter { it.cycleLength != null && it.cycleLength > 0 }
            if (completedCycles.isNotEmpty()) {
                completedCycles.mapNotNull { it.cycleLength }.average().toInt()
            } else {
                profile?.cycleLength ?: 28
            }
        }
    }

    override fun getAveragePeriodLength(userId: String): Flow<Int> {
        return combine(
            getLastNCycles(userId, 6),
            userProfileDao.getProfileFlow(userId)
        ) { recentCycles, profile ->
            val completedPeriods = recentCycles.filter { it.periodLength != null && it.periodLength > 0 }
            if (completedPeriods.isNotEmpty()) {
                completedPeriods.mapNotNull { it.periodLength }.average().toInt()
            } else {
                profile?.periodDuration ?: 5
            }
        }
    }

    override fun getCurrentPhase(userId: String, targetDate: LocalDate): Flow<CyclePhase> {
        return combine(
            getCycleDay(userId, targetDate),
            getAverageCycleLength(userId),
            getAveragePeriodLength(userId)
        ) { cycleDay, cycleLength, periodLength ->
            calculatePhase(cycleDay, cycleLength, periodLength)
        }
    }

    companion object {
        fun calculatePhase(cycleDay: Int, cycleLength: Int, periodLength: Int): CyclePhase {
            val ovulationDay = cycleLength - 14
            val fertileStart = maxOf(ovulationDay - 3, periodLength + 1)
            val fertileEnd = ovulationDay + 1
            val lateLutealStart = maxOf(cycleLength - 6, fertileEnd + 1)

            return when {
                cycleDay in 1..periodLength -> CyclePhase.MENSTRUATION
                cycleDay in (periodLength + 1) until fertileStart -> CyclePhase.FOLLICULAR
                cycleDay in fertileStart..fertileEnd -> CyclePhase.FERTILE_WINDOW
                cycleDay in (fertileEnd + 1) until lateLutealStart -> CyclePhase.LUTEAL
                else -> CyclePhase.LATE_LUTEAL
            }
        }
    }
}
