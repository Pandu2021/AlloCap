package com.pandu.allocap.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pandu.allocap.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    val userName: StateFlow<String?> = repository.userNameFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userBio: StateFlow<String?> = repository.userBioFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val profileImageUri: StateFlow<String?> = repository.profileImageUriFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val selectedAvatarIndex: StateFlow<Int> = repository.selectedAvatarIndexFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun updateName(name: String) {
        viewModelScope.launch { repository.updateUserName(name) }
    }

    fun updateBio(bio: String) {
        viewModelScope.launch { repository.updateUserBio(bio) }
    }

    fun updateProfileImage(uri: String?) {
        viewModelScope.launch { repository.updateProfileImageUri(uri) }
    }

    fun updateAvatar(index: Int) {
        viewModelScope.launch { repository.updateSelectedAvatarIndex(index) }
    }
}
