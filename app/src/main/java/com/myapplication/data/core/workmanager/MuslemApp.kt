package com.myapplication.data.core.workmanager

import android.app.Application
import android.icu.util.TimeUnit
import android.os.Build
import android.os.Build.VERSION_CODES.M
import androidx.work.*
import android.app.NotificationManager

import android.app.NotificationChannel




class MuslemApp: Application() {
    val CHANNEL_1_ID = "Azan channel"

    override fun onCreate() {
        createNotificationChannels()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork("ElSalahWorker",ExistingPeriodicWorkPolicy.KEEP,request)
        super.onCreate()
    }




    private val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .apply {

            setRequiresDeviceIdle(true)

        }
        .setRequiresCharging(false)
        .build()

    private val request = PeriodicWorkRequestBuilder<ElSalahWorker>(1,
        java.util.concurrent.TimeUnit.DAYS
    )
        .setConstraints(constraints)
        .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,
            java.util.concurrent.TimeUnit.MILLISECONDS
        )
        .addTag("settingAlarm")
        .build()


    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                CHANNEL_1_ID,
                "Channel 1",
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "This is Azan Notification Channel"
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)

        }
    }
}