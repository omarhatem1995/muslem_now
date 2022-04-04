package com.myapplication.data.entities.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "Prayer_Time" , primaryKeys = ["prayerId","date"])
@Parcelize
data class PrayerTimeModel(
    val prayerId: Int,
    var image: Int,
    var name: String,
    val time: String,
    var imageStatus: Int,
    var date:String,
    var hijriDate:String,
    var hijriDateEn:String
) : Parcelable