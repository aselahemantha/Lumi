package com.nebulatech.lumi.analytics

object AnalyticsConstants {
    // Screen Names
    object Screens {
        const val ONBOARDING = "onboarding_screen"
        const val HOME_CYCLE_RING = "home_cycle_ring_screen"
        const val HOME_FERTILITY_DASHBOARD = "home_fertility_dashboard_screen"
        const val HOME_LATE_LUTEAL = "home_late_luteal_screen"
        const val CALENDAR = "calendar_screen"
        const val INSIGHTS = "insights_screen"
        const val PROFILE = "profile_screen"
        const val NOTIFICATION_CENTER = "notification_center_screen"
        const val BIOMETRIC_LOCK = "biometric_lock_screen"
        const val DAILY_LOG = "daily_log_screen"
    }

    // Button and Click Identifiers
    object Buttons {
        // Navigation / Tabs
        const val TAB_TODAY = "tab_today"
        const val TAB_CALENDAR = "tab_calendar"
        const val TAB_INSIGHTS = "tab_insights"
        const val TAB_PROFILE = "tab_profile"

        // Header / General
        const val NOTIFICATION_BELL = "btn_notification_bell"
        const val BACK = "btn_back"

        // Home
        const val QUICK_LOG_TODAY = "btn_quick_log_today"
        const val LOG_PERIOD_FLOW = "btn_log_period_flow"
        const val CYCLE_RING_CENTER = "btn_cycle_ring_center"

        // Daily Log
        const val SAVE_DAILY_LOG = "btn_save_daily_log"
        const val CANCEL_DAILY_LOG = "btn_cancel_daily_log"
        const val SELECT_SYMPTOM = "btn_select_symptom"
        const val SELECT_MOOD = "btn_select_mood"
        const val SELECT_INTIMACY = "btn_select_intimacy"

        // Calendar
        const val CALENDAR_DAY_CLICK = "btn_calendar_day_click"
        const val CALENDAR_MONTH_PREV = "btn_calendar_month_prev"
        const val CALENDAR_MONTH_NEXT = "btn_calendar_month_next"
        const val CALENDAR_LEGEND_TOGGLE = "btn_calendar_legend_toggle"

        // Insights
        const val INSIGHTS_TAB_TRENDS = "btn_insights_tab_trends"
        const val INSIGHTS_TAB_SYMPTOMS = "btn_insights_tab_symptoms"
        const val INSIGHTS_EXPORT_REPORT = "btn_insights_export_report"

        // Profile
        const val TOGGLE_BIOMETRIC = "btn_toggle_biometric"
        const val TOGGLE_NOTIFICATIONS = "btn_toggle_notifications"
        const val SYNC_NOW = "btn_sync_now"
        const val BACKUP_RESTORE = "btn_backup_restore"
        const val LOGOUT = "btn_logout"

        // Notification Center
        const val NOTIFICATION_ITEM = "btn_notification_item"
        const val CLEAR_ALL_NOTIFICATIONS = "btn_clear_all_notifications"

        // Biometric Lock
        const val BIOMETRIC_UNLOCK = "btn_biometric_unlock"

        // Onboarding
        const val ONBOARDING_NEXT = "btn_onboarding_next"
        const val ONBOARDING_BACK = "btn_onboarding_back"
        const val ONBOARDING_COMPLETE = "btn_onboarding_complete"
        const val ONBOARDING_OPTION_SELECT = "btn_onboarding_option_select"
    }

    // Custom Event Names
    object Events {
        const val SCREEN_VIEW = "lumi_screen_view"
        const val USER_CLICK = "lumi_user_click"
        const val TAB_SWITCH = "lumi_tab_switch"
        const val LOG_SAVED = "lumi_log_saved"
        const val BIOMETRIC_AUTH = "lumi_biometric_auth"
        const val ONBOARDING_STEP = "lumi_onboarding_step"
        const val SYNC_TRIGGERED = "lumi_sync_triggered"
    }

    // Parameters
    object Params {
        const val SCREEN_NAME = "screen_name"
        const val SCREEN_CLASS = "screen_class"
        const val BUTTON_NAME = "button_name"
        const val PREVIOUS_SCREEN = "previous_screen"
        const val TAB_NAME = "tab_name"
        const val ITEM_ID = "item_id"
        const val RESULT = "result"
        const val VALUE = "value"
        const val STEP_INDEX = "step_index"
    }
}
