package com.nebulatech.lumi.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.nebulatech.lumi.data.local.dao.AppNotificationDao
import com.nebulatech.lumi.data.local.dao.BbtReadingDao
import com.nebulatech.lumi.data.local.dao.CustomSymptomDao
import com.nebulatech.lumi.data.local.dao.CycleDao
import com.nebulatech.lumi.data.local.dao.DailyLogDao
import com.nebulatech.lumi.data.local.dao.DailySymptomDao
import com.nebulatech.lumi.data.local.dao.HealthConditionDao
import com.nebulatech.lumi.data.local.dao.LhTestDao
import com.nebulatech.lumi.data.local.dao.NotificationSettingDao
import com.nebulatech.lumi.data.local.dao.SyncQueueDao
import com.nebulatech.lumi.data.local.dao.UserDao
import com.nebulatech.lumi.data.local.dao.UserProfileDao
import com.nebulatech.lumi.data.local.entity.AppNotificationEntity
import com.nebulatech.lumi.data.local.entity.BbtReadingEntity
import com.nebulatech.lumi.data.local.entity.CustomSymptomEntity
import com.nebulatech.lumi.data.local.entity.CycleEntity
import com.nebulatech.lumi.data.local.entity.DailyLogEntity
import com.nebulatech.lumi.data.local.entity.DailySymptomEntity
import com.nebulatech.lumi.data.local.entity.HealthConditionEntity
import com.nebulatech.lumi.data.local.entity.LhTestEntity
import com.nebulatech.lumi.data.local.entity.NotificationSettingEntity
import com.nebulatech.lumi.data.local.entity.SyncQueueEntity
import com.nebulatech.lumi.data.local.entity.UserEntity
import com.nebulatech.lumi.data.local.entity.UserProfileEntity

@Database(
    entities = [
        UserEntity::class,
        UserProfileEntity::class,
        HealthConditionEntity::class,
        CycleEntity::class,
        DailyLogEntity::class,
        DailySymptomEntity::class,
        BbtReadingEntity::class,
        LhTestEntity::class,
        CustomSymptomEntity::class,
        NotificationSettingEntity::class,
        SyncQueueEntity::class,
        AppNotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class LumiDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun healthConditionDao(): HealthConditionDao
    abstract fun cycleDao(): CycleDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun dailySymptomDao(): DailySymptomDao
    abstract fun bbtReadingDao(): BbtReadingDao
    abstract fun lhTestDao(): LhTestDao
    abstract fun customSymptomDao(): CustomSymptomDao
    abstract fun notificationSettingDao(): NotificationSettingDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun appNotificationDao(): AppNotificationDao

    companion object {
        const val DATABASE_NAME = "lumi.db"
    }
}
