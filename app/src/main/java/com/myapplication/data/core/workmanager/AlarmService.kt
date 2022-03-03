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
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class AlarmService : LifecycleService() {


    val notificationId = 1001





    @InternalCoroutinesApi
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

                        startForeground(notificationId, createSalahNotification(name,preference))
                        if (azkarState)
                        {
                            delay(300000)
                            startForeground(notificationId, createAzkarNotification(name,preference))
                            delay(1800000)
                            stopForeground(true)
                            stopSelf()

                        }else
                        {
                            delay(300000)
                            stopForeground(true)
                            stopSelf()
                        }



                }


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
            try {
                manager.deleteNotificationChannel(oldChannel2)
            }catch (e:Exception)
            {
                e.printStackTrace()
            }

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
        intent.putExtra("category","اذكار ما بعد الاذان")

        val azkarPendingIntent: PendingIntent =
            intent.let { notificationIntent ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                }else
                {
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                }
            }

        val builder = provideNotificationBuilder(name,"حان وقت الازكار",channnelID2,azkarPendingIntent,null)
        return builder.build()
    }


    //
    @InternalCoroutinesApi
    private fun createSalahNotification(title: String, preference:SharedPreferencesRepository): Notification {
        val calendar: Calendar = Calendar.getInstance()
        val manager = getSystemService(
            NotificationManager::class.java
        )


        // Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        // Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.meshary)
        // Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan)
        //
        //
        var oldChannel = preference.preference.getString("ChannelId",null)
        if (oldChannel != null)
        {
            try {
                manager.deleteNotificationChannel(oldChannel)
            }catch (e:Exception)
            {
                e.printStackTrace()
            }


        }

        val channnelID: String = "MuslimChannel +${calendar.timeInMillis}"
        preference.preference.edit().putString("ChannelId",channnelID).apply()
        var sound: Uri? = null


        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                }else
                {
                    PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                }

            }





        val builder = provideNotificationBuilder(title,title,channnelID,pendingIntent,sound)
        return builder.build()
    }


    private fun provideNotificationBuilder(salahTitle:String, notificationTitle:String, channelId:String, pending:PendingIntent,sound:Uri?):NotificationCompat.Builder
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
            .setAutoCancel(true)
            .setContentIntent(pending)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(sound)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

    }

}