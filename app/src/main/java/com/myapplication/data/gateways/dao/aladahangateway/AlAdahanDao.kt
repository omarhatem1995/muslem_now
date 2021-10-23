package com.myapplication.data.gateways.dao.aladahangateway

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.myapplication.data.entities.model.Datum
import com.myapplication.data.entities.model.PrayerTimeModel

@Dao
interface AlAdahanDao {

    @Insert(onConflict = IGNORE)
    fun addPrayerTimes(prayerTimeList: List<PrayerTimeModel>)

    @Query("SELECT * FROM Prayer_Time")
    fun getCurrentPrayerTimes(): LiveData<List<PrayerTimeModel>>
}