package com.myapplication

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PrayerTimes {
    // ---------------------- Global Variables --------------------
    var calcMethod // caculation method
            = 0
    var asrJuristic // Juristic method for Asr
            = 0
    var dhuhrMinutes // minutes after mid-day for Dhuhr
            = 0
    var adjustHighLats // adjusting method for higher latitudes
            = 0
    var timeFormat // time format
            = 0
    var lat // latitude
            = 0.0
    var lng // longitude
            = 0.0
    var timeZone // time-zone
            = 0.0
    var jDate // Julian date
            = 0.0

    // ------------------------------------------------------------
    // Calculation Methods
    private var jafari // Ithna Ashari
            = 0
    private var karachi // University of Islamic Sciences, Karachi
            = 0
    private var iSNA // Islamic Society of North America (ISNA)
            = 0
    private var mWL // Muslim World League (MWL)
            = 0
    private var makkah // Umm al-Qura, Makkah
            = 0
    private var egypt // Egyptian General Authority of Survey
            = 0
    private var custom // Custom Setting
            = 0
    private var tehran // Institute of Geophysics, University of Tehran
            = 0

    // Juristic Methods
    private var shafii // Shafii (standard)
            = 0
    private var hanafi // Hanafi
            = 0

    // Adjusting Methods for Higher Latitudes
    private var none // No adjustment
            = 0
    private var midNight // middle of night
            = 0
    private var oneSeventh // 1/7th of night
            = 0
    private var angleBased // angle/60th of night
            = 0

    // Time Formats
    private var time24 // 24-hour format
            = 0
    private var time12 // 12-hour format
            = 0
    private var time12NS // 12-hour format with no suffix
            = 0
    private var floating // floating point number
            = 0

    // Time Names
    val timeNames: ArrayList<String>
    private val InvalidTime // The string used for invalid times
            : String

    // --------------------- Technical Settings --------------------
    private var numIterations // number of iterations needed to compute times
            = 0

    // ------------------- Calc Method Parameters --------------------
    private val methodParams: HashMap<Int, DoubleArray>

    /*
     * this.methodParams[methodNum] = new Array(fa, ms, mv, is, iv);
     *
     * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
     * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
     * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter value
     * (in angle or minutes)
     */
    lateinit var prayerTimesCurrent: DoubleArray
    private val offsets: IntArray

    // ---------------------- Trigonometric Functions -----------------------
    // range reduce angle in degrees.
    private fun fixangle(a: Double): Double {
        var a = a
        a = a - 360 * Math.floor(a / 360.0)
        a = if (a < 0) a + 360 else a
        return a
    }

    // range reduce hours to 0..23
    private fun fixhour(a: Double): Double {
        var a = a
        a = a - 24.0 * Math.floor(a / 24.0)
        a = if (a < 0) a + 24 else a
        return a
    }

    // radian to degree
    private fun radiansToDegrees(alpha: Double): Double {
        return alpha * 180.0 / Math.PI
    }

    // deree to radian
    private fun DegreesToRadians(alpha: Double): Double {
        return alpha * Math.PI / 180.0
    }

    // degree sin
    private fun dsin(d: Double): Double {
        return Math.sin(DegreesToRadians(d))
    }

    // degree cos
    private fun dcos(d: Double): Double {
        return Math.cos(DegreesToRadians(d))
    }

    // degree tan
    private fun dtan(d: Double): Double {
        return Math.tan(DegreesToRadians(d))
    }

    // degree arcsin
    private fun darcsin(x: Double): Double {
        val `val` = Math.asin(x)
        return radiansToDegrees(`val`)
    }

    // degree arccos
    private fun darccos(x: Double): Double {
        val `val` = Math.acos(x)
        return radiansToDegrees(`val`)
    }

    // degree arctan
    private fun darctan(x: Double): Double {
        val `val` = Math.atan(x)
        return radiansToDegrees(`val`)
    }

    // degree arctan2
    private fun darctan2(y: Double, x: Double): Double {
        val `val` = Math.atan2(y, x)
        return radiansToDegrees(`val`)
    }

    // degree arccot
    private fun darccot(x: Double): Double {
        val `val` = Math.atan2(1.0, x)
        return radiansToDegrees(`val`)
    }

    // ---------------------- Time-Zone Functions -----------------------
    // compute local time-zone for a specific date
    private val timeZone1: Double
        private get() {
            val timez: TimeZone = TimeZone.getDefault()
            return timez.getRawOffset() / 1000.0 / 3600
        }

    // compute base time-zone of the system
    private val baseTimeZone: Double
        private get() {
            val timez: TimeZone = TimeZone.getDefault()
            return timez.getRawOffset() / 1000.0 / 3600
        }

    // detect daylight saving in a given date
    private fun detectDaylightSaving(): Double {
        val timez: TimeZone = TimeZone.getDefault()
        return timez.getDSTSavings().toDouble()
    }

    // ---------------------- Julian Date Functions -----------------------
    // calculate julian date from a calendar date
    private fun julianDate(year: Int, month: Int, day: Int): Double {
        var year = year
        var month = month
        if (month <= 2) {
            year -= 1
            month += 12
        }
        val A = Math.floor(year / 100.0)
        val B = 2 - A + Math.floor(A / 4.0)
        return (Math.floor(365.25 * (year + 4716))
                + Math.floor(30.6001 * (month + 1)) + day + B) - 1524.5
    }

    // convert a calendar date to julian date (second method)
    private fun calcJD(year: Int, month: Int, day: Int): Double {
        val J1970 = 2440588.0
        val date = Date(year, month - 1, day)
        val ms: Double = date.getTime().toDouble() // # of milliseconds since midnight Jan 1,
        // 1970
        val days = Math.floor(ms / (1000.0 * 60.0 * 60.0 * 24.0))
        return J1970 + days - 0.5
    }

    // ---------------------- Calculation Functions -----------------------
    // References:
    // http://www.ummah.net/astronomy/saltime
    // http://aa.usno.navy.mil/faq/docs/SunApprox.html
    // compute declination angle of sun and equation of time
    private fun sunPosition(jd: Double): DoubleArray {
        val D = jd - 2451545
        val g = fixangle(357.529 + 0.98560028 * D)
        val q = fixangle(280.459 + 0.98564736 * D)
        val L = fixangle(q + 1.915 * dsin(g) + 0.020 * dsin(2 * g))

        // double R = 1.00014 - 0.01671 * [self dcos:g] - 0.00014 * [self dcos:
        // (2*g)];
        val e = 23.439 - 0.00000036 * D
        val d = darcsin(dsin(e) * dsin(L))
        var RA = darctan2(dcos(e) * dsin(L), dcos(L)) / 15.0
        RA = fixhour(RA)
        val EqT = q / 15.0 - RA
        val sPosition = DoubleArray(2)
        sPosition[0] = d
        sPosition[1] = EqT
        return sPosition
    }

    // compute equation of time
    private fun equationOfTime(jd: Double): Double {
        return sunPosition(jd)[1]
    }

    // compute declination angle of sun
    private fun sunDeclination(jd: Double): Double {
        return sunPosition(jd)[0]
    }

    // compute mid-day (Dhuhr, Zawal) time
    private fun computeMidDay(t: Double): Double {
        val T = equationOfTime(jDate + t)
        return fixhour(12 - T)
    }

    // compute time for a given angle G
    private fun computeTime(G: Double, t: Double): Double {
        val D = sunDeclination(jDate + t)
        val Z = computeMidDay(t)
        val Beg = -dsin(G) - dsin(D) * dsin(lat)
        val Mid = dcos(D) * dcos(lat)
        val V = darccos(Beg / Mid) / 15.0
        return Z + if (G > 90) -V else V
    }

    // compute the time of Asr
    // Shafii: step=1, Hanafi: step=2
    private fun computeAsr(step: Double, t: Double): Double {
        val D = sunDeclination(jDate + t)
        val G = -darccot(step + dtan(Math.abs(lat - D)))
        return computeTime(G, t)
    }

    // ---------------------- Misc Functions -----------------------
    // compute the difference between two times
    private fun timeDiff(time1: Double, time2: Double): Double {
        return fixhour(time2 - time1)
    }

    // -------------------- Interface Functions --------------------
    // return prayer times for a given date
    private fun getDatePrayerTimes(
        year: Int, month: Int, day: Int,
        latitude: Double, longitude: Double, tZone: Double
    ): ArrayList<String> {
        lat = latitude
        lng = longitude
        timeZone = tZone
        jDate = julianDate(year, month, day)
        val lonDiff = longitude / (15.0 * 24.0)
        jDate = jDate - lonDiff
        return computeDayTimes()
    }

    // return prayer times for a given date
    public fun getPrayerTimes(
        date: Calendar, latitude: Double,
        longitude: Double, tZone: Double
    ): ArrayList<String> {
        val year: Int = date.get(Calendar.YEAR)
        val month: Int = date.get(Calendar.MONTH)
        val day: Int = date.get(Calendar.DATE)
        return getDatePrayerTimes(year, month + 1, day, latitude, longitude, tZone)
    }

    // set custom values for calculation parameters
    private fun setCustomParams(params: DoubleArray) {
        for (i in 0..4) {
            if (params[i] == -1.0) {
                params[i] = methodParams[calcMethod]!![i]
                methodParams[custom] = params
            } else {
                methodParams[custom]!![i] = params[i]
            }
        }
        calcMethod = custom
    }

    // set the angle for calculating Fajr
    fun setFajrAngle(angle: Double) {
        val params = doubleArrayOf(angle, -1.0, -1.0, -1.0, -1.0)
        setCustomParams(params)
    }

    // set the angle for calculating Maghrib
    fun setMaghribAngle(angle: Double) {
        val params = doubleArrayOf(-1.0, 0.0, angle, -1.0, -1.0)
        setCustomParams(params)
    }

    // set the angle for calculating Isha
    fun setIshaAngle(angle: Double) {
        val params = doubleArrayOf(-1.0, -1.0, -1.0, 0.0, angle)
        setCustomParams(params)
    }

    // set the minutes after Sunset for calculating Maghrib
    fun setMaghribMinutes(minutes: Double) {
        val params = doubleArrayOf(-1.0, 1.0, minutes, -1.0, -1.0)
        setCustomParams(params)
    }

    // set the minutes after Maghrib for calculating Isha
    fun setIshaMinutes(minutes: Double) {
        val params = doubleArrayOf(-1.0, -1.0, -1.0, 1.0, minutes)
        setCustomParams(params)
    }

    // convert double hours to 24h format
    fun floatToTime24(time: Double): String {
        var time = time
        val result: String
        if (java.lang.Double.isNaN(time)) {
            return InvalidTime
        }
        time = fixhour(time + 0.5 / 60.0) // add 0.5 minutes to round
        val hours = Math.floor(time).toInt()
        val minutes = Math.floor((time - hours) * 60.0)
        result = if (hours >= 0 && hours <= 9 && minutes >= 0 && minutes <= 9) {
            "0" + hours + ":0" + Math.round(minutes)
        } else if (hours >= 0 && hours <= 9) {
            "0" + hours + ":" + Math.round(minutes)
        } else if (minutes >= 0 && minutes <= 9) {
            hours.toString() + ":0" + Math.round(minutes)
        } else {
            hours.toString() + ":" + Math.round(minutes)
        }
        return result
    }

    // convert double hours to 12h format
    fun floatToTime12(time: Double, noSuffix: Boolean): String {
        var time = time
        if (java.lang.Double.isNaN(time)) {
            return InvalidTime
        }
        time = fixhour(time + 0.5 / 60) // add 0.5 minutes to round
        var hours = Math.floor(time).toInt()
        val minutes = Math.floor((time - hours) * 60)
        val suffix: String
        val result: String
        suffix = if (hours >= 12) {
            "pm"
        } else {
            "am"
        }
        hours = (hours + 12 - 1) % 12 + 1
        /*hours = (hours + 12) - 1;
        int hrs = (int) hours % 12;
        hrs += 1;*/result = if (noSuffix == false) {
            if (hours >= 0 && hours <= 9 && minutes >= 0 && minutes <= 9) {
                ("0" + hours + ":0" + Math.round(minutes) + " "
                        + suffix)
            } else if (hours >= 0 && hours <= 9) {
                "0" + hours + ":" + Math.round(minutes) + " " + suffix
            } else if (minutes >= 0 && minutes <= 9) {
                hours.toString() + ":0" + Math.round(minutes) + " " + suffix
            } else {
                hours.toString() + ":" + Math.round(minutes) + " " + suffix
            }
        } else {
            if (hours >= 0 && hours <= 9 && minutes >= 0 && minutes <= 9) {
                "0" + hours + ":0" + Math.round(minutes)
            } else if (hours >= 0 && hours <= 9) {
                "0" + hours + ":" + Math.round(minutes)
            } else if (minutes >= 0 && minutes <= 9) {
                hours.toString() + ":0" + Math.round(minutes)
            } else {
                hours.toString() + ":" + Math.round(minutes)
            }
        }
        return result
    }

    // convert double hours to 12h format with no suffix
    fun floatToTime12NS(time: Double): String {
        return floatToTime12(time, true)
    }

    // ---------------------- Compute Prayer Times -----------------------
    // compute prayer times at given julian date
    private fun computeTimes(times: DoubleArray): DoubleArray {
        val t = dayPortion(times)
        val Fajr = computeTime(
            180 - methodParams[calcMethod]!![0], t[0]
        )
        val Sunrise = computeTime(180 - 0.833, t[1])
        val Dhuhr = computeMidDay(t[2])
        val Asr = computeAsr((1 + asrJuristic).toDouble(), t[3])
        val Sunset = computeTime(0.833, t[4])
        val Maghrib = computeTime(
            methodParams[calcMethod]!![2], t[5]
        )
        val Isha = computeTime(
            methodParams[calcMethod]!![4], t[6]
        )
        return doubleArrayOf(Fajr, Sunrise, Dhuhr, Asr, Sunset, Maghrib, Isha)
    }

    // compute prayer times at given julian date
    private fun computeDayTimes(): ArrayList<String> {
        var times = doubleArrayOf(5.0, 6.0, 12.0, 13.0, 18.0, 18.0, 18.0) // default times
        for (i in 1..numIterations) {
            times = computeTimes(times)
        }
        times = adjustTimes(times)
        times = tuneTimes(times)
        return adjustTimesFormat(times)
    }

    // adjust times in a prayer time array
    private fun adjustTimes(times: DoubleArray): DoubleArray {
        var times = times
        for (i in times.indices) {
            times[i] += timeZone - lng / 15
        }
        times[2] += dhuhrMinutes.toDouble() / 60 // Dhuhr
        if (methodParams[calcMethod]!![1] === 1.0) // Maghrib
        {
            times[5] = times[4] + methodParams[calcMethod]!![2] / 60
        }
        if (methodParams[calcMethod]!![3] === 1.0) // Isha
        {
            times[6] = times[5] + methodParams[calcMethod]!![4] / 60
        }
        if (adjustHighLats != none) {
            times = adjustHighLatTimes(times)
        }
        return times
    }

    // convert times array to given time format
    private fun adjustTimesFormat(times: DoubleArray): ArrayList<String> {
        val result = ArrayList<String>()
        if (timeFormat == floating) {
            for (time in times) {
                result.add(time.toString())
            }
            return result
        }
        for (i in 0..6) {
            if (timeFormat == time12) {
                result.add(floatToTime12(times[i], false))
            } else if (timeFormat == time12NS) {
                result.add(floatToTime12(times[i], true))
            } else {
                result.add(floatToTime24(times[i]))
            }
        }
        return result
    }

    // adjust Fajr, Isha and Maghrib for locations in higher latitudes
    private fun adjustHighLatTimes(times: DoubleArray): DoubleArray {
        val nightTime = timeDiff(times[4], times[1]) // sunset to sunrise

        // Adjust Fajr
        val FajrDiff = nightPortion(methodParams[calcMethod]!![0]) * nightTime
        if (java.lang.Double.isNaN(times[0]) || timeDiff(times[0], times[1]) > FajrDiff) {
            times[0] = times[1] - FajrDiff
        }

        // Adjust Isha
        val IshaAngle: Double =
            if (methodParams[calcMethod]!![3] === 0.0) methodParams[calcMethod]!![4] else 18.0
        val IshaDiff = nightPortion(IshaAngle) * nightTime
        if (java.lang.Double.isNaN(times[6]) || timeDiff(times[4], times[6]) > IshaDiff) {
            times[6] = times[4] + IshaDiff
        }

        // Adjust Maghrib
        val MaghribAngle: Double =
            if (methodParams[calcMethod]!![1] === 0.0) methodParams[calcMethod]!![2] else 4.0
        val MaghribDiff = nightPortion(MaghribAngle) * nightTime
        if (java.lang.Double.isNaN(times[5]) || timeDiff(times[4], times[5]) > MaghribDiff) {
            times[5] = times[4] + MaghribDiff
        }
        return times
    }

    // the night portion used for adjusting times in higher latitudes
    private fun nightPortion(angle: Double): Double {
        var calc = 0.0
        if (adjustHighLats == angleBased) calc =
            angle / 60.0 else if (adjustHighLats == midNight) calc =
            0.5 else if (adjustHighLats == oneSeventh) calc = 0.14286
        return calc
    }

    // convert hours to day portions
    private fun dayPortion(times: DoubleArray): DoubleArray {
        for (i in 0..6) {
            times[i] /= 24.0
        }
        return times
    }

    // Tune timings for adjustments
    // Set time offsets
    fun tune(offsetTimes: IntArray) {
        for (i in offsetTimes.indices) { // offsetTimes length
            // should be 7 in order
            // of Fajr, Sunrise,
            // Dhuhr, Asr, Sunset,
            // Maghrib, Isha
            offsets[i] = offsetTimes[i]
        }
    }

    private fun tuneTimes(times: DoubleArray): DoubleArray {
        for (i in times.indices) {
            times[i] = times[i] + offsets[i] / 60.0
        }
        return times
    }

    companion object {
        /**
         * @param args
         */
        @JvmStatic
        fun main(args: Array<String>) {
            val latitude = -37.823689
            val longitude = 145.121597
            val timezone = 10.0
            // Test Prayer times here
            val prayers = PrayerTimes()
            prayers.timeFormat = prayers.time12
            prayers.calcMethod = prayers.jafari
            prayers.asrJuristic = prayers.shafii
            prayers.adjustHighLats = prayers.angleBased
            val offsets =
                intArrayOf(0, 0, 0, 0, 0, 0, 0) // {Fajr,Sunrise,Dhuhr,Asr,Sunset,Maghrib,Isha}
            prayers.tune(offsets)
            val now = Date()
            val cal: Calendar = Calendar.getInstance()
            cal.setTime(now)
            val prayerTimes = prayers.getPrayerTimes(
                cal,
                latitude, longitude, timezone
            )
            val prayerNames = prayers.timeNames
            for (i in 0 until prayerTimes.size) {
                println(prayerNames[i] + " - " + prayerTimes[i])
            }
        }
    }

    init {
        // Initialize vars
        calcMethod = 0
        asrJuristic = 0
        dhuhrMinutes = 0
        adjustHighLats = 1
        timeFormat = 0

        // Calculation Methods
        jafari = 0 // Ithna Ashari
        karachi = 1 // University of Islamic Sciences, Karachi
        iSNA = 2 // Islamic Society of North America (ISNA)
        mWL = 3 // Muslim World League (MWL)
        makkah = 4 // Umm al-Qura, Makkah
        egypt = 5 // Egyptian General Authority of Survey
        tehran = 6 // Institute of Geophysics, University of Tehran
        custom = 7 // Custom Setting

        // Juristic Methods
        shafii = 0 // Shafii (standard)
        hanafi = 1 // Hanafi

        // Adjusting Methods for Higher Latitudes
        none = 0 // No adjustment
        midNight = 1 // middle of night
        oneSeventh = 2 // 1/7th of night
        angleBased = 3 // angle/60th of night

        // Time Formats
        time24 = 0 // 24-hour format
        time12 = 1 // 12-hour format
        time12NS = 2 // 12-hour format with no suffix
        floating = 3 // floating point number

        // Time Names
        timeNames = ArrayList()
        timeNames.add("Fajr")
        timeNames.add("Sunrise")
        timeNames.add("Dhuhr")
        timeNames.add("Asr")
        timeNames.add("Sunset")
        timeNames.add("Maghrib")
        timeNames.add("Isha")
        InvalidTime = "-----" // The string used for invalid times

        // --------------------- Technical Settings --------------------
        numIterations = 1 // number of iterations needed to compute
        // times

        // ------------------- Calc Method Parameters --------------------

        // Tuning offsets {fajr, sunrise, dhuhr, asr, sunset, maghrib, isha}
        offsets = IntArray(7)
        offsets[0] = 0
        offsets[1] = 0
        offsets[2] = 0
        offsets[3] = 0
        offsets[4] = 0
        offsets[5] = 0
        offsets[6] = 0

        /*
         *
         * fa : fajr angle ms : maghrib selector (0 = angle; 1 = minutes after
         * sunset) mv : maghrib parameter value (in angle or minutes) is : isha
         * selector (0 = angle; 1 = minutes after maghrib) iv : isha parameter
         * value (in angle or minutes)
         */methodParams = HashMap()

        // Jafari
        val Jvalues = doubleArrayOf(16.0, 0.0, 4.0, 0.0, 14.0)
        methodParams[Integer.valueOf(jafari)] = Jvalues

        // Karachi
        val Kvalues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 18.0)
        methodParams[Integer.valueOf(karachi)] = Kvalues

        // ISNA
        val Ivalues = doubleArrayOf(15.0, 1.0, 0.0, 0.0, 15.0)
        methodParams[Integer.valueOf(iSNA)] = Ivalues

        // MWL
        val MWvalues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)
        methodParams[Integer.valueOf(mWL)] = MWvalues

        // Makkah
        val MKvalues = doubleArrayOf(18.5, 1.0, 0.0, 1.0, 90.0)
        methodParams[Integer.valueOf(makkah)] = MKvalues

        // Egypt
        val Evalues = doubleArrayOf(19.5, 1.0, 0.0, 0.0, 17.5)
        methodParams[Integer.valueOf(egypt)] = Evalues

        // Tehran
        val Tvalues = doubleArrayOf(17.7, 0.0, 4.5, 0.0, 14.0)
        methodParams[Integer.valueOf(tehran)] = Tvalues

        // Custom
        val Cvalues = doubleArrayOf(18.0, 1.0, 0.0, 0.0, 17.0)
        methodParams[Integer.valueOf(custom)] = Cvalues
    }
}
