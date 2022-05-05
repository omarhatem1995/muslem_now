package com.zaker.data.core.workmanager

import android.app.Application
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.zaker.R
import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.Datum
import com.zaker.data.entities.model.PrayerTimeModel
import com.zaker.data.gateways.dao.MuslemNowDataBase
import com.zaker.data.gateways.remote.aladahangateway.AlAdahanGatewayProvider
import com.zaker.data.repositories.SharedPreferencesRepository
import java.util.*

class MonthlyAlarmWorker(appContext: Context, params: WorkerParameters):
    CoroutineWorker(appContext, params) {

    private val sharedPreferencesRepository: SharedPreferencesRepository = SharedPreferencesRepository(
        appContext as Application
    )
    override suspend fun doWork(): Result {
        return try {
            setNewSalahTimes()
            Result.success()
        }catch (e:Exception)
        {
            Result.retry()
        }
    }



   suspend fun setNewSalahTimes()
   {
       val alarmState = sharedPreferencesRepository.getAlarmState()
       val day = Calendar.DAY_OF_MONTH
       val month = Calendar.MONTH
       val year = Calendar.YEAR
       if (!alarmState && day ==1)
       {
           val lat =sharedPreferencesRepository.getLat()
           val long = sharedPreferencesRepository.getLong()

           if (lat != null && long != null) {
               val response = AlAdahanGatewayProvider.provideGateWay().
               getAladahan(lat,long,"5", month = month.toString(),year.toString())

               when (response) {
                   is ApiResponse.Success -> {
                       val aladahan = response.body.data
                       val prayerList =preparePrayerList(aladahan)
                       savePrayerTimes(applicationContext,prayerList)

                   }
                   is ApiResponse.NetworkError -> {}

                   else -> {}
               }
           }
           }
       }

    private fun savePrayerTimes(applicationContext: Context,prayerTimesList: List<PrayerTimeModel>){
        val db = MuslemNowDataBase.getDataBase(applicationContext)
        val savePrayerDao = db.alAdahanDao()
        for(x in prayerTimesList) {
            savePrayerDao.addPrayerTimes(x)
        }
    }


    fun preparePrayerList(data:List<Datum>):List<PrayerTimeModel>
    {
        val prayerList:MutableList<PrayerTimeModel> = mutableListOf()
        for (i in data.indices) {

            var date = data.get(i).date.gregorian.date

            var hijriDate = data[i].date.hijri.day + " " + data[i].date.hijri.month.ar +
                    " " + data[i].date.hijri.year
            var hijriDateEn = data[i].date.hijri.day + " " + data[i].date.hijri.month.en +
                    " " + data[i].date.hijri.year

            var fajrAdahanModel = PrayerTimeModel(
                0, R.drawable.ic_elfajr,
                "Fajr", getTimeOnlyForPrayer(data.get(i).timings.fajr), 1, date,
                hijriDate,hijriDateEn
            )

            var sunRiseAdahanModel = PrayerTimeModel(
                1, R.drawable.ic_sunrise,
                "Sunrise", getTimeOnlyForPrayer(data.get(i).timings.sunrise), 1, date,
                hijriDate,hijriDateEn
            )

            var dhurAdahanModel = PrayerTimeModel(
                2, R.drawable.ic_eldhur,
                "Dhur", getTimeOnlyForPrayer(data.get(i).timings.dhuhr), 1, date,
                hijriDate,hijriDateEn
            )

            var asrAdahanModel = PrayerTimeModel(
                3, R.drawable.ic_elasr,
                "Asr", getTimeOnlyForPrayer(data.get(i).timings.asr), 1, date,
                hijriDate,hijriDateEn
            )


            var maghribAdahanModel = PrayerTimeModel(
                4, R.drawable.ic_elmaghrib,
                "Maghrib", getTimeOnlyForPrayer(data.get(i).timings.maghrib), 1, date,
                hijriDate,hijriDateEn
            )

            var ishaAdahanModel = PrayerTimeModel(
                5, R.drawable.ic_elisha,
                "Isha", getTimeOnlyForPrayer(data.get(i).timings.isha), 1, date,
                hijriDate,hijriDateEn
            )


            prayerList.add(0, fajrAdahanModel)
            prayerList.add(1, sunRiseAdahanModel)
            prayerList.add(2, dhurAdahanModel)
            prayerList.add(3, asrAdahanModel)

            prayerList.add(4, maghribAdahanModel)
            prayerList.add(5, ishaAdahanModel)

        }

        return prayerList
    }

    private fun getTimeOnlyForPrayer(input: String): String {
        var firstFiveChars = ""
        if (input.length > 4) {
            firstFiveChars = input.substring(0, 5)
        }
        return firstFiveChars
    }

   }



