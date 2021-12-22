package com.myapplication.domain.usecases.data.repo

import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.data.entities.model.PrayerTimeModel

interface SaveAzkarRepo {
    suspend fun SaveAzkar(azkar: AzkarModel)
}