package com.nebulatech.lumi.data.model

enum class CyclePhase {
    MENSTRUATION,
    FOLLICULAR,
    FERTILE_WINDOW,
    LUTEAL,
    LATE_LUTEAL
}

enum class PrimaryGoal {
    TRACK_CYCLE,
    UNDERSTAND_SYMPTOMS,
    OPTIMIZE_FERTILITY,
    AVOID_PREGNANCY
}

enum class WeightUnit {
    KG,
    LBS
}

enum class HealthConditionType {
    PCOS,
    ENDOMETRIOSIS,
    THYROID,
    NONE
}

enum class FlowIntensityType {
    LIGHT,
    MEDIUM,
    HEAVY,
    SPOTTING,
    NONE
}

enum class MoodType {
    CALM,
    ENERGETIC,
    SENSITIVE,
    TIRED
}

enum class LhIntensityType {
    LOW,
    HIGH,
    PEAK
}

enum class BbtSource {
    MANUAL,
    OURA,
    GARMIN,
    APPLE_HEALTH
}

enum class SymptomCategoryType {
    PHYSICAL,
    EMOTIONAL,
    DIGESTIVE,
    SKIN,
    OTHER
}

enum class ReminderType {
    PERIOD_START,
    FERTILE_WINDOW,
    OVULATION,
    BBT_REMINDER,
    DAILY_LOG,
    PERIOD_END
}

enum class SyncStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    FAILED
}

enum class SyncOperation {
    INSERT,
    UPDATE,
    DELETE
}
