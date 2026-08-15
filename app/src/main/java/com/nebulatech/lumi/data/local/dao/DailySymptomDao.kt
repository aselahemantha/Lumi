package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.DailySymptomEntity
import kotlinx.coroutines.flow.Flow

data class SymptomFrequency(
    val symptomKey: String,
    val count: Int
)

@Dao
interface DailySymptomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(symptoms: List<DailySymptomEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(symptom: DailySymptomEntity)

    @Query("SELECT * FROM daily_symptoms WHERE dailyLogId = :dailyLogId")
    fun getSymptomsForLogFlow(dailyLogId: String): Flow<List<DailySymptomEntity>>

    @Query("SELECT * FROM daily_symptoms WHERE dailyLogId = :dailyLogId")
    suspend fun getSymptomsForLog(dailyLogId: String): List<DailySymptomEntity>

    @Query("SELECT * FROM daily_symptoms WHERE userId = :userId AND logDate BETWEEN :from AND :to ORDER BY logDate ASC")
    fun getSymptomsInRangeFlow(userId: String, from: String, to: String): Flow<List<DailySymptomEntity>>

    @Query("SELECT * FROM daily_symptoms WHERE userId = :userId AND logDate BETWEEN :from AND :to ORDER BY logDate ASC")
    suspend fun getSymptomsInRange(userId: String, from: String, to: String): List<DailySymptomEntity>

    @Query("SELECT symptomKey, COUNT(*) as count FROM daily_symptoms WHERE userId = :userId AND logDate BETWEEN :from AND :to GROUP BY symptomKey ORDER BY count DESC")
    suspend fun getSymptomFrequency(userId: String, from: String, to: String): List<SymptomFrequency>

    @Query("DELETE FROM daily_symptoms WHERE dailyLogId = :dailyLogId")
    suspend fun deleteSymptomsForLog(dailyLogId: String): Int
}
