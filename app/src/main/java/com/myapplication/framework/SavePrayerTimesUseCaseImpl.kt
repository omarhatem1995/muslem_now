package com.myapplication.framework

import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.domain.usecases.data.repo.SavePrayerTimesRepo
import com.myapplication.domain.usecases.ui.AlAdahanUseCases

class SavePrayerTimesUseCaseImpl (
    val prayerTimesRepo: SavePrayerTimesRepo
    ) : AlAdahanUseCases.SaveAdahanTiming {
    override suspend fun invoke(adahanList: PrayerTimeModel) =
        prayerTimesRepo.SaveAladahanTime(adahanList)

}