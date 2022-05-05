package com.zaker.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.room.Update
import com.zaker.R
import com.zaker.data.entities.model.AzkarModel
import com.zaker.data.gateways.dao.MuslemNowDataBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*

class AzkarWidget: AppWidgetProvider() {
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

        val c: Calendar = Calendar.getInstance()
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 1)
        val totalTime: Long = c.getTimeInMillis()

        val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.setInexactRepeating(
            AlarmManager.RTC,
            c.getTimeInMillis(),
            AlarmManager.INTERVAL_DAY,
            alarmIntent
        )

    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, intent, 0))
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        RemoteViews(context!!.packageName, R.layout.prayer_times_widget).also {
        }
        super.onReceive(context, intent)
        if (intent!!.action != null) {
            val extras = intent!!.extras
            if (extras != null) {
                super.onReceive(context, intent)
                val widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                val viewsAzkar = RemoteViews(context.packageName, R.layout.azkar_widget)
                if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
                    val db = MuslemNowDataBase.getDataBase(context)
                    val prayerDao = db.alAzkarDao()
                    val rnds = (0..137).random()
                    GlobalScope.launch {
                        val x: Flow<AzkarModel> =
                            prayerDao.getSpecificAzkar(rnds.toLong())
                        val y = x.collect {
                            viewsAzkar.setTextViewText(R.id.tvZekr, it.zekr)

                            AppWidgetManager.getInstance(context).updateAppWidget(
                                ComponentName(context, AzkarWidget::class.java),
                                viewsAzkar
                            )
                        }
                    }
                }

            }


        } else {
        }
    }
    var widgetsId = 0

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val db = MuslemNowDataBase.getDataBase(context)
        val azkarDao = db.alAzkarDao()
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (i in 0 until appWidgetIds.size) {
            val currentWidgetId = appWidgetIds[0]
            widgetsId = currentWidgetId
            val viewsPrayer = RemoteViews(context.packageName, R.layout.azkar_widget)
            val intentUpdateAzkar = Intent(Intent.ACTION_VIEW)
            intentUpdateAzkar.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intentUpdateAzkar.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val idArrayAzkar = intArrayOf(currentWidgetId)

            intentUpdateAzkar.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArrayAzkar);
            val pendingUpdateAzkar = PendingIntent.getBroadcast(
                context, currentWidgetId, intentUpdateAzkar,
                PendingIntent.FLAG_UPDATE_CURRENT
            )

            var alarmManager: AlarmManager
            val intent = Intent(context, Update::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT
            )
            alarmManager = context
                .getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val cal = Calendar.getInstance()
            cal.timeInMillis = System.currentTimeMillis()
            cal.add(Calendar.SECOND, 10)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, cal
                    .timeInMillis, (5 * 1000).toLong(), pendingIntent
            )
            appWidgetManager.updateAppWidget(currentWidgetId, viewsPrayer)

        }
    }

    }



