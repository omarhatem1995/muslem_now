package com.myapplication

import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel

import android.app.NotificationManager

import android.content.Intent

import android.content.BroadcastReceiver
import android.content.Context
import android.os.Build
import android.provider.AlarmClock
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.myapplication.data.core.workmanager.AlarmService
import com.myapplication.data.core.workmanager.NotificationWorker
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.domain.core.Constants


class MyNotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.e(null, "onReceive1: ${intent.action} ", )
//        val workManager = WorkManager.getInstance(context)
//        val intentData = intent.getBundleExtra("prayerObject")?.get("prayerTime")
//        var name: String = ""
//        if (intentData is PrayerTimeModel) {
//            name = intentData.name
//        }
//
//        val WorkRequest = NotificationWorker.buildWorkRequest(name)
//        workManager.enqueueUniqueWork(
//            NotificationWorker.UNIQUE_WORK_NAME,
//            ExistingWorkPolicy.KEEP,
//            WorkRequest
//        )
        Log.e(null, "onReceive1: ${intent.action} ", )
        if (intent.action == "Trigger") {

            Log.e(null, "onReceive2: triggered ", )
            val workManager = WorkManager.getInstance(context)
            val intentData = intent.getBundleExtra("prayerObject")?.get("prayerTime")
            var name: String = ""
            if (intentData is PrayerTimeModel) {
                name = intentData.name
            }

            val WorkRequest = NotificationWorker.buildWorkRequest(name)
            workManager.enqueueUniqueWork(
                NotificationWorker.UNIQUE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                WorkRequest
            )
        } else if (intent.action == "Off") {
            Log.e(null, "onReceive2: off ", )
        }
//        val notification: Notification = intent.getParcelableExtra(NOTIFICATION)!!
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val notificationChannel = NotificationChannel(
//                Constants.NOTIFICATION_CHANNEL_ID,
//                "NOTIFICATION_CHANNEL_NAME",
//                importance
//            )
//            assert(notificationManager != null)
//            notificationManager.createNotificationChannel(notificationChannel)
//        }
//        val id = intent.getIntExtra(NOTIFICATION_ID, 0)
//        assert(notificationManager != null)
//        notificationManager.notify(id, notification)
//    }
    }

    companion object {
        var NOTIFICATION_ID = "notification-id"
        var NOTIFICATION = "notification"
    }
}
