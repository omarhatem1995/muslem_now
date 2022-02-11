package com.myapplication.data.gateways.dao

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.aladahangateway.AlAdahanDao
import com.myapplication.data.gateways.dao.alazkargateway.AlAzkarDao
import com.myapplication.data.gateways.dao.qurangateway.QuranDao
import com.myapplication.data.gateways.dao.qurangateway.QuranPageTypeConverter

@Database(entities = [PrayerTimeModel::class , AzkarModel::class , QuranVersesEntity::class], version = 1)
@TypeConverters(QuranPageTypeConverter::class)
abstract class MuslemNowDataBase : RoomDatabase(){

    abstract fun alAdahanDao(): AlAdahanDao
    abstract fun alAzkarDao(): AlAzkarDao
   abstract fun alQuranDao(): QuranDao

    companion object {
        @Volatile
        private var INSTANCE: MuslemNowDataBase? = null

        fun getDataBase(context: Context): MuslemNowDataBase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MuslemNowDataBase::class.java,
                    "muslemNow-database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}