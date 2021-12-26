package com.myapplication.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import com.myapplication.R
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.app.AlarmManager
import android.widget.Toast
import java.util.*
import androidx.room.Update
import com.myapplication.PrayerTimes
import android.content.ComponentName
import java.text.DateFormat
import java.text.SimpleDateFormat


class PrayerTimesWidget : AppWidgetProvider() {
    val ACTION_AUTO_UPDATE_WIDGET = "com.myapplication.widgets.action.ACTION_AUTO_UPDATE_WIDGET"
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
        val timeDiffInMillis: Long = totalTime - System.currentTimeMillis()

        Log.d("setAlramManager", " is called $alarmMgr")

        val alarmUp = PendingIntent.getBroadcast(
            context, 0,
            Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE),
            PendingIntent.FLAG_NO_CREATE
        ) != null

        if (alarmUp) {
            Log.d("myTag", "Alarm is already active $timeDiffInMillis")
        } else {
            Log.d("myTag", "Alarm is already Not Active $timeDiffInMillis")

        }
    }

    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, intent, 0))
    }

    lateinit var localTime: String

    override fun onReceive(context: Context?, intent: Intent?) {
        RemoteViews(context!!.packageName, R.layout.prayer_times_widget).also {
        }

        Log.d("laksdlsakdlasd", "getWidget ID $intent")
        super.onReceive(context, intent)
        if (intent!!.action != null) {
            val extras = intent!!.extras
            Log.d("laksdlsakdlasd", "getWidget ID ")
            if (extras != null) {
                super.onReceive(context, intent)
                val widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

                val viewsPrayer = RemoteViews(context.packageName, R.layout.prayer_times_widget)
                var currentDayHijri = " "

                // do something for the widget that has appWidgetId = widgetId
                Log.d("laksdlsakdlasd", "getWidget ID Prayer" + widgetId.toString() + "$widgetsId")
                if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
                    // do something useful here
                    Log.d(
                        "laksdlsakdlasd22",
                        "getWidget ID Prayer" + widgetId.toString() + "$widgetsId"
                    )
                    val cal = Calendar.getInstance()
                    val georgianDateFormatForInsertion: DateFormat =
                        SimpleDateFormat("dd-MM-yyyy", Locale("en"))
                    localTime = georgianDateFormatForInsertion.format(cal.time)

                    val db = MuslemNowDataBase.getDataBase(context)
                    val prayerDao = db.alAdahanDao()

                    GlobalScope.launch {
                        val x: Flow<List<PrayerTimeModel>> =
                            prayerDao.getSpecificDayPrayerTimes(localTime)
                        val y = x.collect {
                            it.map { prayerTime ->
                                prayerTime.prayerId
                                currentDayHijri = it[0].hijriDate

                                viewsPrayer.setTextViewText(R.id.tvFajrTime, it[0].time)
                                viewsPrayer.setTextViewText(R.id.tvSunRiseTime, it[1].time)
                                viewsPrayer.setTextViewText(R.id.tvDuhrTime, it[2].time)
                                viewsPrayer.setTextViewText(R.id.tvAsrTime, it[3].time)
                                viewsPrayer.setTextViewText(R.id.tvMaghribTime, it[4].time)
                                viewsPrayer.setTextViewText(R.id.tvIshaTime, it[5].time)
                                viewsPrayer.setTextViewText(R.id.tvCalendarDate, currentDayHijri)

                                AppWidgetManager.getInstance(context).updateAppWidget(
                                    ComponentName(context, PrayerTimesWidget::class.java),
                                    viewsPrayer
                                )
                            }
                        }

                    }

                }

            }

        } else {
            Log.d("laksdlsakdlasd", "getWidget ID " + "null")
        }
    }

    var widgetsId = 0
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val db = MuslemNowDataBase.getDataBase(context)
        val prayerDao = db.alAdahanDao()
        var currentDayHijri = " "
        var currentFajr = " "
//        var timings = PrayerTimeModel
        GlobalScope.launch {
            val x: Flow<List<PrayerTimeModel>> = prayerDao.getSpecificDayPrayerTimes("24-12-2021")
            val y = x.collect {
                it.map { prayerTime ->
                    prayerTime.prayerId
                    currentDayHijri = it[0].hijriDate
                    currentFajr = it[0].time
                }
            }
            Log.d("getCurrentDay", "lsdlsdlsd $y")
            /*val y = x.observe(ProcessLifecycleOwner.get(), androidx.lifecycle.Observer { prayer ->
                Log.d("prayerCurrentDay", prayer[0].date)
            })*/

        }
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (i in 0 until appWidgetIds.size) {
            val currentWidgetId = appWidgetIds[0]
            widgetsId = currentWidgetId
            Log.d("asdlasdlasdl", currentWidgetId.toString())
            val viewsPrayer = RemoteViews(context.packageName, R.layout.prayer_times_widget)
            val intentUpdatePrayer = Intent(Intent.ACTION_VIEW)
            intentUpdatePrayer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intentUpdatePrayer.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val idArrayPrayerTimes = intArrayOf(currentWidgetId)

            intentUpdatePrayer.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArrayPrayerTimes);
            val pendingUpdatePrayer = PendingIntent.getBroadcast(
                context, currentWidgetId, intentUpdatePrayer,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
//            viewsPrayer.setOnClickPendingIntent(R.id.ivFajr, pendingUpdatePrayer)
/*
            viewsPrayer.setTextViewText(R.id.tvCalendarDate, currentDayHijri)
            viewsPrayer.setTextViewText(R.id.tvFajrTime, currentFajr)
*/


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