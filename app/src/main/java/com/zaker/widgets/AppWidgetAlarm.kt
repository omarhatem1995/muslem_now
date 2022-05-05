package com.zaker.widgets

import android.app.AlarmManager

import android.app.PendingIntent
import android.content.Context

import android.content.Intent
import java.util.*


class AppWidgetAlarm(context: Context) {
    private val ALARM_ID = 0
    private val INTERVAL_MILLIS = 10000
    private val mContext: Context
    val ACTION_AUTO_UPDATE_WIDGET = "com.myapplication.widgets.action.ACTION_AUTO_UPDATE_WIDGET"

    fun startAlarm() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.MILLISECOND, INTERVAL_MILLIS)
        val alarmIntent = Intent(mContext, NextPrayerWidget::class.java)
        alarmIntent.action = ACTION_AUTO_UPDATE_WIDGET
        val pendingIntent = PendingIntent.getBroadcast(
            mContext,
            ALARM_ID,
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // RTC does not wake the device up
        alarmManager.setRepeating(
            AlarmManager.RTC,
            calendar.getTimeInMillis(),
            1000 * 60,
            pendingIntent
        )
    }

    fun stopAlarm() {
        val alarmIntent = Intent(ACTION_AUTO_UPDATE_WIDGET)
        val pendingIntent = PendingIntent.getBroadcast(
            mContext,
            ALARM_ID,
            alarmIntent,
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    init {
        mContext = context
    }
}