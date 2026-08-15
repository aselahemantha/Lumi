package com.nebulatech.lumi.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Grain
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.ui.graphics.Color
import com.nebulatech.lumi.core.domain.DataError
import com.nebulatech.lumi.core.domain.EmptyResult
import com.nebulatech.lumi.core.domain.Result
import com.nebulatech.lumi.data.local.dao.AppNotificationDao
import com.nebulatech.lumi.data.local.dao.NotificationSettingDao
import com.nebulatech.lumi.data.local.entity.AppNotificationEntity
import com.nebulatech.lumi.data.local.entity.NotificationSettingEntity
import com.nebulatech.lumi.notifications.LumiNotificationItem
import com.nebulatech.lumi.notifications.NotificationCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.util.UUID

interface NotificationRepository {
    fun getNotificationsFlow(userId: String): Flow<List<LumiNotificationItem>>
    suspend fun addNotification(userId: String, item: LumiNotificationItem): EmptyResult<DataError.Local>
    suspend fun clearAll(userId: String): EmptyResult<DataError.Local>
    suspend fun deleteNotification(id: String): EmptyResult<DataError.Local>
    fun getSettingsFlow(userId: String): Flow<List<NotificationSettingEntity>>
    suspend fun updateSetting(userId: String, type: String, isEnabled: Boolean): EmptyResult<DataError.Local>
    suspend fun toggleAllSettings(userId: String, isEnabled: Boolean): EmptyResult<DataError.Local>
}

class RoomNotificationRepository(
    private val appNotificationDao: AppNotificationDao,
    private val notificationSettingDao: NotificationSettingDao
) : NotificationRepository {

    override fun getNotificationsFlow(userId: String): Flow<List<LumiNotificationItem>> {
        return appNotificationDao.getAllNotificationsFlow(userId).map { list ->
            list.map { entity ->
                val category = runCatching { NotificationCategory.valueOf(entity.category) }
                    .getOrDefault(NotificationCategory.DAILY_REFLECTION)
                val (badgeIcon, badgeBg, badgeTint) = when (category) {
                    NotificationCategory.PERIOD_PREDICTION ->
                        Triple(Icons.Outlined.WaterDrop, Color(0xFFFDE8EF), Color(0xFF8E5572))
                    NotificationCategory.FERTILITY_INSIGHT ->
                        Triple(Icons.Outlined.Spa, Color(0xFFFDE8F4), Color(0xFFB54876))
                    NotificationCategory.PEAK_VITALITY ->
                        Triple(Icons.Outlined.AutoAwesome, Color(0xFFFDF0E4), Color(0xFFD97706))
                    NotificationCategory.PHASE_INSIGHT ->
                        Triple(Icons.Outlined.Grain, Color(0xFFEFE8EB), Color(0xFF5B3950))
                    else -> Triple(null, Color.Transparent, Color.Transparent)
                }

                LumiNotificationItem(
                    id = entity.id,
                    category = category,
                    title = entity.title,
                    body = entity.body,
                    timeText = entity.timeText,
                    badgeIcon = badgeIcon,
                    badgeBgColor = badgeBg,
                    badgeIconColor = badgeTint
                )
            }
        }
    }

    override suspend fun addNotification(userId: String, item: LumiNotificationItem): EmptyResult<DataError.Local> {
        return try {
            val entity = AppNotificationEntity(
                id = item.id.ifBlank { UUID.randomUUID().toString() },
                userId = userId,
                category = item.category.name,
                title = item.title,
                body = item.body,
                timeText = item.timeText,
                isRead = false,
                createdAt = Instant.now().toString()
            )
            appNotificationDao.insertOrUpdate(entity)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun clearAll(userId: String): EmptyResult<DataError.Local> {
        return try {
            appNotificationDao.clearAllNotifications(userId)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun deleteNotification(id: String): EmptyResult<DataError.Local> {
        return try {
            appNotificationDao.deleteNotification(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override fun getSettingsFlow(userId: String): Flow<List<NotificationSettingEntity>> {
        return notificationSettingDao.getAllSettingsFlow(userId)
    }

    override suspend fun updateSetting(userId: String, type: String, isEnabled: Boolean): EmptyResult<DataError.Local> {
        return try {
            val now = Instant.now().toString()
            val existing = notificationSettingDao.getSettingByType(userId, type)
            if (existing != null) {
                notificationSettingDao.toggleSetting(userId, type, isEnabled, now)
            } else {
                val newSetting = NotificationSettingEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    reminderType = type,
                    isEnabled = isEnabled,
                    reminderHour = null,
                    reminderMinute = null,
                    daysBefore = null,
                    updatedAt = now
                )
                notificationSettingDao.insertOrUpdate(newSetting)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun toggleAllSettings(userId: String, isEnabled: Boolean): EmptyResult<DataError.Local> {
        return try {
            val now = Instant.now().toString()
            val defaultTypes = listOf(
                "DAILY_LOG" to Pair(20, 30),
                "BBT_REMINDER" to Pair(7, 0),
                "PERIOD_START" to Pair(9, 0),
                "FERTILE_WINDOW" to Pair(9, 0),
                "PHASE_INSIGHT" to Pair(10, 0)
            )
            val entities = defaultTypes.map { (type, time) ->
                NotificationSettingEntity(
                    id = UUID.randomUUID().toString(),
                    userId = userId,
                    reminderType = type,
                    isEnabled = isEnabled,
                    reminderHour = time.first,
                    reminderMinute = time.second,
                    daysBefore = 2,
                    updatedAt = now
                )
            }
            notificationSettingDao.insertAll(entities)
            Result.Success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
