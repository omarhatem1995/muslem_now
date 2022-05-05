package com.zaker.domain.core

import android.content.Context
import android.location.Location
import android.location.LocationManager
import com.google.android.gms.maps.model.LatLng

object LastKnowLocation {
    fun setPartnerDataToPref(key: String?, value: String?, context: Context) {
        val sharedPref = context.getSharedPreferences("ProviderInfo", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun getPartnerDataFromPref(key: String?, context: Context): String? {
        val sharedPref = context.getSharedPreferences("ProviderInfo", Context.MODE_PRIVATE)
        return sharedPref.getString(key, "null")
    }
    fun convertLatLongToLocation(latLng: LatLng): Location? {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        return location
    }
}