package com.myapplication

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.LocaleList
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import com.myapplication.data.entities.model.PrayerTimeModel
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.chrono.IslamicChronology
import org.json.JSONObject
import java.io.*
import java.lang.StringBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

class LocaleUtil {
    companion object {
        val supportedLocales = listOf("en", "ar")
        const val OPTION_PHONE_LANGUAGE = "sys_def"

        fun getLocaleFromPrefCode(prefCode: String): Locale {
            val localeCode = if (prefCode != OPTION_PHONE_LANGUAGE) {
                prefCode
            } else {
                val systemLang = ConfigurationCompat.getLocales(Resources.getSystem().configuration)
                    .get(0).language
                if (systemLang in supportedLocales) {
                    systemLang
                } else {
                    "en"
                }
            }
            return Locale(localeCode)
        }

        fun applyLocalizedContext(baseContext: Context, prefLocaleCode: String) {
            val currentLocale = getLocaleFromPrefCode(prefLocaleCode)
            val baseLocale = getLocaleFromConfiguration(baseContext.resources.configuration)
            Locale.setDefault(currentLocale)
            if (!baseLocale.toString().equals(currentLocale.toString(), ignoreCase = true)) {
                val config = getLocalizedConfiguration(currentLocale)
                baseContext.resources.updateConfiguration(
                    config,
                    baseContext.resources.displayMetrics
                )
            }
        }

        @Suppress("DEPRECATION")
        private fun getLocaleFromConfiguration(configuration: Configuration): Locale {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                configuration.locales.get(0)
            } else {
                configuration.locale
            }
        }

