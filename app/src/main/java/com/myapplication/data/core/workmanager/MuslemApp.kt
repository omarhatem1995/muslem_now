package com.myapplication.data.core.workmanager

import android.app.Application
import android.icu.util.TimeUnit
import android.os.Build
import android.os.Build.VERSION_CODES.M
import androidx.work.*

class MuslemApp: Application() {


    override fun onCreate() {

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
}