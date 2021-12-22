package com.myapplication.data.entities.model

import androidx.room.Entity

@Entity(tableName = "Azkar" , primaryKeys = ["id"])
data class AzkarModel (
    val id : Int,
    val zekr : String,
    val description : String,
    val category : String,
    val count : Int? = 0,
    var userPressedCount : Int,
    val reference : String,
    val date : String
)
