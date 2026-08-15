package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.DailyLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DailyLogEntity)

    @Query("SELECT * FROM daily_logs WHERE userId = :userId AND logDate = :date LIMIT 1")
    suspend fun getLogForDate(userId: String, date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_logs WHERE userId = :userId AND logDate = :date LIMIT 1")
    fun getLogForDateFlow(userId: String, date: String): Flow<DailyLogEntity?>

    @Query("SELECT * FROM daily_logs WHERE userId = :userId AND logDate BETWEEN :from AND :to ORDER BY logDate ASC")
    fun getLogsInRangeFlow(userId: String, from: String, to: String): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE userId = :userId AND logDate BETWEEN :from AND :to ORDER BY logDate ASC")
    suspend fun getLogsInRange(userId: String, from: String, to: String): List<DailyLogEntity>

    @Query("SELECT * FROM daily_logs WHERE cycleId = :cycleId ORDER BY logDate ASC")
    fun getLogsForCycleFlow(cycleId: String): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE cycleId = :cycleId ORDER BY logDate ASC")
    suspend fun getLogsForCycle(cycleId: String): List<DailyLogEntity>

    @Query("DELETE FROM daily_logs WHERE id = :logId")
    suspend fun deleteLog(logId: String): Int
}
