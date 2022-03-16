package com.myapplication.widgets

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.myapplication.LocaleUtil
import com.myapplication.LocaleUtil.Companion.nextPrayer
import com.myapplication.R
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import android.preference.PreferenceManager


class NextPrayerWidget : AppWidgetProvider() {
    val ACTION_AUTO_UPDATE_WIDGET = "com.myapplication.widgets.action.ACTION_AUTO_UPDATE_WIDGET"
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)

   /*     val am = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmManagerBroadcastReceiver::class.java)
        val pi = PendingIntent.getBroadcast(context, 0, intent, 0)
        //After after 3 seconds
        //After after 3 seconds
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 100 * 3, 1000, pi)
*/

        val intentAc = Intent(ACTION_AUTO_UPDATE_WIDGET)
        val pending = PendingIntent.getService(context, 1235, intentAc, 0)
        val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val interval = (1000 * 60).toLong()

        alarmMgr.setInexactRepeating(
            AlarmManager.RTC,
            interval,
            AlarmManager.INTERVAL_DAY,
            pending
        )
        val alarmManager = AppWidgetAlarm(context)
        alarmManager.startAlarm()
   /*     val alarm = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.cancel(pending)
        val interval = (1000 * 60).toLong()
        alarm.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime(),
            interval,
            pending
        )*/

    }
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)

/*        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
        val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(PendingIntent.getBroadcast(context, 0, intent, 0))*/
    }
    lateinit var localTime: String
    var timeFormat = "k:mm"

    override fun onReceive(context: Context?, intent: Intent?) {
        RemoteViews(context!!.packageName, R.layout.next_prayer_widget).also {
        }
        val extras = intent!!.extras
        val alarmUp = PendingIntent.getBroadcast(
            context, 1235,
            Intent(ACTION_AUTO_UPDATE_WIDGET),
            PendingIntent.FLAG_NO_CREATE
        ) != null

        if (alarmUp) {
        }else{

        }
/*        if (intent!!.action != null) {
            val extras = intent!!.extras
            if (extras != null) {*/
                super.onReceive(context, intent)
                val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                val pending = PendingIntent.getService(context, 0, intent, 0)
/*

                val c: Calendar = Calendar.getInstance()
                c.set(Calendar.HOUR_OF_DAY, 0)
                c.set(Calendar.MINUTE, 0)
                c.set(Calendar.SECOND, 20)
                c.set(Calendar.MILLISECOND, 1)*/
                val alarmMgr = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmMgr.setInexactRepeating(
                    AlarmManager.RTC,
                    1*60*1000,
                    AlarmManager.INTERVAL_DAY,
                    pending
                )
                val widgetId = extras?.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID
                )
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)

                val viewsPrayer = RemoteViews(context.packageName, R.layout.next_prayer_widget)
                var currentDayHijri = " "

                if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
                    // do something useful here
                    val cal = Calendar.getInstance()
                    val georgianDateFormatForInsertion: DateFormat =
                        SimpleDateFormat("dd-MM-yyyy", Locale("en"))
                    localTime = georgianDateFormatForInsertion.format(cal.time)
                    val currentHourDateFormat: DateFormat = SimpleDateFormat("HH:mm",Locale("en"))
                    val currentHour = currentHourDateFormat.format(cal.time)
                    val dateFormatForChecking: DateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy",Locale("en"))
                    val currentDateForChecking = dateFormatForChecking.format(cal.time)

                    val db = MuslemNowDataBase.getDataBase(context)
                    val prayerDao = db.alAdahanDao()
                    var prayerList: MutableList<PrayerTimeModel> = ArrayList()

                    GlobalScope.launch {
                        val x: Flow<List<PrayerTimeModel>> =
                            prayerDao.getSpecificDayPrayerTimes(localTime)
                        val y = x.collect {
                            it.map { prayerTime ->

                                 var currentPrayerId = nextPrayer(it.toMutableList(),currentHour)

                                if (currentPrayerId != 300) {
                                    prayerTime.prayerId
                                    currentDayHijri = it[0].hijriDate
                                    val prefs =  PreferenceManager.getDefaultSharedPreferences(context)
                                    var city = prefs.getString("city","Cairo")

                                    val nextPrayerTime =
                                        it[currentPrayerId].time + " " + it[currentPrayerId].date
                                    val previousPrayerTime = it[currentPrayerId-1].time
                                    val remainingTimeForNextPrayer =
                                        LocaleUtil.remainingTimeForNextPrayer(
                                            currentDateForChecking,
                                            nextPrayerTime
                                        )
                                    viewsPrayer.setTextViewText(R.id.remainingTimeForNextPrayerWidget,
                                        remainingTimeForNextPrayer)
                                    viewsPrayer.setTextViewText(R.id.currentCityTv,
                                        city)
                                    viewsPrayer.setTextViewText(R.id.nextPrayerNameTv,
                                        LocaleUtil.getNameOfPrayerInArabic(
                                            currentPrayerId
                                        )
                                    )
                                    viewsPrayer.setTextViewText(R.id.currentPrayerTv,
                                        LocaleUtil.getNameOfPrayerInArabic(
                                            currentPrayerId-1
                                        )
                                    )
                                    viewsPrayer.setTextViewText(R.id.currentPrayerTimeTv,
                                        previousPrayerTime
                                    )
                                    viewsPrayer.setImageViewResource(R.id.currentPrayerIv,
                                        LocaleUtil.setDrawable(currentPrayerId-1)
                                    )

                                }
                                AppWidgetManager.getInstance(context).updateAppWidget(
                                    ComponentName(context, NextPrayerWidget::class.java),
                                    viewsPrayer
                                )
                            }
                        }
                    }
                }else{

                }
            }
//        }
//    }

}