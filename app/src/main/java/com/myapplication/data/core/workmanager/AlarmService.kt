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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.repositories.SharedPreferencesRepository
import com.myapplication.ui.azkar.AzkarActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class AlarmService : LifecycleService() {


    val notificationId = 1001





    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

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
                val app: MuslemApp = applicationContext as MuslemApp

                val preference = SharedPreferencesRepository(app)

                val azkarState = preference.getAzkarAfterAzan()

                this.lifecycleScope.launch(Dispatchers.Main) {
                    lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED)
                    {
                        startForeground(notificationId, createSalahNotification(name,preference))
                        if (azkarState)
                        {
                            delay(180000)
                            startForeground(notificationId, createAzkarNotification(name,preference))
                        }

                    }

                }


            } else {
                stopSelf()
            }
        }


        return START_STICKY
    }

    private fun createAzkarNotification(name: String,preference:SharedPreferencesRepository): Notification? {

        val calendar: Calendar = Calendar.getInstance()
        val manager = getSystemService(
            NotificationManager::class.java
        )

        val oldChannel2 = preference.preference.getString("ChannelId2",null)
        if (oldChannel2 != null)
        {
            manager.deleteNotificationChannel(oldChannel2)
        }
        val channnelID2: String = "AzkarChannel +${calendar.timeInMillis}"
        preference.preference.edit().putString("ChannelId2",channnelID2).apply()

        val channel2 = NotificationChannel(
            channnelID2,
            "AzKarChannel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel2.description = "This is Azkar Notification Channel"
        manager.createNotificationChannel(channel2)

        val intent =  Intent(this, AzkarActivity::class.java)
        intent.putExtra("category","أذكار الصباح")

        val azkarPendingIntent: PendingIntent =
            intent.let { notificationIntent ->
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }

        val builder = provideNotificationBuilder(name,"حان وقت الازكار",channnelID2,azkarPendingIntent)
        return builder.build()
    }


    //
    private fun createSalahNotification(title: String,preference:SharedPreferencesRepository): Notification {
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
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            }





        val builder = provideNotificationBuilder(title,title,channnelID,pendingIntent)
        return builder.build()
    }


    private fun provideNotificationBuilder(salahTitle:String, notificationTitle:String, channelId:String, pending:PendingIntent):NotificationCompat.Builder
    {
        return NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(notificationTitle)
            .setSmallIcon(
                when (salahTitle) {
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
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

    }

}