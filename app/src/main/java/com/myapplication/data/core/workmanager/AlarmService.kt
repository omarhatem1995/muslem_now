package com.myapplication.data.core.workmanager


import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.repositories.SharedPreferencesRepository

class AlarmService : Service() {


    val notificationId = 1001


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent != null) {
            Log.e("foregroundService", "onStartCommand:${intent.action} ")
        }
        if (intent != null) {
            if (intent.action == "Trigger") {
                val intentData = intent?.getBundleExtra("prayerObject")?.get("prayerTime")
                var name: String = ""
                if (intentData is PrayerTimeModel) {
                    name = intentData.name
                }

                startForeground(notificationId, createNotification(name))
            } else {
                stopSelf()
            }
        }


        return START_STICKY
    }


    //
    private fun createNotification(title: String): Notification {
        val app: MuslemApp = applicationContext as MuslemApp
        val preference = SharedPreferencesRepository(app)
        var channnelID: String
        Log.d("getElMoazen", " is : " + preference.getMoazen().toString())
        val sound: Uri
        if (preference.getAzanType().equals(Constants.FULL_AZAN) && preference.getMoazen()
                .equals(Constants.AZANELHOSARY)
        ) {
            channnelID = MuslemApp.CHANNEL_FULL_AZAN_HOSARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        } else if (preference.getAzanType().equals(Constants.FULL_AZAN) && preference.getMoazen()
                .equals(Constants.AZANMESHARY)
        ) {
            channnelID = MuslemApp.CHANNEL_FULL_AZAN_MESHARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.meshary)
        } else if (preference.getAzanType().equals(Constants.TAKBIRAT_ONLY)) {
            channnelID = MuslemApp.CHANNEL_TAKBIRAT_AZAN_HOSARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan)

        } else {
            channnelID = MuslemApp.CHANNEL_FULL_AZAN_HOSARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        }
        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, 0)
            }
        val builder = NotificationCompat.Builder(applicationContext, channnelID)
            .setContentTitle(title)
            .setSmallIcon(
                when (title) {
                    "Fajr" -> {
                        R.drawable.ic_elfajr
                    }
                    "Dhur" -> {
                        R.drawable.ic_eldhur
                    }
                    "Asr" -> {
                        R.drawable.ic_elasr
                    }
                    "Maghrib" -> {
                        R.drawable.ic_elmaghrib
                    }
                    "Isha" -> {
                        R.drawable.ic_elisha
                    }
                    else -> {
                        R.drawable.muslem_now_logo
                    }
                }
            )
            .setOngoing(false)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(sound)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        return builder.build()
    }

}