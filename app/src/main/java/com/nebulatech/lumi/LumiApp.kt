package com.nebulatech.lumi

import android.app.Application
import com.nebulatech.lumi.di.databaseModule
import com.nebulatech.lumi.home.HomeViewModel
import com.nebulatech.lumi.logging.LoggingViewModel
import com.nebulatech.lumi.onboarding.OnboardingViewModel
import com.nebulatech.lumi.profile.ProfileViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::OnboardingViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::LoggingViewModel)
    viewModelOf(::ProfileViewModel)
}

class LumiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LumiApp)
            modules(
                appModule,
                databaseModule
            )
        }
    }
}
