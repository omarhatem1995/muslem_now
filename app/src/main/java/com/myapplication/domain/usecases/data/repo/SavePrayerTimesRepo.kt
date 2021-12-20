package com.myapplication.domain.usecases.data.repo

import com.myapplication.data.entities.model.PrayerTimeModel

interface SavePrayerTimesRepo {
    suspend fun SaveAladahanTime(alAdahan: PrayerTimeModel)
}