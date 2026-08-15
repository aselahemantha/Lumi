package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.nebulatech.lumi.data.local.entity.CycleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CycleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cycle: CycleEntity)

    @Update
    suspend fun update(cycle: CycleEntity)

    @Query("SELECT * FROM cycles WHERE userId = :userId AND isCurrent = 1 ORDER BY startDate DESC LIMIT 1")
    fun getCurrentCycleFlow(userId: String): Flow<CycleEntity?>

    @Query("SELECT * FROM cycles WHERE userId = :userId AND isCurrent = 1 ORDER BY startDate DESC LIMIT 1")
    suspend fun getCurrentCycle(userId: String): CycleEntity?

    @Query("SELECT * FROM cycles WHERE id = :cycleId LIMIT 1")
    suspend fun getCycleById(cycleId: String): CycleEntity?

    @Query("SELECT * FROM cycles WHERE id = :cycleId LIMIT 1")
    fun getCycleByIdFlow(cycleId: String): Flow<CycleEntity?>

    @Query("SELECT * FROM cycles WHERE userId = :userId ORDER BY startDate DESC LIMIT :n")
    fun getLastNCyclesFlow(userId: String, n: Int): Flow<List<CycleEntity>>

    @Query("SELECT * FROM cycles WHERE userId = :userId ORDER BY startDate DESC LIMIT :n")
    suspend fun getLastNCycles(userId: String, n: Int): List<CycleEntity>

    @Query("SELECT * FROM cycles WHERE userId = :userId ORDER BY startDate DESC")
    fun getAllCyclesFlow(userId: String): Flow<List<CycleEntity>>

    @Query("SELECT * FROM cycles WHERE userId = :userId ORDER BY startDate DESC")
    suspend fun getAllCycles(userId: String): List<CycleEntity>

    @Query("UPDATE cycles SET isCurrent = 0, updatedAt = :updatedAt WHERE userId = :userId AND isCurrent = 1")
    suspend fun markAllNotCurrent(userId: String, updatedAt: String): Int

    @Query("UPDATE cycles SET endDate = :endDate, cycleLength = :cycleLength, periodLength = :periodLength, isCurrent = 0, isRegular = :isRegular, updatedAt = :updatedAt WHERE id = :cycleId")
    suspend fun closeCycle(cycleId: String, endDate: String, cycleLength: Int, periodLength: Int?, isRegular: Boolean?, updatedAt: String): Int

    @Query("DELETE FROM cycles WHERE id = :cycleId")
    suspend fun deleteCycle(cycleId: String): Int
}
