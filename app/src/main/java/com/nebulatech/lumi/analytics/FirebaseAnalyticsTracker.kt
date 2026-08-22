package com.nebulatech.lumi.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

class FirebaseAnalyticsTracker(
    context: Context
) : AnalyticsTracker {

    private val appContext = context.applicationContext

    private val firebaseAnalytics: FirebaseAnalytics? by lazy {
        runCatching {
            FirebaseAnalytics.getInstance(appContext)
        }.onFailure {
            Log.w(TAG, "FirebaseAnalytics could not be initialized", it)
        }.getOrNull()
    }

    private val crashlytics: FirebaseCrashlytics? by lazy {
        runCatching {
            FirebaseCrashlytics.getInstance()
        }.onFailure {
            Log.w(TAG, "FirebaseCrashlytics could not be initialized", it)
        }.getOrNull()
    }

    override fun trackScreenView(screenName: String, screenClass: String?) {
        logBreadcrumb("Navigation -> Screen: $screenName")
        setCustomCrashKey("current_screen", screenName)

        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass ?: screenName)
            putString(AnalyticsConstants.Params.SCREEN_NAME, screenName)
        }

        runCatching {
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
            firebaseAnalytics?.logEvent(AnalyticsConstants.Events.SCREEN_VIEW, bundle)
        }.onFailure {
            Log.w(TAG, "Failed to track screen view: $screenName", it)
        }
    }

    override fun trackButtonClick(
        buttonName: String,
        screenName: String,
        extraParams: Map<String, Any>
    ) {
        logBreadcrumb("Click: $buttonName on Screen: $screenName")

        val bundle = Bundle().apply {
            putString(AnalyticsConstants.Params.BUTTON_NAME, buttonName)
            putString(AnalyticsConstants.Params.SCREEN_NAME, screenName)
            extraParams.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }

        runCatching {
            firebaseAnalytics?.logEvent(AnalyticsConstants.Events.USER_CLICK, bundle)
            firebaseAnalytics?.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, Bundle().apply {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "button")
                putString(FirebaseAnalytics.Param.ITEM_ID, buttonName)
                putString(FirebaseAnalytics.Param.ITEM_NAME, buttonName)
            })
        }.onFailure {
            Log.w(TAG, "Failed to track button click: $buttonName", it)
        }
    }

    override fun trackEvent(eventName: String, params: Map<String, Any>) {
        val bundle = Bundle().apply {
            params.forEach { (key, value) ->
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Long -> putLong(key, value)
                    is Double -> putDouble(key, value)
                    is Float -> putFloat(key, value)
                    is Boolean -> putBoolean(key, value)
                    else -> putString(key, value.toString())
                }
            }
        }

        runCatching {
            firebaseAnalytics?.logEvent(eventName, bundle)
        }.onFailure {
            Log.w(TAG, "Failed to track event: $eventName", it)
        }
    }

    override fun setUserProperty(name: String, value: String?) {
        runCatching {
            firebaseAnalytics?.setUserProperty(name, value)
        }.onFailure {
            Log.w(TAG, "Failed to set user property: $name", it)
        }
    }

    override fun setUserId(userId: String?) {
        runCatching {
            firebaseAnalytics?.setUserId(userId)
            if (userId != null) {
                crashlytics?.setUserId(userId)
            }
        }.onFailure {
            Log.w(TAG, "Failed to set user ID", it)
        }
    }

    override fun recordException(throwable: Throwable, message: String?) {
        runCatching {
            message?.let { crashlytics?.log("Exception context: $it") }
            crashlytics?.recordException(throwable)
        }.onFailure {
            Log.w(TAG, "Failed to record exception in Crashlytics", it)
        }
    }

    override fun logBreadcrumb(message: String) {
        runCatching {
            crashlytics?.log(message)
        }.onFailure {
            Log.w(TAG, "Failed to log breadcrumb in Crashlytics", it)
        }
    }

    override fun setCustomCrashKey(key: String, value: String) {
        runCatching {
            crashlytics?.setCustomKey(key, value)
        }.onFailure {
            Log.w(TAG, "Failed to set custom crash key", it)
        }
    }

    companion object {
        private const val TAG = "LumiAnalytics"
    }
}
