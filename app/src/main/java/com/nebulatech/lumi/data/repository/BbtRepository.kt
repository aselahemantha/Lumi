package com.nebulatech.lumi.data.repository

import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.local.dao.BbtReadingDao
import com.nebulatech.lumi.data.mapper.toBbtReading
import com.nebulatech.lumi.data.mapper.toBbtReadingEntity
import com.nebulatech.lumi.data.model.BbtReading
import com.nebulatech.lumi.data.model.BbtSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

interface BbtRepository {
    fun getBbtForDate(userId: String, date: LocalDate): Flow<BbtReading?>
    fun getBbtForCycle(cycleId: String): Flow<List<BbtReading>>
    fun getBbtInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<BbtReading>>
    suspend fun saveBbtReading(
        userId: String,
        date: LocalDate,
        temperature: Double,
        temperatureUnit: String = "F",
        readingTime: String? = null,
        cycleId: String? = null,
        cycleDay: Int? = null,
        disturbedSleep: Boolean = false,
        feverIllness: Boolean = false,
        source: BbtSource = BbtSource.MANUAL
    ): Result<BbtReading, DataError.Local>
}

class RoomBbtRepository(
    private val bbtReadingDao: BbtReadingDao
) : BbtRepository {

    override fun getBbtForDate(userId: String, date: LocalDate): Flow<BbtReading?> {
        return bbtReadingDao.getReadingForDateFlow(userId, date.toString()).map { it?.toBbtReading() }
    }

    override fun getBbtForCycle(cycleId: String): Flow<List<BbtReading>> {
        return bbtReadingDao.getBbtForCycleFlow(cycleId).map { list -> list.map { it.toBbtReading() } }
    }

    override fun getBbtInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<BbtReading>> {
        return bbtReadingDao.getBbtInRangeFlow(userId, fromDate.toString(), toDate.toString()).map { list ->
            list.map { it.toBbtReading() }
        }
    }

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
    ): Result<BbtReading, DataError.Local> {
        return try {
            val dateString = date.toString()
            val existing = bbtReadingDao.getReadingForDate(userId, dateString)
            val now = Instant.now().toString()
            val reading = BbtReading(
                id = existing?.id ?: UUID.randomUUID().toString(),
                userId = userId,
                cycleId = cycleId ?: existing?.cycleId,
                dailyLogId = existing?.dailyLogId,
                readingDate = dateString,
                readingTime = readingTime ?: existing?.readingTime,
                temperature = temperature,
                temperatureUnit = temperatureUnit,
                cycleDay = cycleDay ?: existing?.cycleDay,
                disturbedSleep = disturbedSleep,
                feverIllness = feverIllness,
                source = source,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
                isSynced = false
            )
            bbtReadingDao.insertOrUpdate(reading.toBbtReadingEntity())
            Result.Success(reading)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
