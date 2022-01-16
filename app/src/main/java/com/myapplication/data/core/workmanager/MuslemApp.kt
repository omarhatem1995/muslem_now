package com.myapplication.data.core.workmanager


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.work.*
import com.myapplication.R
import com.myapplication.common.Constants
import com.myapplication.data.repositories.SharedPreferencesRepository


class MuslemApp : Application() {

    companion object {
        val CHANNEL_FULL_AZAN_MESHARY = "Meshary channel"
        val CHANNEL_FULL_AZAN_HOSARY = "Hosary channel"
        val CHANNEL_TAKBIRAT_AZAN_HOSARY = "Takbirat channel"
        val channelName1 = "Channel Meshary"
        val channelName2 = "Channel Hosary"
        val channelName3 = "Channel Takbirat Hosary"
    }


    override fun onCreate() {
       // createNotificationChannels()
        try {
            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    "ElSalahWorker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        } catch (e: Exception) {
            Log.e(null, "onCreate: ${e.message}")
        }

        super.onCreate()
    }


    private val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .apply {

            setRequiresDeviceIdle(false)

        }
        .setRequiresCharging(false)
        .build()

    private val request = PeriodicWorkRequestBuilder<ElSalahWorker>(
        3,
        java.util.concurrent.TimeUnit.HOURS
    )
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR, PeriodicWorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,
            java.util.concurrent.TimeUnit.MILLISECONDS
        )
        .addTag("settingAlarm")
        .build()


    private fun createNotificationChannels() {
        val preference = SharedPreferencesRepository(this)
        val channelID: String
        val channelName: String
        val sound: Uri
        if (preference.getAzanType().equals(Constants.FULL_AZAN) && preference.getMoazen().equals(Constants.AZANELHOSARY)) {
            channelName = channelName2
            channelID = CHANNEL_FULL_AZAN_HOSARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        } else if (preference.getAzanType().equals(Constants.FULL_AZAN) && preference.getMoazen().equals(Constants.AZANMESHARY)) {
            channelID = CHANNEL_FULL_AZAN_MESHARY
            channelName = channelName1
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.meshary)
        }else if(preference.getAzanType().equals(Constants.TAKBIRAT_ONLY)) {
            channelID = CHANNEL_TAKBIRAT_AZAN_HOSARY
            channelName = channelName3
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan)
        }else {
            channelID = CHANNEL_FULL_AZAN_HOSARY
            channelName = channelName2
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        }
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel1 = NotificationChannel(
                channelID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            channel1.description = "This is Azan Notification Channel"
            channel1.setSound(
                sound,
                attributes
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            manager.createNotificationChannel(channel1)

        }
    }
}