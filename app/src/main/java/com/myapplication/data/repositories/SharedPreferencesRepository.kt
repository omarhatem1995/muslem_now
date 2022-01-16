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

    fun setLat(lat:String?) = preference.edit()
        .putString("latitude", lat).apply()

    fun getLat() = preference.getString("latitude", null)

    fun setLong(long:String?) = preference.edit()
        .putString("longitude", long).apply()

    fun getLong() = preference.getString("longitude", null)

    fun setSettings(isSet:Boolean?) = isSet?.let {
        preference.edit()
        .putBoolean("isSet", it).apply()
    }

    fun getSettings() = preference.getBoolean("isSet", false)

    fun setLanguage(language:String?) = preference.edit()
        .putString("language", language).apply()

    fun getLanguage() = preference.getString("language", null)

    fun setAzanType(type:String?) = preference.edit()
        .putString("azanType", type).apply()

    fun getAzanType() = preference.getString("azanType", null)

    fun setMoazen(moazen:String?) = preference.edit()
        .putString("moazen", moazen).apply()

    fun getMoazen() = preference.getString("moazen", null)

    fun setFirstLaunch(isFirstLaunch:Boolean?) = isFirstLaunch?.let {
        preference.edit()
        .putBoolean("firstLaunch", it).apply()
    }

    fun getFirstLaunch() = preference.getBoolean("firstLaunch", false)

    fun setAzkarAfterAzan(isAzkarAfterAzan:Boolean?) = isAzkarAfterAzan?.let {
        preference.edit()
        .putBoolean("azkarAfterAzan", it).apply()
    }

    fun getAzkarAfterAzan() = preference.getBoolean("azkarAfterAzan",false)

    fun setAzkarSabah(isAzkarSabah:Boolean?) = isAzkarSabah?.let {
        preference.edit()
        .putBoolean("azkarSabah", it).apply()
    }

    fun getAzkarSabah() = preference.getBoolean("azkarSabah",false)

    fun setAzkarMasaa(isAzkarMasaa:Boolean?) = isAzkarMasaa?.let {
        preference.edit()
        .putBoolean("azkarMasaa", it).apply()
    }

    fun getAzkarMasaa() = preference.getBoolean("azkarMasaa",false)

    fun setAzkarSabahTiming(sabahTiming:String?) = preference.edit()
        .putString("sabahTiming", sabahTiming).apply()

    fun getAzkarSabahTiming() = preference.getString("sabahTiming", "05:30")

    fun setAzkarMasaaTiming(masaaTiming:String?) = preference.edit()
        .putString("masaaTiming", masaaTiming).apply()

    fun getAzkarMasaaTiming() = preference.getString("masaaTiming", "05:30")

    fun setFajr(isFajr:Boolean?) = isFajr?.let {
        preference.edit()
        .putBoolean("Fajr", it).apply()
    }

    fun getFajr() = preference.getBoolean("Fajr", true)

    fun setDuhr(isDuhr:Boolean?) = isDuhr?.let {
        preference.edit()
        .putBoolean("Dhur", it).apply()
    }

    fun getDuhr() = preference.getBoolean("Dhur", true)

    fun setSunRise(isSunRise:Boolean?) = isSunRise?.let {
        preference.edit()
        .putBoolean("SunRise", it).apply()
    }

    fun getSunRise() = preference.getBoolean("SunRise", true)

    fun setAsr(isAsr:Boolean?) = isAsr?.let {
        preference.edit()
        .putBoolean("Asr", it).apply()
    }

    fun getAsr() = preference.getBoolean("Asr", true)

    fun setMaghrib(isMaghrib:Boolean?) = isMaghrib?.let {
        preference.edit()
        .putBoolean("Maghrib", it).apply()
    }

    fun getMaghrib() = preference.getBoolean("Maghrib", true)

    fun setIsha(isIsha:Boolean?) = isIsha?.let {
        preference.edit()
        .putBoolean("Isha", it).apply()
    }

    fun getIsha() = preference.getBoolean("Isha", true)

}