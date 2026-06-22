package com.pandu.allocap.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandu.allocap.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class WelcomeViewModel(
    repository: UserPreferencesRepository
) : ViewModel() {

    val userName: StateFlow<String?> = repository.userNameFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun onUnlock() {
        // Future: Trigger navigation or bio-auth check
    }
}
