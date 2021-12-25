package com.myapplication.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.myapplication.data.repositories.SharedPreferencesRepository

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    var preference = SharedPreferencesRepository(application)

}