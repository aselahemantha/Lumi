package com.nebulatech.lumi.di

import androidx.room.Room
import com.nebulatech.lumi.data.local.database.DatabaseMigrator
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
import com.nebulatech.lumi.security.DatabaseKeyManager
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val databaseModule = module {
    // Security & Encryption
    singleOf(::DatabaseKeyManager)

    // Database
    single {
        val context = androidContext()
        val keyManager = get<DatabaseKeyManager>()
        val passphrase = keyManager.getOrCreatePassphrase()
        val passphraseHex = keyManager.getPassphraseString()

        // Safely migrate existing unencrypted databases (from previous releases)
        DatabaseMigrator.migrateIfNeeded(context, LumiDatabase.DATABASE_NAME, passphraseHex)

        val factory = SupportOpenHelperFactory(passphrase)

        Room.databaseBuilder(
            context,
            LumiDatabase::class.java,
            LumiDatabase.DATABASE_NAME
        )
        .openHelperFactory(factory)
        .fallbackToDestructiveMigration(true)
        .build()
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
