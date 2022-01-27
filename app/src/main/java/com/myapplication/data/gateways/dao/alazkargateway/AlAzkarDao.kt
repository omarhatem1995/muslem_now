package com.myapplication.data.gateways.dao.alazkargateway

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.IGNORE
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import androidx.room.Update
import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.data.entities.model.PrayerTimeModel
import kotlinx.coroutines.flow.Flow

@Dao
interface AlAzkarDao {

    @Insert(onConflict = REPLACE)
    fun addAzkar(zekr: AzkarModel)

    @Query("UPDATE Azkar SET userPressedCount = :userPressedCount WHERE id = :id")
    fun updateAzkarCount(userPressedCount :Int, id :Int)

    @Update
    fun updateAzkar(azkarModel: List<AzkarModel>)

    @Query("SELECT * FROM Azkar")
    fun getCurrentAzkar(): LiveData<List<AzkarModel>>

    @Query("SELECT * FROM Azkar WHERE category = :category")
    fun getSpecificDayAzkar(category:String ): LiveData<List<AzkarModel>>

    @Query("SELECT * FROM Azkar WHERE category = :category")
    fun getSpecificDayAzkar2(category:String ): List<AzkarModel>

    @Query("SELECT * FROM Azkar WHERE id = :id")
    fun getSpecificAzkar(id:Long): Flow<AzkarModel>
}