package com.zaker.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.zaker.data.repositories.SharedPreferencesRepository

class SettingsViewModel(application: Application): AndroidViewModel(application) {
    var preference = SharedPreferencesRepository(application)

}