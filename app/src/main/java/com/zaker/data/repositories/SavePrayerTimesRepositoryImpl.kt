package com.zaker.data.repositories

import com.zaker.data.entities.model.PrayerTimeModel
import com.zaker.data.gateways.dao.aladahangateway.AlAdahanDao
import com.zaker.domain.usecases.data.repo.SavePrayerTimesRepo

class SavePrayerTimesRepositoryImpl (
    val alAdahanDao: AlAdahanDao
): SavePrayerTimesRepo{
    override suspend fun SaveAladahanTime(alAdahan: PrayerTimeModel) {
        return alAdahanDao.addPrayerTimes(alAdahan)
    }

}