package com.nebulatech.lumi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.nebulatech.lumi.data.local.entity.NotificationSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationSettingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(setting: NotificationSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(settings: List<NotificationSettingEntity>)

    @Query("SELECT * FROM notification_settings WHERE userId = :userId")
    fun getAllSettingsFlow(userId: String): Flow<List<NotificationSettingEntity>>

    @Query("SELECT * FROM notification_settings WHERE userId = :userId")
    suspend fun getAllSettings(userId: String): List<NotificationSettingEntity>

    @Query("SELECT * FROM notification_settings WHERE userId = :userId AND reminderType = :type LIMIT 1")
    suspend fun getSettingByType(userId: String, type: String): NotificationSettingEntity?

    @Query("UPDATE notification_settings SET isEnabled = :isEnabled, updatedAt = :updatedAt WHERE userId = :userId AND reminderType = :type")
    suspend fun toggleSetting(userId: String, type: String, isEnabled: Boolean, updatedAt: String): Int

    @Query("DELETE FROM notification_settings WHERE userId = :userId")
    suspend fun deleteSettingsForUser(userId: String): Int
}
