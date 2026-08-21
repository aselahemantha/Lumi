package com.nebulatech.lumi.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * No-op implementation of [AnalyticsTracker] used for Compose previews and testing defaults.
 */
object NoOpAnalyticsTracker : AnalyticsTracker {
    override fun trackScreenView(screenName: String, screenClass: String?) {}
    override fun trackButtonClick(buttonName: String, screenName: String, extraParams: Map<String, Any>) {}
    override fun trackEvent(eventName: String, params: Map<String, Any>) {}
    override fun setUserProperty(name: String, value: String?) {}
    override fun setUserId(userId: String?) {}
    override fun recordException(throwable: Throwable, message: String?) {}
    override fun logBreadcrumb(message: String) {}
    override fun setCustomCrashKey(key: String, value: String) {}
}

/**
 * CompositionLocal providing access to the current [AnalyticsTracker].
 */
val LocalAnalyticsTracker = staticCompositionLocalOf<AnalyticsTracker> {
    NoOpAnalyticsTracker
}

/**
 * Composable helper that logs a screen view whenever [screenName] changes or is first composed.
 */
@Composable
fun TrackScreenView(
    screenName: String,
    screenClass: String? = null,
    tracker: AnalyticsTracker = LocalAnalyticsTracker.current
) {
    LaunchedEffect(screenName) {
        tracker.trackScreenView(screenName = screenName, screenClass = screenClass)
    }
}
