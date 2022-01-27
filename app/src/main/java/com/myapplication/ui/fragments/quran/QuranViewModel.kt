package com.myapplication.ui.fragments.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.myapplication.data.repositories.SharedPreferencesRepository

class QuranViewModel(application: Application): AndroidViewModel(application)  {
    var preference = SharedPreferencesRepository(application)

}