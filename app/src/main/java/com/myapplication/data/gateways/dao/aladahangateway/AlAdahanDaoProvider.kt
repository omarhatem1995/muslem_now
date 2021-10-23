//package com.myapplication.data.gateways.dao.aladahangateway
//
//import android.content.Context
//import androidx.room.Room
//
//object AlAdahanDaoProvider {
//    fun provideGateway(applicationContext: Context): AlAdahanDatabase.AppDatabase{
//         val db = Room.databaseBuilder(
//            applicationContext,
//            AlAdahanDatabase.AppDatabase::class.java, "database-name"
//        ).build()
//        return db
//    }
//}