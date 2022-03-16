package com.myapplication.data.core.workmanager


import android.app.Application
import androidx.work.*
import com.myapplication.data.entities.model.getLocalQuranResponse
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.util.concurrent.TimeUnit

@InternalCoroutinesApi
@DelicateCoroutinesApi
class MuslemApp : Application() {

    companion object {
        val CHANNEL_FULL_AZAN_MESHARY = "Meshary channel"
        val CHANNEL_FULL_AZAN_HOSARY = "Hosary channel"
        val CHANNEL_TAKBIRAT_AZAN_HOSARY = "Takbirat channel"
        val channelName1 = "Channel Meshary"
        val channelName2 = "Channel Hosary"
        val channelName3 = "Channel Takbirat Hosary"
    }

    private var quranJob:Job? = null


    override fun onCreate() {
       // createNotificationChannels()

        quranJob = Job()
        val coroutineScope = CoroutineScope(Dispatchers.IO+quranJob!!)

        val dataBase = MuslemNowDataBase.getDataBase(this)
        coroutineScope.launch{
            dataBase.alQuranDao().getAllQuran().collect {
                if (it.isEmpty())
                {
                    val quran =getLocalQuranResponse(this@MuslemApp).onEach { entity->
                       val oldUniCode =entity.unicode
                        entity.unicode = "\\u$oldUniCode"
                    }

                    dataBase.alQuranDao().loadQuran(quran)
                }else
                {
                    quranJob!!.cancel()
                }
            }

        }
        try {
            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    "ElSalahWorker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    request
                )
        } catch (e: Exception) {
        }

        initMonthlyWorker()

        super.onCreate()
    }


    private val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .apply {

            setRequiresDeviceIdle(false)

        }
        .setRequiresCharging(false)
        .build()
    private val monthlyConstraints = Constraints.Builder()
        .setRequiresBatteryNotLow(false)
        .apply {

            setRequiresDeviceIdle(false)

        }
        .setRequiresCharging(false)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    @DelicateCoroutinesApi
    private val request = PeriodicWorkRequestBuilder<ElSalahWorker>(
        3,
        TimeUnit.HOURS
    )
        .setConstraints(constraints)
        .setBackoffCriteria(
            BackoffPolicy.LINEAR, PeriodicWorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,
            TimeUnit.MILLISECONDS
        )
        .addTag("settingAlarm")
        .build()



    private val monthlyRequest = PeriodicWorkRequestBuilder<MonthlyAlarmWorker>(12, TimeUnit.HOURS)
        .setConstraints(monthlyConstraints)
        .setBackoffCriteria(BackoffPolicy.LINEAR,PeriodicWorkRequest.DEFAULT_BACKOFF_DELAY_MILLIS,TimeUnit.MILLISECONDS).build()


    fun initMonthlyWorker()
    {
        try {
            WorkManager.getInstance(applicationContext)
                .enqueueUniquePeriodicWork(
                    "MonthlyWorker",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    monthlyRequest
                )
        } catch (e: Exception) {
        }
    }


}