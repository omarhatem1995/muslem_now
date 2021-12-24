package com.myapplication.data.core.workmanager


import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.data.entities.model.PrayerTimeModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class AlarmService : Service() {


    val notificationId = 1001


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            Log.e("foregroundService", "onStartCommand:${intent.action} ", )
        }
        if (intent != null) {
            if(intent.action == "Trigger" )
            {
                val intentData = intent?.getBundleExtra("prayerObject")?.get("prayerTime")
                var name: String = ""
                if (intentData is PrayerTimeModel) {
                    name = intentData.name
                }

                startForeground(notificationId,createNotification(name))
            }else{
                stopSelf()
            }
        }


        return START_STICKY
    }




    private fun createNotification(title:String): Notification {

        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        val builder = NotificationCompat.Builder(applicationContext, MuslemApp.CHANNEL_1_ID)
            .setContentTitle(title)
            .setSmallIcon(when(title)
            {
                "Fajr" ->{ R.drawable.ic_elfajr}
                "Dhur" ->{
                    R.drawable.ic_eldhur}
                "Asr" ->{
                    R.drawable.ic_elasr}
                "Maghrib" ->{
                    R.drawable.ic_elmaghrib}
                "Isha" ->{
                    R.drawable.ic_elisha}
                else -> {
                    R.drawable.muslem_now_logo}
            })
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound( Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.meshary))
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        return builder.build()
    }

}