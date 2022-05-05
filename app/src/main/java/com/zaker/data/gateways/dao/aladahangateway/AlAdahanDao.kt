package com.zaker.data.gateways.dao.aladahangateway

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.Query
import com.zaker.data.entities.model.PrayerTimeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AlAdahanDao {

    @Insert(onConflict = IGNORE)
    fun addPrayerTimes(prayerTimeList: PrayerTimeModel)

    @Query("SELECT * FROM Prayer_Time")
    fun getCurrentPrayerTimes(): LiveData<List<PrayerTimeModel>>

    @Query("SELECT * FROM Prayer_Time WHERE date = :date")
    fun getSpecificDayPrayerTimes(date:String): Flow<List<PrayerTimeModel>>
}