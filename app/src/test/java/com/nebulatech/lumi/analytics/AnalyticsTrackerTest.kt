package com.nebulatech.lumi.analytics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AnalyticsTrackerTest {

    private lateinit var fakeAnalyticsTracker: FakeAnalyticsTracker

    @Before
    fun setUp() {
        fakeAnalyticsTracker = FakeAnalyticsTracker()
    }

    @Test
    fun `trackScreenView records screen name and crashlytics breadcrumb`() {
        fakeAnalyticsTracker.trackScreenView(AnalyticsConstants.Screens.HOME_CYCLE_RING)

        assertEquals(1, fakeAnalyticsTracker.trackedScreens.size)
        assertEquals(AnalyticsConstants.Screens.HOME_CYCLE_RING, fakeAnalyticsTracker.trackedScreens.first().screenName)
        assertEquals("Navigation -> Screen: ${AnalyticsConstants.Screens.HOME_CYCLE_RING}", fakeAnalyticsTracker.breadcrumbs.first())
        assertEquals(AnalyticsConstants.Screens.HOME_CYCLE_RING, fakeAnalyticsTracker.customCrashKeys["current_screen"])
    }

    @Test
    fun `trackButtonClick logs button name, screen, and breadcrumb`() {
        fakeAnalyticsTracker.trackButtonClick(
            buttonName = AnalyticsConstants.Buttons.TAB_CALENDAR,
            screenName = AnalyticsConstants.Screens.HOME_CYCLE_RING,
            extraParams = mapOf("tab" to "CALENDAR")
        )

        assertEquals(1, fakeAnalyticsTracker.trackedClicks.size)
        val click = fakeAnalyticsTracker.trackedClicks.first()
        assertEquals(AnalyticsConstants.Buttons.TAB_CALENDAR, click.buttonName)
        assertEquals(AnalyticsConstants.Screens.HOME_CYCLE_RING, click.screenName)
        assertEquals("CALENDAR", click.params["tab"])
        assertEquals("Click: ${AnalyticsConstants.Buttons.TAB_CALENDAR} on Screen: ${AnalyticsConstants.Screens.HOME_CYCLE_RING}", fakeAnalyticsTracker.breadcrumbs.first())
    }

    @Test
    fun `recordException records non-fatal exception and context message`() {
        val testException = IllegalStateException("Test database exception")
        fakeAnalyticsTracker.recordException(testException, "Failed to load cycle history")

        assertEquals(1, fakeAnalyticsTracker.recordedExceptions.size)
        assertEquals(testException, fakeAnalyticsTracker.recordedExceptions.first().first)
        assertEquals("Failed to load cycle history", fakeAnalyticsTracker.recordedExceptions.first().second)
    }

    @Test
    fun `setUserId sets user identifier across analytics and crashlytics`() {
        fakeAnalyticsTracker.setUserId("user-12345")
        assertEquals("user-12345", fakeAnalyticsTracker.currentUserId)
    }

    // ── Test Fake ────────────────────────────────────────────────────────────

    class FakeAnalyticsTracker : AnalyticsTracker {
        data class ScreenRecord(val screenName: String, val screenClass: String?)
        data class ClickRecord(val buttonName: String, val screenName: String, val params: Map<String, Any>)

        val trackedScreens = mutableListOf<ScreenRecord>()
        val trackedClicks = mutableListOf<ClickRecord>()
        val trackedEvents = mutableListOf<Pair<String, Map<String, Any>>>()
        val recordedExceptions = mutableListOf<Pair<Throwable, String?>>()
        val breadcrumbs = mutableListOf<String>()
        val customCrashKeys = mutableMapOf<String, String>()
        val userProperties = mutableMapOf<String, String?>()
        var currentUserId: String? = null

        override fun trackScreenView(screenName: String, screenClass: String?) {
            logBreadcrumb("Navigation -> Screen: $screenName")
            setCustomCrashKey("current_screen", screenName)
            trackedScreens.add(ScreenRecord(screenName, screenClass))
        }

        override fun trackButtonClick(
            buttonName: String,
            screenName: String,
            extraParams: Map<String, Any>
        ) {
            logBreadcrumb("Click: $buttonName on Screen: $screenName")
            trackedClicks.add(ClickRecord(buttonName, screenName, extraParams))
        }

        override fun trackEvent(eventName: String, params: Map<String, Any>) {
            trackedEvents.add(eventName to params)
        }

        override fun setUserProperty(name: String, value: String?) {
            userProperties[name] = value
        }

        override fun setUserId(userId: String?) {
            this.currentUserId = userId
        }

        override fun recordException(throwable: Throwable, message: String?) {
            recordedExceptions.add(throwable to message)
        }

        override fun logBreadcrumb(message: String) {
            breadcrumbs.add(message)
        }

        override fun setCustomCrashKey(key: String, value: String) {
            customCrashKeys[key] = value
        }
    }
}
