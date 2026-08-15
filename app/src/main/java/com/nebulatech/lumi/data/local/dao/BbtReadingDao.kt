package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.BbtReadingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BbtReadingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(reading: BbtReadingEntity)

    @Query("SELECT * FROM bbt_readings WHERE userId = :userId AND readingDate = :date LIMIT 1")
    suspend fun getReadingForDate(userId: String, date: String): BbtReadingEntity?

    @Query("SELECT * FROM bbt_readings WHERE userId = :userId AND readingDate = :date LIMIT 1")
    fun getReadingForDateFlow(userId: String, date: String): Flow<BbtReadingEntity?>

    @Query("SELECT * FROM bbt_readings WHERE cycleId = :cycleId ORDER BY readingDate ASC")
    fun getBbtForCycleFlow(cycleId: String): Flow<List<BbtReadingEntity>>

    @Query("SELECT * FROM bbt_readings WHERE cycleId = :cycleId ORDER BY readingDate ASC")
    suspend fun getBbtForCycle(cycleId: String): List<BbtReadingEntity>

    @Query("SELECT * FROM bbt_readings WHERE userId = :userId AND readingDate BETWEEN :from AND :to ORDER BY readingDate ASC")
    fun getBbtInRangeFlow(userId: String, from: String, to: String): Flow<List<BbtReadingEntity>>

    @Query("DELETE FROM bbt_readings WHERE id = :id")
    suspend fun deleteReading(id: String): Int
}
