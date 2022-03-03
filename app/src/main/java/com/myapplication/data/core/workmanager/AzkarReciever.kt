package com.myapplication.data.core.workmanager

import android.app.Notification
import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.myapplication.MainActivity
import com.myapplication.R
import androidx.core.content.ContextCompat.getSystemService

import android.app.NotificationManager
import android.app.PendingIntent

import android.os.Build
import com.myapplication.ui.azkar.AzkarActivity
import com.myapplication.ui.settings.AzkarSettingsFragment
import kotlinx.coroutines.InternalCoroutinesApi


class AzkarReciever : BroadcastReceiver() {
    @InternalCoroutinesApi
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            Log.d("onRecieveSIasdlk" , " is called Sabah")
            if (intent.action == "AzkarSabah") {
                createNotification(context, intent.getStringExtra("azkar").toString(),"أذكار الصباح","1")
//                var azkarFragment = AzkarSettingsFragment()
//                azkarFragment.setAlarmAzkarSabahDaily("10:45","AzkarSabah")
                Log.d("onRecieveSIasdlk" , " is called Sabah")
            } else if (intent.action == "AzkarMasaa") {
                createNotification(context, intent.getStringExtra("azkar").toString(),"أذكار المساء","2")
                Log.d("onRecieveSIasdlk" , " is called Masaa")
            }


        }
        context.startService(Intent(context, MainActivity::class.java))
    }

    private fun createNotification(context: Context, title: String , category : String
                                   ,channelID:String): Notification {
        val notifyIntent = Intent(context, AzkarActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        notifyIntent.putExtra("category",category)
        val notifyPendingIntent = PendingIntent.getActivity(
            context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        var builder = NotificationCompat.Builder(context, channelID)
            .setSmallIcon(R.drawable.arabic)
            .setContentTitle(title)
//            .setContentText("textContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(notifyPendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = title
            val descriptionText = ""
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(2, builder.build())

        }

        return builder.build()

    }

}