package com.nebulatech.lumi.data.repository

import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.EmptyResult
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.local.dao.DailyLogDao
import com.nebulatech.lumi.data.local.dao.DailySymptomDao
import com.nebulatech.lumi.data.local.entity.DailySymptomEntity
import com.nebulatech.lumi.data.mapper.toDailyLog
import com.nebulatech.lumi.data.mapper.toDailyLogEntity
import com.nebulatech.lumi.data.model.CyclePhase
import com.nebulatech.lumi.data.model.DailyLog
import com.nebulatech.lumi.data.model.FlowIntensityType
import com.nebulatech.lumi.data.model.MoodType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

interface DailyLogRepository {
    fun getLogForDate(userId: String, date: LocalDate): Flow<DailyLog?>
    suspend fun getOrCreateLogForDate(
        userId: String,
        date: LocalDate,
        cycleId: String? = null,
        cycleDay: Int? = null,
        cyclePhase: CyclePhase? = null
    ): Result<DailyLog, DataError.Local>
    suspend fun saveFlowLog(
        userId: String,
        date: LocalDate,
        flowIntensity: FlowIntensityType?,
        mood: MoodType?,
        symptoms: Set<String>,
        cycleId: String? = null,
        cycleDay: Int? = null,
        cyclePhase: CyclePhase? = null
    ): EmptyResult<DataError.Local>
    fun getLogsInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<DailyLog>>
    suspend fun getSymptomFrequency(userId: String, days: Int = 30): Result<Map<String, Int>, DataError.Local>
}

@OptIn(ExperimentalCoroutinesApi::class)
class RoomDailyLogRepository(
    private val dailyLogDao: DailyLogDao,
    private val dailySymptomDao: DailySymptomDao
) : DailyLogRepository {

    override fun getLogForDate(userId: String, date: LocalDate): Flow<DailyLog?> {
        val dateString = date.toString()
        return dailyLogDao.getLogForDateFlow(userId, dateString).flatMapLatest { logEntity ->
            if (logEntity != null) {
                dailySymptomDao.getSymptomsForLogFlow(logEntity.id).map { symptomEntities ->
                    logEntity.toDailyLog(symptomEntities)
                }
            } else {
                flowOf(null)
            }
        }
    }

    override suspend fun getOrCreateLogForDate(
        userId: String,
        date: LocalDate,
        cycleId: String?,
        cycleDay: Int?,
        cyclePhase: CyclePhase?
    ): Result<DailyLog, DataError.Local> {
        return try {
            val dateString = date.toString()
            val existing = dailyLogDao.getLogForDate(userId, dateString)
            if (existing != null) {
                val symptoms = dailySymptomDao.getSymptomsForLog(existing.id)
                Result.Success(existing.toDailyLog(symptoms))
            } else {
                val now = Instant.now().toString()
                val newLog = DailyLog(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    cycleId = cycleId,
                    logDate = dateString,
                    cycleDay = cycleDay,
                    cyclePhase = cyclePhase,
                    flowIntensity = null,
                    mood = null,
                    notes = null,
                    createdAt = now,
                    updatedAt = now
                )
                dailyLogDao.insertOrUpdate(newLog.toDailyLogEntity())
                Result.Success(newLog)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

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
        return try {
            val dateString = date.toString()
            val existing = dailyLogDao.getLogForDate(userId, dateString)
            val logId = existing?.id ?: UUID.randomUUID().toString()
            val now = Instant.now().toString()

            val log = DailyLog(
                id = logId,
                userId = userId,
                cycleId = cycleId ?: existing?.cycleId,
                logDate = dateString,
                cycleDay = cycleDay ?: existing?.cycleDay,
                cyclePhase = cyclePhase ?: existing?.cyclePhase?.let { runCatching { CyclePhase.valueOf(it) }.getOrNull() },
                flowIntensity = flowIntensity,
                mood = mood,
                notes = existing?.notes,
                createdAt = existing?.createdAt ?: now,
                updatedAt = now,
                isSynced = false
            )
            dailyLogDao.insertOrUpdate(log.toDailyLogEntity())

            dailySymptomDao.deleteSymptomsForLog(logId)
            val symptomEntities = symptoms.map { symptomName ->
                DailySymptomEntity(
                    id = UUID.randomUUID().toString(),
                    dailyLogId = logId,
                    userId = userId,
                    logDate = dateString,
                    cycleId = log.cycleId,
                    cycleDay = log.cycleDay,
                    symptomKey = symptomName.uppercase().replace(" ", "_"),
                    symptomDisplayName = symptomName,
                    isCustom = false,
                    customSymptomId = null,
                    createdAt = now
                )
            }
            if (symptomEntities.isNotEmpty()) {
                dailySymptomDao.insertAll(symptomEntities)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getLogsInRange(userId: String, fromDate: LocalDate, toDate: LocalDate): Flow<List<DailyLog>> {
        return dailyLogDao.getLogsInRangeFlow(userId, fromDate.toString(), toDate.toString()).map { list ->
            list.map { it.toDailyLog() }
        }
    }

    override suspend fun getSymptomFrequency(userId: String, days: Int): Result<Map<String, Int>, DataError.Local> {
        return try {
            val to = LocalDate.now().toString()
            val from = LocalDate.now().minusDays(days.toLong()).toString()
            val frequencies = dailySymptomDao.getSymptomFrequency(userId, from, to)
            val resultMap = frequencies.associate { it.symptomKey to it.count }
            Result.Success(resultMap)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
