package com.nebulatech.lumi.di

import com.nebulatech.lumi.analytics.AnalyticsTracker
import com.nebulatech.lumi.analytics.FirebaseAnalyticsTracker
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule = module {
    single<AnalyticsTracker> {
        FirebaseAnalyticsTracker(androidContext())
    }
}
