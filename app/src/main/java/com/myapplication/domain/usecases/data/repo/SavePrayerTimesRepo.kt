package com.myapplication.domain.usecases.data.repo

import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.entities.model.PrayerTimeModel

interface SavePrayerTimesRepo {
    suspend fun SaveAladahanTime(alAdahan: List<PrayerTimeModel>)
}