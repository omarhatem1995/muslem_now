package com.myapplication.data.core.workmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.BundleCompat
import androidx.core.content.ContextCompat.getSystemService

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.myapplication.MyNotificationPublisher
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.dao.aladahangateway.AlAdahanDao
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ElSalahWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {
    @DelicateCoroutinesApi
    override suspend fun doWork(): Result {

        return try {
            setSalahAlarm()
            Result.success()
        }catch (e:Exception)
        {
            Result.retry()

        }

    }



     @DelicateCoroutinesApi
    // @RequiresApi(Build.VERSION_CODES.S)
     fun setSalahAlarm()
    {
        val c: Date = Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        Log.e(null, "getCurrentDate: ${df.format(c)} ")
       // df.calendar.timeInMillis
        val formattedDate: String = df.format(c)
        val dao = MuslemNowDataBase.getDataBase(applicationContext).alAdahanDao()
        val alarmManager: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var hasPermission: Boolean? = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission = alarmManager.canScheduleExactAlarms()
        }

        val intent = Intent(applicationContext, MyNotificationPublisher::class.java)
        intent.action = "Trigger"
        val intent2 = Intent(applicationContext, MyNotificationPublisher::class.java)
        intent2.action = "Off"
        //setting up a pending intent
        val goOffPendingIntent = PendingIntent.getBroadcast(applicationContext, 999, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        //var alarmPendingIntent:PendingIntent = PendingIntent.getService(applicationContext, 777, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            alarmPendingIntent = PendingIntent.getForegroundService(applicationContext, 777, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//        }


        GlobalScope.launch {
            val prayerTimes = dao.getSpecificDayPrayerTimes(formattedDate).collect {

               val calendar:Calendar = Calendar.getInstance()


                it.map { prayerTime ->
                    calendar.set(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        separatenumbers(prayerTime.time)[0],
                        separatenumbers(prayerTime.time)[1],
                        0
                    )

                    val bundle: Bundle = Bundle()
                    bundle.putParcelable("prayerTime",prayerTime)
                    intent.putExtra("prayerObject",bundle)

                    val alarmPendingIntent:PendingIntent = PendingIntent.getBroadcast(applicationContext, 777, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis,alarmPendingIntent)
                    if (hasPermission == true)
                    {
                        alarmManager.setAlarmClock(alarmInfo,goOffPendingIntent)
                    }

                }
            }
        }

    }


    fun separateNumbers(time:String):ArrayList<Int>
    {
       val editedTime = time.dropWhile {
            it == ':'
        }

       val hour = editedTime.substring(0,1).toInt()
        val min = editedTime.substring(2,3).toInt()

        val timeArray :ArrayList<Int> = arrayListOf()
        timeArray.add(hour)
        timeArray.add(min)


        return timeArray

    }

    fun separatenumbers(time:String):ArrayList<Int>
    {
       val hour = time.substringBefore(':').toInt()
        val min = time.substringAfter(':').toInt()
        val timeArray :ArrayList<Int> = arrayListOf()
        timeArray.add(hour)
        timeArray.add(min)


        return timeArray

    }


}