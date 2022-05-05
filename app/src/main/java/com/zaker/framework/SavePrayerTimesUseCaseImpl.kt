package com.zaker.framework

import com.zaker.data.entities.model.PrayerTimeModel
import com.zaker.domain.usecases.data.repo.SavePrayerTimesRepo
import com.zaker.domain.usecases.ui.AlAdahanUseCases

class SavePrayerTimesUseCaseImpl (
    val prayerTimesRepo: SavePrayerTimesRepo
    ) : AlAdahanUseCases.SaveAdahanTiming {
    override suspend fun invoke(adahanList: PrayerTimeModel) =
        prayerTimesRepo.SaveAladahanTime(adahanList)

}