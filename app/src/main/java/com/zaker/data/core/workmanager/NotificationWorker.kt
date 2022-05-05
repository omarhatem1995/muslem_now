package com.zaker.data.core.workmanager

import android.app.Notification
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.work.*
import com.zaker.R
import com.zaker.common.Constants
import com.zaker.data.core.workmanager.MuslemApp.Companion.CHANNEL_FULL_AZAN_MESHARY
import com.zaker.data.core.workmanager.MuslemApp.Companion.CHANNEL_FULL_AZAN_HOSARY
import com.zaker.data.core.workmanager.MuslemApp.Companion.CHANNEL_TAKBIRAT_AZAN_HOSARY
import com.zaker.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.InternalCoroutinesApi

class NotificationWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {


    companion object {
        val UNIQUE_WORK_NAME: String = "ShowNotificationWork"
        private const val YOUR_PARAM = "YOUR_PARAM"

        fun buildWorkRequest(yourParameter: String): OneTimeWorkRequest {
            val data = Data.Builder().putString(YOUR_PARAM, yourParameter).build()
            return OneTimeWorkRequestBuilder<NotificationWorker>().apply { setInputData(data) }
                .build()
        }
    }


    @InternalCoroutinesApi
    override suspend fun doWork(): Result {
        return try {
            val salahName: String? = inputData.getString(YOUR_PARAM)
            // code to be executed
            setForegroundAsync(createForegroundInfo(salahName!!))
            Result.success()
        } catch (throwable: Throwable) {
            // clean up and log
            Result.failure()
        }
    }


    @InternalCoroutinesApi
    private fun createForegroundInfo(title: String): ForegroundInfo {
        // generate a random id for each notification
        val notificationId = 1001
        return ForegroundInfo(notificationId, createNotification(title))
    }


    @InternalCoroutinesApi
    private fun createNotification(title: String): Notification {
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)
        val app: MuslemApp = applicationContext as MuslemApp
        val prefrences = SharedPreferencesRepository(app)
        var channnelID: String
        val sound: Uri
        if (prefrences.getAzanType().equals(Constants.FULL_AZAN) && prefrences.getMoazen()
                .equals(Constants.AZANELHOSARY)
        ) {
            channnelID = CHANNEL_FULL_AZAN_HOSARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
        } else if (prefrences.getAzanType().equals(Constants.FULL_AZAN) && prefrences.getMoazen()
                .equals(Constants.AZANMESHARY)
        ) {
            channnelID = CHANNEL_FULL_AZAN_MESHARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.meshary)
        } else if (prefrences.getAzanType().equals(Constants.TAKBIRAT_ONLY)) {
            channnelID = CHANNEL_TAKBIRAT_AZAN_HOSARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan)
        } else {
            channnelID = CHANNEL_FULL_AZAN_HOSARY
            sound =
                Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.elhosary)
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
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(sound)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        //.setContentIntent(intent)

        return builder.build()
    }
}
