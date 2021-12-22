package com.myapplication.data.repositories

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class SharedPreferencesRepository(application: Application) {
    var preference: SharedPreferences = application.getSharedPreferences("", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun setCity(currentCity: String?) = preference.edit()
        .putString("city", currentCity).apply()

    fun getCity() = preference.getString("city", null)

    fun setDate(date:String?) = preference.edit()
        .putString("date", date).apply()

    fun getDate() = preference.getString("date", null)
}