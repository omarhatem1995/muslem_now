package com.myapplication.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.myapplication.R
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.dao.aladahangateway.AlAdahanDao
import com.myapplication.ui.fragments.home.HomeViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.os.Bundle


class PrayerTimesWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        RemoteViews(context!!.packageName, R.layout.qibla_widget).also {
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
                // do something for the widget that has appWidgetId = widgetId
                Log.d("laksdlsakdlasd", "getWidget ID Prayer" + widgetId.toString() + "$widgetsId")

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
            viewsPrayer.setOnClickPendingIntent(R.id.prayerTime, pendingUpdatePrayer)
            viewsPrayer.setTextViewText(R.id.tvCalendarDate, currentDayHijri)
            viewsPrayer.setTextViewText(R.id.tvFajrTime, currentFajr)

            appWidgetManager.updateAppWidget(currentWidgetId, viewsPrayer)

        }
    }

}