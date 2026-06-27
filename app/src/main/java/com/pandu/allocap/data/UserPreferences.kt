package com.pandu.allocap.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_BIO = stringPreferencesKey("user_bio")
        val PROFILE_IMAGE_URI = stringPreferencesKey("profile_image_uri")
        val SELECTED_AVATAR_INDEX = intPreferencesKey("selected_avatar_index")
    }

    val userNameFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_NAME]
    }

    val userBioFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.USER_BIO] ?: "Managing capital with intelligence."
    }

    val profileImageUriFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.PROFILE_IMAGE_URI]
    }

    val selectedAvatarIndexFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.SELECTED_AVATAR_INDEX] ?: 0
    }

    suspend fun updateUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
        }
    }

    suspend fun updateUserBio(bio: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_BIO] = bio
        }
    }

    suspend fun updateProfileImageUri(uri: String?) {
        context.dataStore.edit { preferences ->
            if (uri == null) {
                preferences.remove(PreferencesKeys.PROFILE_IMAGE_URI)
            } else {
                preferences[PreferencesKeys.PROFILE_IMAGE_URI] = uri
            }
        }
    }

    suspend fun updateSelectedAvatarIndex(index: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_AVATAR_INDEX] = index
        }
    }
}
