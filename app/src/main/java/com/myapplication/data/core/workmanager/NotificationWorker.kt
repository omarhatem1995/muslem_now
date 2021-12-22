package com.myapplication.data.core.workmanager

import android.app.Notification
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.work.*
import com.myapplication.R
import com.myapplication.data.core.workmanager.MuslemApp.Companion.CHANNEL_1_ID

class NotificationWorker(context: Context, params: WorkerParameters): CoroutineWorker(context,params) {


    companion object {
        val UNIQUE_WORK_NAME: String = "ShowNotificationWork"
        private const val YOUR_PARAM = "YOUR_PARAM"

        fun buildWorkRequest(yourParameter: String): OneTimeWorkRequest {
            val data = Data.Builder().putString(YOUR_PARAM, yourParameter).build()
            return OneTimeWorkRequestBuilder<NotificationWorker>().apply { setInputData(data) }.build()
        }
    }



    override suspend fun doWork(): Result {
        try {
            val salahName: String? = inputData.getString(YOUR_PARAM)
            // code to be executed
            setForeground(createForegroundInfo(salahName!!))
            return Result.success()
        } catch (throwable: Throwable) {
            // clean up and log
            return Result.failure()
        }
    }






    private fun createForegroundInfo(title:String): ForegroundInfo {
        // generate a random id for each notification
        val notificationId = 1001
        return ForegroundInfo(notificationId, createNotification(title))
    }


    private fun createNotification(title:String): Notification {
        val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(id)



        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_1_ID)
            .setContentTitle(title)
            .setSmallIcon(when(title)
            {
                "Fajr" ->{ R.drawable.ic_elfajr}
                "Dhur" ->{R.drawable.ic_eldhur}
                "Asr" ->{R.drawable.ic_elasr}
                "Maghrib" ->{R.drawable.ic_elmaghrib}
                "Isha" ->{R.drawable.ic_elisha}
                else -> {R.drawable.muslem_now_logo}
            })
            .setOngoing(false)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound( Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + applicationContext.packageName + "/" + R.raw.azan))
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)

        return builder.build()
    }
    }