        fun getLocalizedConfiguration(locale: Locale): Configuration {
            val config = Configuration()
            return config.apply {
                config.setLayoutDirection(locale)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    config.setLocale(locale)
                    val localeList = LocaleList(locale)
                    LocaleList.setDefault(localeList)
                    config.setLocales(localeList)
                } else {
                    config.setLocale(locale)
                }
            }
        }

        fun hijriMonthName(dtISO: DateTime): String {
            val SAUDI_ARABIA = DateTimeZone.forID("Asia/Riyadh")
            return when (dtISO.withChronology(IslamicChronology.getInstance(SAUDI_ARABIA)).monthOfYear) {
                1 -> "مُحرّم"
                2 -> "صفر"
                3 -> "ربيع الأول"
                4 -> "ربيع الثاني"
                5 -> "جُمادى الأولى"
                6 -> "جُمادى الآخرة"
                7 -> "رجب"
                8 -> "شعبان"
                9 -> "رمضان"
                10 -> "شوال"
                11 -> "ذو القعدة"
                12 -> " ذو الحجة"
                else -> "."
            }
        }

        fun getTimeAMandPM(input: String, context: Context): String {
            var hours = input.substring(0, 2)
            var minutes = input.substring(2, 5)
            var hoursInt = hours.toInt()

            return if (hoursInt < 12) {
                hours = String.format("%02d", hoursInt)
                "$hours$minutes " + context.getString(R.string.am)
            } else {
                hoursInt -= 12
                if (hoursInt == 0)
                    hoursInt = 1
                hours = String.format("%02d", hoursInt)
                "$hours$minutes " + context.getString(R.string.pm)
            }
        }
        fun getNameOfPrayer(context:Context,id:Int) : String{
            return when (id) {
                0 -> context.getString(R.string.fajr)
                1 -> context.getString(R.string.sunrise)
                2 -> context.getString(R.string.duhr)
                3 -> context.getString(R.string.asr)
                4 -> context.getString(R.string.maghrib)
                else -> context.getString(R.string.isha)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun nextPrayer(prayerList: MutableList<PrayerTimeModel>, currentTime: String) : Int{
            val timeNow  = currentTime
            val localTime : LocalTime  = LocalTime.parse(timeNow);
            val currentMillis  = localTime.toSecondOfDay() * 1000;
            Log.d("currnetDate" , currentMillis.toString() + " , "+ timeNow)

            for(i in 0..prayerList.size -1 ){
                val time  = prayerList[i].time
                val localTime : LocalTime  = LocalTime.parse(time)
                val prayerMillis  = localTime.toSecondOfDay() * 1000
                Log.d("currnetDate" , prayerMillis.toString() + " , "+ time)
                if(prayerMillis > currentMillis){
                    Log.d("nextPrayer Is" , ""+ prayerList[i].prayerId)
                    return prayerList[i].prayerId
                }
            }
            return 300
        }

        fun remainingTimeForNextPrayer(currentTime: String,nextPrayer: String):String{
            val dateFormat = SimpleDateFormat("hmmaa")
            val dateFormat2 = SimpleDateFormat("hh:mm")
            val dateForNextPrayerFormat = SimpleDateFormat("HH:mm dd-MM-yyyy")
            try {
                val date: Date = dateForNextPrayerFormat.parse(currentTime)
                val out: String = dateForNextPrayerFormat.format(date)
                val dateNextPrayer: Date = dateForNextPrayerFormat.parse(nextPrayer)
                val out2: String = dateForNextPrayerFormat.format(dateNextPrayer)
                Log.e("Time", out)
                val diff: Long =dateNextPrayer.time- date.time
                val seconds = diff / 1000
                val minutes = seconds / 60
                val hours = minutes / 60
                val days = hours / 24

                val secondsInMilli: Long = 1000
                val minutesInMilli = secondsInMilli * 60
                val hoursInMilli = minutesInMilli * 60
                val daysInMilli = hoursInMilli * 24

                var different: Long = dateNextPrayer.time - date.time

                val elapsedDays: Long = different / daysInMilli
                different = different % daysInMilli

                val elapsedHours: Long = different / hoursInMilli
                different = different % hoursInMilli

                val elapsedMinutes: Long = different / minutesInMilli
                different = different % minutesInMilli

                val elapsedSeconds: Long = different / secondsInMilli

                Log.d("differenceIs" , " : "+ elapsedDays + " ," +
                        elapsedHours + " , " + elapsedMinutes + " , " + elapsedSeconds)

                Log.d("currentandnext " , " : " + out + " > " + out2)
                Log.d("nextPrayerHours" , " is "+hours + ":" + minutes)
                var returnFormatForHours = String.format("%02d",elapsedHours)
                var returnFormatForMinutes = String.format("%02d",elapsedMinutes)
                if (returnFormatForHours.length > 2) returnFormatForHours = returnFormatForHours.substring(0,2)
                if (returnFormatForMinutes.length > 2) returnFormatForMinutes = returnFormatForMinutes.substring(0,2)
                Log.d("nextPrayerHours" , " is "+returnFormatForHours + ":" + returnFormatForMinutes)
                return returnFormatForHours+":"+returnFormatForMinutes
            } catch (e: ParseException) {
                Log.d("nextPrayerHours" , " error "+ e.message)

            }
            return "0"
        }

        fun setDrawable(id:Int) : Int{
            if(id == 0)
                return R.drawable.ic_elfajr
            else if (id == 1)
                return R.drawable.ic_sunrise
            else if (id == 2)
                return R.drawable.ic_eldhur
            else if (id == 3)
                return R.drawable.ic_elasr
            else if (id == 4)
                return R.drawable.ic_elmaghrib
            else if (id == 5)
                return R.drawable.ic_elisha

            else return R.drawable.ic_elfajr
        }

        fun writeToFile(data: String, context: Context) {
            try {
                Log.d("writeToJson", " is called")
                val outputStreamWriter =
                    OutputStreamWriter(context.openFileOutput("savedAzkar.json", AppCompatActivity.MODE_APPEND))
                outputStreamWriter.write(data)
                outputStreamWriter.append("\n");
                outputStreamWriter.close()
            } catch (e: IOException) {
                Log.e("Exception", "File write failed: $e")
            }
            Log.d("writeToJson", " is saved")

        }
        fun fileExists(context: Context, filename: String?): Boolean {
            val file = context.getFileStreamPath(filename)
            return if (file == null || !file.exists()) {
                Log.d("filesasdlkasd", " doesnt exists")
                false
            } else {
                Log.d("filesasdlkasd", " exists")
                true
            }
        }
        fun checkIfIdExistsAndReplace(context: Context,id:Int , counter : Int) : Boolean{
            var ret = false
            if(fileExists(context,"savedAzkar.json")) {

                try {
                    val inputStream: InputStream = context.openFileInput("savedAzkar.json")
                    if (inputStream != null) {
                        val inputStreamReader = InputStreamReader(inputStream)
                        val bufferedReader = BufferedReader(inputStreamReader)
                        var receiveString: String? = ""
                        val stringBuilder = StringBuilder()
                        while (bufferedReader.readLine().also { receiveString = it } != null) {
                            stringBuilder.append("\n").append(receiveString)
                            Log.d("foundLoasdkasd", "found: " + receiveString)
                            val obj = JSONObject(receiveString)
                            if (id == obj.getString("id").toInt()) {
                               obj.put("counter",counter)
                                ret = true
                            } else {
                                ret = false
                            }
                            Log.d("foundLoasdkasd", "found: " + obj.getString("id"))
                            Log.d("foundLoasdkasd", "found: " + obj.getString("counter"))
                        }

                        inputStream.close()
//                ret = stringBuilder.toString()
                    }
                } catch (e: FileNotFoundException) {
                    Log.e("login activity", "File not found: " + e.toString())
                } catch (e: IOException) {
                    Log.e("login activity", "Can not read file: $e")
                }
            }
            return ret
        }

    }
}