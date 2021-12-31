package com.myapplication.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.myapplication.LocaleUtil
import com.myapplication.MyNotificationPublisher
import com.myapplication.data.core.workmanager.AlarmService
import com.myapplication.data.core.workmanager.MuslemApp
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(app:MuslemApp): AndroidViewModel(app) {

    val preferences = SharedPreferencesRepository(app)
    val workManager = WorkManager.getInstance(app)

    val workState: LiveData<List<WorkInfo>?> =  workManager.getWorkInfosByTagLiveData("settingAlarm")




    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun setSalahAlarm()
    {
        withContext(Dispatchers.IO)
        {
            val c: Date = Calendar.getInstance().time
            println("Current time => $c")
            Log.e(null, "setSalahAlarm: ", )
            val df = SimpleDateFormat("dd-MM-yyyy", Locale("en"))
            Log.e(null, "getCurrentDate: ${df.format(c)} ")
            val currentHourDateFormat = SimpleDateFormat("HH:mm", Locale("en"))
            val formattedDate2:String = currentHourDateFormat.format(c)
            // df.calendar.timeInMillis
            val formattedDate: String = df.format(c)
            val dao = MuslemNowDataBase.getDataBase(getApplication()).alAdahanDao()
            val alarmManager: AlarmManager = getApplication<MuslemApp>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
            var hasPermission: Boolean? = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                hasPermission = alarmManager.canScheduleExactAlarms()
            }

            val intent = Intent(getApplication(), MyNotificationPublisher::class.java)
            intent.action = "Off"
            val intent2 = Intent(getApplication(), AlarmService::class.java)
            intent2.action = "Trigger"
            //setting up a pending intent

            //var alarmPendingIntent:PendingIntent = PendingIntent.getService(applicationContext, 777, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            alarmPendingIntent = PendingIntent.getForegroundService(applicationContext, 777, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//        }


            val prayerTimes = dao.getSpecificDayPrayerTimes(formattedDate).collect {

                val calendar: Calendar = Calendar.getInstance()
                val finalList = LocaleUtil.prayerFilter(it.toMutableList(), formattedDate2)


                finalList.map { prayerTime ->
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
                    intent2.putExtra("prayerObject",bundle)

                    val alarmPendingIntent: PendingIntent = PendingIntent.getBroadcast(getApplication(), prayerTime.prayerId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)

                    val goOffPendingIntent = PendingIntent.getForegroundService(getApplication(), prayerTime.prayerId, intent2, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_ONE_SHOT)
                    val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis,alarmPendingIntent)
                    if (hasPermission == true)
                    {
                        Log.e(null, "setSalahAlarm: ${calendar.timeInMillis} ", )
                        alarmManager.setAlarmClock(alarmInfo,goOffPendingIntent)
                    }

                }
            }

        }


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

    class Factory(val app: MuslemApp) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }
}