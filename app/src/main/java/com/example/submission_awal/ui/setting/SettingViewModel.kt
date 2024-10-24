package com.example.submission_awal.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SettingViewModel(private val preferences: SettingPreferences) : ViewModel() {
    fun getThemeSetting(): LiveData<Boolean> {
        return preferences.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            preferences.saveThemeSetting(isDarkModeActive)
        }
    }
    fun saveNotificationSetting(isNotificationEnabled: Boolean) {
        viewModelScope.launch {
            preferences.saveNotificationSetting(isNotificationEnabled)
        }
    }
    fun isNotificationEnabled(): LiveData<Boolean> {
        return preferences.isNotificationEnabled().asLiveData()
    }

}