package com.myapplication.data.gateways.dao.aladahangateway

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myapplication.data.entities.model.PrayerTimeModel

@Database(entities = [PrayerTimeModel::class], version = 1)
abstract class AlAdahanDatabase : RoomDatabase() {

    abstract fun alAdahanDao(): AlAdahanDao

    companion object {
        @Volatile
        private var INSTANCE: AlAdahanDatabase? = null

        fun getDataBase(context: Context): AlAdahanDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AlAdahanDatabase::class.java,
                    "alAdahan-database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}