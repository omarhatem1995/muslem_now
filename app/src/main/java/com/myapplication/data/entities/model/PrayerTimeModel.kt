package com.myapplication.data.entities.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Prayer_Time")
data class PrayerTimeModel(
    @PrimaryKey(autoGenerate = true)
    val prayerId: Int,

    var image: Int,
    var name: String,
    val time: String,
    var imageStatus: Int,
    var date:String
)