package com.myapplication.data.repositories

import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.aladahangateway.AlAdahanDao
import com.myapplication.domain.usecases.data.repo.SavePrayerTimesRepo

class SavePrayerTimesRepositoryImpl (
    val alAdahanDao: AlAdahanDao
): SavePrayerTimesRepo{
    override suspend fun SaveAladahanTime(alAdahan: PrayerTimeModel) {
        return alAdahanDao.addPrayerTimes(alAdahan)
    }

}