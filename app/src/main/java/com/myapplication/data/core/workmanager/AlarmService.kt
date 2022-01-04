package com.myapplication.data.core.workmanager


import android.app.*
import android.content.ContentResolver
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.repositories.SharedPreferencesRepository
import java.util.*

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
        val calendar: Calendar = Calendar.getInstance()
        val manager = getSystemService(
            NotificationManager::class.java
        )


        // Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        // Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.meshary)
        // Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan)
        //
        //
        val oldChannel = preference.preference.getString("ChannelId",null)
        if (oldChannel != null)
        {
            manager.deleteNotificationChannel(oldChannel)
        }

        val channnelID: String = "MuslimChannel +${calendar.timeInMillis}"
        preference.preference.edit().putString("ChannelId",channnelID).apply()
        var sound: Uri? = null


        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()







        Log.d("getElMoazen", " is : " + preference.getMoazen().toString())

        //preference.getAzanType().equals(Constants.FULL_AZAN) &&
        Log.e(null, "createNotification: ${preference.getMoazen()}  ", )
        if ( preference.getAzanType().equals(Constants.FULL_AZAN) &&preference.getMoazen()
                .equals(Constants.AZANELHOSARY)
        ) {
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
            //preference.getAzanType().equals(Constants.FULL_AZAN) &&
        } else if (preference.getAzanType().equals(Constants.FULL_AZAN) &&preference.getMoazen()
                .equals(Constants.AZANMESHARY)
        ) {
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.meshary)
        } else if (preference.getAzanType().equals(Constants.TAKBIRAT_ONLY)) {

            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan)

        } else {
            Log.e(null, "createNotification: else", )
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        }

        val channel1 = NotificationChannel(
            channnelID,
            "AzanChannel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel1.description = "This is Azan Notification Channel"

        channel1.setSound(
            sound,
            attributes
        )

        manager.createNotificationChannel(channel1)

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
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        return builder.build()
    }

}