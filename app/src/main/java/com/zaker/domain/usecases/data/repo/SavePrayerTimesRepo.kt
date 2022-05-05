package com.zaker.domain.usecases.data.repo

import com.zaker.data.entities.model.PrayerTimeModel

interface SavePrayerTimesRepo {
    suspend fun SaveAladahanTime(alAdahan: PrayerTimeModel)
}