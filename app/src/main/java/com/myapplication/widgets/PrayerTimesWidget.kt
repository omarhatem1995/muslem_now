package com.myapplication.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
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

class PrayerTimesWidget : AppWidgetProvider() {

    override fun onReceive(context: Context?, intent: Intent?) {
        RemoteViews(context!!.packageName, R.layout.qibla_widget).also {
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        for (i in 0 until appWidgetIds.size) {
            val currentWidgetId = appWidgetIds[i]
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
            viewsPrayer.setTextViewText(
                R.id.prayerTvPrayer,
                "This is Prayer Times  "
            )

            val db = MuslemNowDataBase.getDataBase(context)
            val prayerDao = db.alAdahanDao()
            var currentDayHijri = " "
            GlobalScope.launch {
                val x : Flow<List<PrayerTimeModel>> = prayerDao.getSpecificDayPrayerTimes("24-12-2021")
                val y = x.collect {
                    it.map { prayerTime ->
                        prayerTime.prayerId
                        currentDayHijri = it[0].hijriDate
                        Log.d("getCurrentDay" , "lsdlsdlsd ${it.size}")
                        Log.d("getCurrentDay" , "lsdlsdlsd ${it[0].hijriDate}")

                    }
                }
                Log.d("getCurrentDay" , "lsdlsdlsd $y")
                /*val y = x.observe(ProcessLifecycleOwner.get(), androidx.lifecycle.Observer { prayer ->
                    Log.d("prayerCurrentDay", prayer[0].date)
                })*/
                viewsPrayer.setTextViewText(R.id.tvCalendarDate,"currentDayHijri")

            }
        }
        }

}