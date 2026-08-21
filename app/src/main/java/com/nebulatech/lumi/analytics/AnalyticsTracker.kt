package com.nebulatech.lumi.analytics

/**
 * Interface defining analytics tracking, user click monitoring,
 * and Crashlytics diagnostics for the Lumi application.
 */
interface AnalyticsTracker {
    /**
     * Tracks a screen view event with Firebase Analytics and records a Crashlytics breadcrumb.
     */
    fun trackScreenView(screenName: String, screenClass: String? = null)

    /**
     * Tracks a user click / interaction event.
     */
    fun trackButtonClick(
        buttonName: String,
        screenName: String,
        extraParams: Map<String, Any> = emptyMap()
    )

    /**
     * Tracks a custom analytics event with key-value parameters.
     */
    fun trackEvent(
        eventName: String,
        params: Map<String, Any> = emptyMap()
    )

    /**
     * Sets a custom user property in Firebase Analytics.
     */
    fun setUserProperty(name: String, value: String?)

    /**
     * Associates a user identifier with Analytics and Crashlytics.
     */
    fun setUserId(userId: String?)

    /**
     * Records a non-fatal exception in Firebase Crashlytics.
     */
    fun recordException(throwable: Throwable, message: String? = null)

    /**
     * Logs a diagnostic breadcrumb in Firebase Crashlytics.
     */
    fun logBreadcrumb(message: String)

    /**
     * Sets a custom key-value pair in Crashlytics reports.
     */
    fun setCustomCrashKey(key: String, value: String)
}
