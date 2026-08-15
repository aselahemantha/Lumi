package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.SyncQueueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity): Long

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    suspend fun getPendingItems(): List<SyncQueueEntity>

    @Query("SELECT * FROM sync_queue WHERE status = 'PENDING' ORDER BY createdAt ASC")
    fun getPendingItemsFlow(): Flow<List<SyncQueueEntity>>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE status = 'PENDING'")
    fun getPendingCountFlow(): Flow<Int>

    @Query("UPDATE sync_queue SET status = :status, lastAttemptedAt = :attemptedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, attemptedAt: String): Int

    @Query("UPDATE sync_queue SET retryCount = retryCount + 1, status = :status, lastAttemptedAt = :attemptedAt WHERE id = :id")
    suspend fun markFailed(id: Long, status: String = "FAILED", attemptedAt: String): Int

    @Query("DELETE FROM sync_queue WHERE id = :id")
    suspend fun delete(id: Long): Int

    @Query("DELETE FROM sync_queue WHERE status = 'COMPLETED'")
    suspend fun deleteCompleted(): Int
}
