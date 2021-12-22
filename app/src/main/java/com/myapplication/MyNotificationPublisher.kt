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
import com.myapplication.data.core.workmanager.AlarmService
import com.myapplication.domain.core.Constants


class MyNotificationPublisher : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (intent.action == "Trigger")
        {
            val serviceIntent = Intent(context,AlarmService::class.java)
            serviceIntent.putExtra("PrayerItem",intent.getBundleExtra("prayerObject"))
            AlarmService.enqueueWork(context,serviceIntent)
        }else if (intent.action == "Off")
        {

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
//
//    companion object {
//        var NOTIFICATION_ID = "notification-id"
//        var NOTIFICATION = "notification"
   }
}