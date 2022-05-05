package com.zaker.data.entities.model

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class AlAdahanResponseModel (
        val code: Long,
        val status: String,
        val data: List<Datum>
    )

    data class Datum (
        val timings: Timings,
        val date: Date,
        val meta: Meta
    )

    data class Date (
        val readable: String,
        val timestamp: String,
        val gregorian: Gregorian,
        val hijri: Hijri
    )

    data class Gregorian (
        val date: String,
        val format: String,
        val day: String,
        val weekday: GregorianWeekday,
        val month: GregorianMonth,
        val year: String,
        val designation: Designation
    )

    data class Designation (
        val abbreviated: String,
        val expanded: String
    )

    data class GregorianMonth (
        val number: Long,
        val en: String
    )

    data class GregorianWeekday (
        val en: String
    )

    data class Hijri (
        val date: String,
        val format: String,
        val day: String,
        val weekday: HijriWeekday,
        val month: HijriMonth,
        val year: String,
        val designation: Designation,
        val holidays: JsonArray
    )

    data class HijriMonth (
        val number: Long,
        val en: String,
        val ar: String
    )

    data class HijriWeekday (
        val en: String,
        val ar: String
    )

    data class Meta (
        val latitude: Double,
        val longitude: Double,
        val timezone: String,
        val method: Method,
        val latitudeAdjustmentMethod: String,
        val midnightMode: String,
        val school: String,
        val offset: Map<String, Long>
    )

    data class Method (
        val id: Long,
        val name: String,
        val params: Params,
        val location: Location
    )

    data class Location (
        val latitude: Double,
        val longitude: Double
    )

    data class Params (
        @SerializedName("Fajr")
        val fajr: Double,

        @SerializedName("Isha")
        val isha: Double
    )

    data class Timings (
        @SerializedName("Fajr")
        val fajr: String,

        @SerializedName("Sunrise")
        val sunrise: String,

        @SerializedName("Dhuhr")
        val dhuhr: String,

        @SerializedName("Asr")
        val asr: String,

        @SerializedName("Sunset")
        val sunset: String,

        @SerializedName("Maghrib")
        val maghrib: String,

        @SerializedName("Isha")
        val isha: String,

        @SerializedName("Imsak")
        val imsak: String,

        @SerializedName("Midnight")
        val midnight: String
    )
