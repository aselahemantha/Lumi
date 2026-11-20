package com.nebulatech.lumi.data.model

/**
 * Shared domain constants for cycle calculations, validation bounds, and fallback defaults.
 */
object CycleDefaults {
    const val DEFAULT_CYCLE_LENGTH = 28
    const val DEFAULT_PERIOD_DURATION = 5
    const val DEFAULT_LUTEAL_PHASE_DAYS = 14
    const val MISSED_PERIOD_THRESHOLD_DAYS = 10

    const val MIN_CYCLE_LENGTH = 20
    const val MIN_EDITABLE_CYCLE_LENGTH = 21
    const val MAX_CYCLE_LENGTH = 45

    const val MIN_PERIOD_DURATION = 2
    const val MAX_PERIOD_DURATION = 10
}
