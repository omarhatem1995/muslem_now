package com.myapplication.data.core.workmanager

import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.myapplication.LocaleUtil.Companion.prayerFilter
import com.myapplication.MainActivity
import com.myapplication.MyNotificationPublisher

import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.dao.aladahangateway.AlAdahanDao
import com.myapplication.data.repositories.SharedPreferencesRepository

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*
@DelicateCoroutinesApi
class ElSalahWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    private val sharedPreferencesRepository:SharedPreferencesRepository = SharedPreferencesRepository(
        appContext as Application
    )
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {

        return try {
            setSalahAlarm()
            Log.e(null, "doWork: success", )
            sharedPreferencesRepository.setAlarmState(true)
            Result.success()
        }catch (e:Exception)
        {
            Log.e(null, "doWork: failed", )
            Result.retry()

        }

    }



     @RequiresApi(Build.VERSION_CODES.O)
     @DelicateCoroutinesApi
    // @RequiresApi(Build.VERSION_CODES.S)
     fun setSalahAlarm()
    {
        val c: Date = Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("dd-MM-yyyy", Locale("en"))
        val currentHourDateFormat = SimpleDateFormat("HH:mm", Locale("en"))
        Log.e(null, "getCurrentDate: ${df.format(c)} ")
       // df.calendar.timeInMillis
        val formattedDate: String = df.format(c)
        val formattedDate2:String = currentHourDateFormat.format(c)
        val dao = MuslemNowDataBase.getDataBase(applicationContext).alAdahanDao()
        val alarmManager: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        var hasPermission: Boolean? = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            hasPermission = alarmManager.canScheduleExactAlarms()
        }

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.action = "edit"
        val intent2 = Intent(applicationContext, AlarmService::class.java)
        intent2.action = "Trigger"
        //setting up a pending intent

        //var alarmPendingIntent:PendingIntent = PendingIntent.getService(applicationContext, 777, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            alarmPendingIntent = PendingIntent.getForegroundService(applicationContext, 777, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//        }


        GlobalScope.launch {
            val prayerTimes = dao.getSpecificDayPrayerTimes(formattedDate).collect {

               val calendar:Calendar = Calendar.getInstance()

                val finalList = prayerFilter(it.toMutableList(),formattedDate2)


                finalList.map { prayerTime ->
                    calendar.set(
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH),
                        separatenumbers(prayerTime.time)[0],
                        separatenumbers(prayerTime.time)[1],
                        0
                    )

                    Log.e(null, "setSalahAlarm: ${calendar.time} ", )
                    val bundle: Bundle = Bundle()
                    bundle.putParcelable("prayerTime",prayerTime)
                    intent2.putExtra("prayerObject",bundle)

                    //FLAG_UPDATE_CURRENT
                    val alarmPendingIntent: PendingIntent = PendingIntent.getActivity(applicationContext, prayerTime.prayerId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    var goOffPendingIntent = PendingIntent.getForegroundService(applicationContext, prayerTime.prayerId, intent2, PendingIntent.FLAG_UPDATE_CURRENT)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    {
                         goOffPendingIntent = PendingIntent.getForegroundService(applicationContext, prayerTime.prayerId, intent2, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
                    }


                    val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis,alarmPendingIntent )
                    if (hasPermission == true)
                    {
                        alarmManager.setAlarmClock(alarmInfo, goOffPendingIntent)
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