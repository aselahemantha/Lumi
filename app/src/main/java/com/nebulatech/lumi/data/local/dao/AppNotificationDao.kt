package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.AppNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppNotificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(notification: AppNotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<AppNotificationEntity>)

    @Query("SELECT * FROM app_notifications WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllNotificationsFlow(userId: String): Flow<List<AppNotificationEntity>>

    @Query("DELETE FROM app_notifications WHERE userId = :userId")
    suspend fun clearAllNotifications(userId: String): Int

    @Query("DELETE FROM app_notifications WHERE id = :id")
    suspend fun deleteNotification(id: String): Int
}
