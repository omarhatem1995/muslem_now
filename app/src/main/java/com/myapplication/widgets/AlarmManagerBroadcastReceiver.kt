package com.myapplication.widgets

import android.appwidget.AppWidgetManager

import android.content.ComponentName


import android.R
import android.content.BroadcastReceiver
import android.content.Context

import android.widget.RemoteViews

import android.os.PowerManager

import android.content.Intent
import android.util.Log


class AlarmManagerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
  /*      val wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG")
        //Acquire the lock
        wl.acquire()*/
        Log.d("onRecieveAlarm", " is called")
        //You can do the processing here update the widget/remote views.
        /*val remoteViews = RemoteViews(
            context.getPackageName(),
            R.id.remainingTimeForNextPrayerWidget
        )
        remoteViews.setTextViewText(
            R.id.tvTime,
            com.sun.org.apache.bcel.internal.classfile.Utility.getCurrentTime("hh:mm:ss a")
        )*/
        val thiswidget = ComponentName(context, NextPrayerWidget::class.java)
        val manager = AppWidgetManager.getInstance(context)
//        manager.updateAppWidget(thiswidget, remoteViews)
        //Release the lock
//        wl.release()
    }
}