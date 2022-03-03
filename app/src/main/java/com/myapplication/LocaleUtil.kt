package com.myapplication

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorManager
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
import java.time.LocalTime
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
            Log.d("getTimeAMandPM", " is $hoursInt")
            Log.d("getTimeAMandPM", " is $minutes")
            return if (hoursInt < 12) {
                hours = String.format("%02d", hoursInt)
                "$hours$minutes " + context.getString(R.string.am)
            } else {
                hoursInt -= 12
                if (hoursInt == 0)
                    hoursInt = 12
                hours = String.format("%02d", hoursInt)
                "$hours$minutes " + context.getString(R.string.pm)
            }
        }
        fun convertToArabic(value: String,language:String): String? {
            return if(language == "ar") {
                (value + "")
                    .replace("1".toRegex(), "١").replace("2".toRegex(), "٢")
                    .replace("3".toRegex(), "٣").replace("4".toRegex(), "٤")
                    .replace("5".toRegex(), "٥").replace("6".toRegex(), "٦")
                    .replace("7".toRegex(), "٧").replace("8".toRegex(), "٨")
                    .replace("9".toRegex(), "٩").replace("0".toRegex(), "٠")
                    .replace(":".toRegex(), ":")
            } else
                value
        }
        fun getNameOfPrayer(context: Context, id: Int): String {
            return when (id) {
                0 -> context.getString(R.string.fajr)
                1 -> context.getString(R.string.sunrise)
                2 -> context.getString(R.string.duhr)
                3 -> context.getString(R.string.asr)
                4 -> context.getString(R.string.maghrib)
                else -> context.getString(R.string.isha)
            }
        }
        fun getNameOfPrayer(id: Int): String {
            return when (id) {
                0 -> "الفجر"
                1 -> "الشروق"
                2 -> "الظهر"
                3 -> "العصر"
                4 -> "المغرب"
                else -> "العشاء"
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun nextPrayer(prayerList: MutableList<PrayerTimeModel>, currentTime: String): Int {
            val localTime: LocalTime = LocalTime.parse(currentTime);
            val currentMillis = localTime.toSecondOfDay() * 1000;

            for (i in 0 until prayerList.size) {
                val time = prayerList[i].time
                val localTime: LocalTime = LocalTime.parse(time)
                val prayerMillis = localTime.toSecondOfDay() * 1000
                if (prayerMillis > currentMillis) {
                    return prayerList[i].prayerId
                }
            }
            return 300
        }

        fun remainingTimeForNextPrayer(currentTime: String, nextPrayer: String): String {
            val dateFormat = SimpleDateFormat("hmmaa")
            val dateFormat2 = SimpleDateFormat("hh:mm")
            val dateForNextPrayerFormat = SimpleDateFormat("HH:mm dd-MM-yyyy")
            try {
                val date: Date = dateForNextPrayerFormat.parse(currentTime)
                val out: String = dateForNextPrayerFormat.format(date)
                val dateNextPrayer: Date = dateForNextPrayerFormat.parse(nextPrayer)
                val out2: String = dateForNextPrayerFormat.format(dateNextPrayer)
                Log.e("Time", out)
                val diff: Long = dateNextPrayer.time - date.time
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

                Log.d(
                    "differenceIs", " : " + elapsedDays + " ," +
                            elapsedHours + " , " + elapsedMinutes + " , " + elapsedSeconds
                )

                Log.d("currentandnext ", " : " + out + " > " + out2)
                Log.d("nextPrayerHours", " is " + hours + ":" + minutes)
                var returnFormatForHours = String.format("%02d", elapsedHours)
                var returnFormatForMinutes = String.format("%02d", elapsedMinutes)
                if (returnFormatForHours.length > 2) returnFormatForHours =
                    returnFormatForHours.substring(0, 2)
                if (returnFormatForMinutes.length > 2) returnFormatForMinutes =
                    returnFormatForMinutes.substring(0, 2)
                Log.d(
                    "nextPrayerHours",
                    " is " + returnFormatForHours + ":" + returnFormatForMinutes
                )
                return returnFormatForHours + ":" + returnFormatForMinutes
            } catch (e: ParseException) {
                Log.d("nextPrayerHours", " error " + e.message)

            }
            return "0"
        }

        fun setDrawable(id: Int): Int {
            if (id == 0)
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
                    OutputStreamWriter(
                        context.openFileOutput(
                            "savedAzkar.json",
                            AppCompatActivity.MODE_APPEND
                        )
                    )
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

        fun checkIfIdExistsAndReplace(context: Context, id: Int, counter: Int): Boolean {
            var ret = false
            if (fileExists(context, "savedAzkar.json")) {

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
                                obj.put("counter", counter)
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


        @RequiresApi(Build.VERSION_CODES.O)
        fun prayerFilter(
            prayerList: MutableList<PrayerTimeModel>,
            currentTime: String
        ): MutableList<PrayerTimeModel> {

            val finalList: MutableList<PrayerTimeModel> = mutableListOf()
            val timeNow = currentTime
            val localTime: LocalTime = LocalTime.parse(timeNow)
            val currentMillis = localTime.toSecondOfDay() * 1000
            Log.d("currnetDate", "$currentMillis , $timeNow")

            for (i in 0 until prayerList.size) {
                val time = prayerList[i].time
                val localTime: LocalTime = LocalTime.parse(time)
                val prayerMillis = localTime.toSecondOfDay() * 1000
                Log.d("currnetDate", "$prayerMillis , $time")
                if (prayerMillis > currentMillis) {
                    Log.d("nextPrayer Is", "" + prayerList[i].prayerId)
                    finalList.add(prayerList[i])
                }
            }
            return finalList
        }
      /*  lateinit var sensorManager : SensorManager
        lateinit var sensor: Sensor

        fun initSensorManager(context: Context){
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)

        }*/

        fun getJuzName(number:String,context:Context):String {
            when (number) {
                "01" -> {
                    return context.getString(R.string.first_juz)
                }
                "02" -> {
                    return context.getString(R.string.second_juz)
                }
                "03" -> {
                    return context.getString(R.string.third_juz)
                }
                "04" -> {
                    return context.getString(R.string.fourth_juz)
                }
                "05" -> {
                    return context.getString(R.string.fifth_juz)
                }
                "06" -> {
                    return context.getString(R.string.sixth_juz)
                }
                "07" -> {
                    return context.getString(R.string.seventh_juz)
                }
                "08" -> {
                    return context.getString(R.string.eighth_juz)
                }
                "09" -> {
                    return context.getString(R.string.ninth_juz)
                }
                "10" -> {
                    return context.getString(R.string.tenth_juz)
                }
                "11" -> {
                    return context.getString(R.string.eleventh_juz)
                }
                "12" -> {
                    return context.getString(R.string.twelfth_juz)
                }
                "13" -> {
                    return context.getString(R.string.thirteenth_juz)
                }
                "14" -> {
                    return context.getString(R.string.fourteenth_juz)
                }
                "15" -> {
                    return context.getString(R.string.fifteenth_juz)
                }
                "16" -> {
                    return context.getString(R.string.sixteenth_juz)
                }
                "17" -> {
                    return context.getString(R.string.seventeenth_juz)
                }
                "18" -> {
                    return context.getString(R.string.eighteenth_juz)
                }
                "19" -> {
                    return context.getString(R.string.nineteenth_juz)
                }
                "20" -> {
                    return context.getString(R.string.twentieth_juz)
                }
                "21" -> {
                    return context.getString(R.string.twentyone_juz)
                }
                "22" -> {
                    return context.getString(R.string.twentytwo_juz)
                }
                "23" -> {
                    return context.getString(R.string.twentythree_juz)
                }
                "24" -> {
                    return context.getString(R.string.twenthfourth_juz)
                }
                "25" -> {
                    return context.getString(R.string.twentyfifth_juz)
                }
                "26" -> {
                    return context.getString(R.string.twentysixth_juz)
                }
                "27" -> {
                    return context.getString(R.string.twentyseventh_juz)
                }
                "28" -> {
                    return context.getString(R.string.twentyeighth_juz)
                }
                "29" -> {
                    return context.getString(R.string.twentyninth_juz)
                }
                else -> return context.getString(R.string.thirty_juz)
            }
        }
        fun getMakkiyahOrMaddaniyah(context: Context,type:String) : String{
            if(type == "Makkiyah"){
                return context.getString(R.string.makkiyah)
            }else
                return context.getString(R.string.madaniyah)
        }
    }
}