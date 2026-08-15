package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.LhTestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LhTestDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(test: LhTestEntity)

    @Query("SELECT * FROM lh_tests WHERE userId = :userId AND testDate = :date LIMIT 1")
    suspend fun getTestForDate(userId: String, date: String): LhTestEntity?

    @Query("SELECT * FROM lh_tests WHERE userId = :userId AND testDate = :date LIMIT 1")
    fun getTestForDateFlow(userId: String, date: String): Flow<LhTestEntity?>

    @Query("SELECT * FROM lh_tests WHERE cycleId = :cycleId ORDER BY testDate ASC")
    fun getLhTestsForCycleFlow(cycleId: String): Flow<List<LhTestEntity>>

    @Query("SELECT * FROM lh_tests WHERE cycleId = :cycleId ORDER BY testDate ASC")
    suspend fun getLhTestsForCycle(cycleId: String): List<LhTestEntity>

    @Query("SELECT * FROM lh_tests WHERE userId = :userId AND testDate BETWEEN :from AND :to ORDER BY testDate ASC")
    fun getLhTestsInRangeFlow(userId: String, from: String, to: String): Flow<List<LhTestEntity>>

    @Query("DELETE FROM lh_tests WHERE id = :id")
    suspend fun deleteTest(id: String): Int
}
