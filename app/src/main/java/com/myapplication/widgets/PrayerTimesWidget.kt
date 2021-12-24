package com.myapplication.widgets

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.myapplication.R

class PrayerTimesWidget : QiblaWidget() {
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
        val thisWidget = ComponentName(
            context,
            QiblaWidget::class.java
        )
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }
}