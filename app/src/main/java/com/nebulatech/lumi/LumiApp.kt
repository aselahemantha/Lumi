package com.nebulatech.lumi

import android.app.Application
import com.nebulatech.lumi.onboarding.OnboardingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::OnboardingViewModel)
}

class LumiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LumiApp)
            modules(appModule)
        }
    }
}
