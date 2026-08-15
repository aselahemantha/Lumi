package com.nebulatech.lumi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nebulatech.lumi.data.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Determines the correct start destination before the NavHost is composed.
 * - null  → still loading (Room query in progress)
 * - true  → existing user found → start at HomeRoute
 * - false → no user in Room   → start at OnboardingRoute
 */
class AppViewModel(
    userRepository: UserRepository
) : ViewModel() {

    val isExistingUser = userRepository.getCurrentUser()
        .map { user -> user != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
