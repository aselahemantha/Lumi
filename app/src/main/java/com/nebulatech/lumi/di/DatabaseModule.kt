package com.nebulatech.lumi.di

import androidx.room.Room
import com.nebulatech.lumi.data.local.database.LumiDatabase
import com.nebulatech.lumi.data.repository.BbtRepository
import com.nebulatech.lumi.data.repository.CycleRepository
import com.nebulatech.lumi.data.repository.DailyLogRepository
import com.nebulatech.lumi.data.repository.LhTestRepository
import com.nebulatech.lumi.data.repository.NotificationRepository
import com.nebulatech.lumi.data.repository.RoomBbtRepository
import com.nebulatech.lumi.data.repository.RoomCycleRepository
import com.nebulatech.lumi.data.repository.RoomDailyLogRepository
import com.nebulatech.lumi.data.repository.RoomLhTestRepository
import com.nebulatech.lumi.data.repository.RoomNotificationRepository
import com.nebulatech.lumi.data.repository.RoomUserRepository
import com.nebulatech.lumi.data.repository.UserRepository
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            LumiDatabase::class.java,
            LumiDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration(true).build()
    }

    // DAOs
    single { get<LumiDatabase>().userDao() }
    single { get<LumiDatabase>().userProfileDao() }
    single { get<LumiDatabase>().healthConditionDao() }
    single { get<LumiDatabase>().cycleDao() }
    single { get<LumiDatabase>().dailyLogDao() }
    single { get<LumiDatabase>().dailySymptomDao() }
    single { get<LumiDatabase>().bbtReadingDao() }
    single { get<LumiDatabase>().lhTestDao() }
    single { get<LumiDatabase>().customSymptomDao() }
    single { get<LumiDatabase>().notificationSettingDao() }
    single { get<LumiDatabase>().syncQueueDao() }
    single { get<LumiDatabase>().appNotificationDao() }

    // Repositories
    singleOf(::RoomUserRepository) { bind<UserRepository>() }
    singleOf(::RoomCycleRepository) { bind<CycleRepository>() }
    singleOf(::RoomDailyLogRepository) { bind<DailyLogRepository>() }
    singleOf(::RoomBbtRepository) { bind<BbtRepository>() }
    singleOf(::RoomLhTestRepository) { bind<LhTestRepository>() }
    singleOf(::RoomNotificationRepository) { bind<NotificationRepository>() }
}
