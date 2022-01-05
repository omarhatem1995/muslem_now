package com.myapplication.ui.fragments.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.hardware.*
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.myapplication.databinding.FragmentHomeBinding
import kotlin.math.roundToInt

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.myapplication.data.entities.model.Datum
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.domain.usecases.ui.AlAdahanUseCases
import com.myapplication.ui.entities.AlAdahanViewState
import java.text.DateFormat
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList
import android.location.Geocoder
import android.os.Build
import android.os.CountDownTimer
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.myapplication.LocaleUtil.Companion.getNameOfPrayer
import com.myapplication.LocaleUtil.Companion.nextPrayer
import com.myapplication.LocaleUtil.Companion.remainingTimeForNextPrayer
import java.util.concurrent.TimeUnit

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task
import android.app.AlarmManager
import android.app.Dialog
import android.app.Notification

import android.os.SystemClock

import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.myapplication.MyNotificationPublisher
import com.myapplication.QiblahActivity
import com.myapplication.R
import com.myapplication.ui.ViewUtils
import java.io.IOException
import java.lang.reflect.InvocationTargetException


class HomeFragment : Fragment(), AlAdahanUseCases.View, PrayerSoundClickListener {

    lateinit var binding: FragmentHomeBinding

    companion object {
        const val TAG = "HomeFragmentLog"
        const val MARSHMALLOW = 23

        const val ACCESS_FINE_LOCATION_REQ_CODE = 35

        const val QIBLA_LATITUDE = 21.3891
        const val QIBLA_LONGITUDE = 39.8579
    }

    var currentDegree: Float = 0f
    var currentNeedleDegree: Float = 0f

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    lateinit var sensorManager: SensorManager
    lateinit var sensor: Sensor
    lateinit var userLocation: Location
    lateinit var tvHeading: TextView
    lateinit var needleAnimation: RotateAnimation
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var currentTime: TextView
    lateinit var localTime: String
    var monthOfTheYear = "null"
    var currentYear = "null"
    private val vm: HomeViewModel by viewModels()
    val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
    lateinit var nextDay: String
    lateinit var progressDialog: Dialog
    var arrayList: MutableList<Boolean> = ArrayList()

    lateinit var nextDayForCalculations: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var view: View = inflater.inflate(R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )
        binding.homeViewmodel = vm
        tvHeading = view.findViewById<TextView>(R.id.tvHeading)
        currentTime = view.findViewById<TextView>(R.id.date_georgian)
        needleAnimation = RotateAnimation(
            currentNeedleDegree,
            0f,
            Animation.RELATIVE_TO_SELF,
            .5f,
            Animation.RELATIVE_TO_SELF,
            .5f
        )

        binding.lifecycleOwner = this
        initViews()
        return binding.root
    }

    fun initViews(){
        userLocation = Location("User Location")
        arrayList.add(0, vm.preference.getFajr())
        arrayList.add(1, vm.preference.getDuhr())
        arrayList.add(2, vm.preference.getSunRise())
        arrayList.add(3, vm.preference.getAsr())
        arrayList.add(4, vm.preference.getMaghrib())
        arrayList.add(5, vm.preference.getIsha())
        getUserLocation()
        getDateAndTime()
        binding.ivQiblaDirection.setOnClickListener {
            val intent = Intent(context, QiblahActivity::class.java)
            context?.startActivity(intent)
        }

        binding.sideMenu.setOnClickListener {
            val sideMenuFragment = SideMenuFragment()
            addFragmentOnlyOnce(parentFragmentManager, sideMenuFragment, "BLANK")

        }
        progressDialog = context?.let { Dialog(it) }!!

    }

    fun addFragmentOnlyOnce(fragmentManager: FragmentManager, fragment: Fragment?, tag: String?) {
        // Make sure the current transaction finishes first
        fragmentManager.executePendingTransactions()

        // If there is no fragment yet with this tag...
        if (fragmentManager.findFragmentByTag(tag) == null) {
            // Add it
            val transaction: FragmentTransaction = fragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.fragment_sidemenu_enter_animation,
                R.anim.fragment_sidemenu_exit_animation, R.anim.fragment_sidemenu_enter_animation,
                R.anim.fragment_sidemenu_exit_animation
            )
            transaction.addToBackStack(null)
            if (fragment != null) {
                transaction.add(R.id.frameLayoutSideMenu, fragment, tag)
            }
            transaction.commit()
        }
    }

    private fun getDateAndTime() {
        val georgianFullDateFormat: DateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale("ar"))
        val georgianDateFormatForInsertion: DateFormat =
            SimpleDateFormat("dd-MM-yyyy", Locale("en"))
        val georgianDateFormatForChecking: DateFormat =
            SimpleDateFormat("hh:mm dd-MM-yyyy", Locale("en"))

        val currentLocalTime = cal.time
        val monthOfTheyYearFormat: DateFormat = SimpleDateFormat("MM")
        val currentYearFormat: DateFormat = SimpleDateFormat("yyyy")

        localTime = georgianDateFormatForInsertion.format(cal.time)

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        val nextDayCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
        nextDayCal.add(Calendar.DAY_OF_MONTH, 1)
        nextDay = georgianDateFormatForInsertion.format(nextDayCal.time)
        nextDayForCalculations = georgianDateFormatForChecking.format(nextDayCal.time)

        monthOfTheYear = monthOfTheyYearFormat.format(currentLocalTime)
        currentYear = currentYearFormat.format(currentLocalTime)

        binding.dateGeorgian.text = georgianFullDateFormat.format(cal.time).toString()

    }
/*
    private fun scheduleNotification(notification: Notification, delay: Int) {
        val notificationIntent = Intent(context, MyNotificationPublisher::class.java)
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION_ID, 1)
        notificationIntent.putExtra(MyNotificationPublisher.NOTIFICATION, notification)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val futureInMillis = SystemClock.elapsedRealtime() + delay
        val alarmManager = (context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager?)!!
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis] = pendingIntent
    }

    private fun getNotification(content: String): Notification? {
        val builder: NotificationCompat.Builder? =
            context?.let { NotificationCompat.Builder(it, default_notification_channel_id) }
        builder?.setContentTitle("Scheduled Notification")
        builder?.setContentText(content)
        builder?.setSmallIcon(R.drawable.ic_side_menu_nav_icon)
        builder?.setAutoCancel(true)
        builder?.setChannelId(NOTIFICATION_CHANNEL_ID)
        return builder?.build()
    }*/

    private fun getTimeOnlyForPrayer(input: String): String {
        var firstFiveChars = ""
        if (input.length > 4) {
            firstFiveChars = input.substring(0, 5)
        }
        return firstFiveChars
    }

    var prayerList: MutableList<PrayerTimeModel> = ArrayList()
    var nextPrayerIs: Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun renderParentTimings(data: List<Datum>) {
        var flagFoundPrayerId = false

        val currentHourDateFormat: DateFormat = SimpleDateFormat("HH:mm")
        val dateFormatForChecking: DateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy")
        val currentHour = currentHourDateFormat.format(cal.time)
        val currentDateForChecking = dateFormatForChecking.format(cal.time)

        for (i in 0..data.size - 1) {

            var date = data.get(i).date.gregorian.date

            var hijriDate = data[i].date.hijri.day + " " + data[i].date.hijri.month.ar +
                    " " + data[i].date.hijri.year

            var fajrAdahanModel = PrayerTimeModel(
                0, R.drawable.ic_elfajr,
                "Fajr", getTimeOnlyForPrayer(data.get(i).timings.fajr), 1, date,
                hijriDate
            )

            var sunRiseAdahanModel = PrayerTimeModel(
                1, R.drawable.ic_sunrise,
                "Sunrise", getTimeOnlyForPrayer(data.get(i).timings.sunrise), 1, date,
                hijriDate
            )

            var dhurAdahanModel = PrayerTimeModel(
                2, R.drawable.ic_eldhur,
                "Dhur", getTimeOnlyForPrayer(data.get(i).timings.dhuhr), 1, date,
                hijriDate
            )

            var asrAdahanModel = PrayerTimeModel(
                3, R.drawable.ic_elasr,
                "Asr", getTimeOnlyForPrayer(data.get(i).timings.asr), 1, date,
                hijriDate
            )
/*
            var sunSetAdahanModel = PrayerTimeModel(
                4, R.drawable.ic_elfajr,
                "SunSet", data.get(i).timings.sunset, R.drawable.ic_volume_high, date
            )*/

            var maghribAdahanModel = PrayerTimeModel(
                4, R.drawable.ic_elmaghrib,
                "Maghrib", getTimeOnlyForPrayer(data.get(i).timings.maghrib), 1, date,
                hijriDate
            )

            var ishaAdahanModel = PrayerTimeModel(
                5, R.drawable.ic_elisha,
                "Isha", getTimeOnlyForPrayer(data.get(i).timings.isha), 1, date,
                hijriDate
            )
/*

            var imsakAdahanModel = PrayerTimeModel(
                7, R.drawable.ic_elfajr,
                "Imsak", data.get(i).timings.imsak, R.drawable.ic_volume_high, date
            )
*/

            /*           var midnightAdahanModel = PrayerTimeModel(
                           8, R.drawable.ic_elfajr,
                           "Midnight", data.get(i).timings.midnight, R.drawable.ic_volume_high, date
                       )
           */
            prayerList = ArrayList()
            prayerList.add(0, fajrAdahanModel)
            prayerList.add(1, sunRiseAdahanModel)
            prayerList.add(2, dhurAdahanModel)
            prayerList.add(3, asrAdahanModel)
//            supplierNames1.add(4, sunSetAdahanModel)
            prayerList.add(4, maghribAdahanModel)
            prayerList.add(5, ishaAdahanModel)
//            supplierNames1.add(7, imsakAdahanModel)
//            supplierNames1.add(6, midnightAdahanModel)
            vm.savePrayerTimes(requireContext(), prayerList)
            if (localTime.equals(date)) {
                binding.dateHijri.text =
                    data[i].date.hijri.day + " " + data[i].date.hijri.month.ar +
                            " " + data[i].date.hijri.year

                nextPrayerIs = nextPrayer(prayerList, currentHour)
                for (i in 0..prayerList.size - 1) {
                    if (nextPrayerIs == prayerList[i].prayerId) {

                        val nextPrayerTime = prayerList[i].time + " " + prayerList[i].date
                        val remainingTimeForNextPrayer =
                            remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
                        binding.remainingTimeForNextPrayer.text =
                            getString(R.string.remaining_time_for) + " " + getNameOfPrayer(
                                requireContext(), i
                            )
//                        binding.remainingTimeForNextPrayerValue.text = remainingTimeForNextPrayer

                        counterForNextPrayer(remainingTimeForNextPrayer)
                        flagFoundPrayerId = true


                        binding.prayerTimesList.adapter =
                            PrayerAdapter(
                                requireContext(),
                                prayerList,
                                nextPrayerIs,
                                this,
                                arrayList
                            )
                    }
                }
                if (!flagFoundPrayerId) {
                    vm.getPrayerTimesForSpecificDate(nextDay, requireContext())
                        .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                            if (!prayer.isNullOrEmpty()) {
                                binding.remainingTimeForNextPrayer.text =
                                    getString(R.string.remaining_time_for) + getNameOfPrayer(
                                        requireContext(),
                                        prayer[0].prayerId
                                    )
                                val nextPrayerTime = prayer[0].time + " " + prayer[0].date
                                val remainingTimeForNextPrayer =
                                    remainingTimeForNextPrayer(
                                        currentDateForChecking,
                                        nextPrayerTime
                                    )
                                /*binding.remainingTimeForNextPrayerValue.text =
                                    remainingTimeForNextPrayer*/
                                counterForNextPrayer(remainingTimeForNextPrayer)
                                Log.d("remainingTime", remainingTimeForNextPrayer)
                                binding.prayerTimesList.adapter =
                                    PrayerAdapter(
                                        requireContext(),
                                        prayerList,
                                        nextPrayerIs,
                                        this,
                                        arrayList
                                    )
                            }
                        })
                }
            }

        }

        binding.prayerTimesList.layoutManager = linearLayoutManager
        binding.prayerTimesList.isNestedScrollingEnabled = false

    }

    fun counterForNextPrayer(time: String) {
        val remainingHours = time.substring(0, 2).toLong()
        var totalConvertedMinutes = 0L
        Log.d("counterForNext", " " + time)
        if (!remainingHours.equals(0)) {
            totalConvertedMinutes = remainingHours * 60
        }

        if (time.length == 5) {
            val yourMinutes = time.substring(3, 5).toLong()

            val timer = object : CountDownTimer(
                TimeUnit.MINUTES.toMillis(totalConvertedMinutes + yourMinutes),
                1000
            ) {
                override fun onTick(millisUntilFinished: Long) {
                    //Convert milliseconds into hour,minute and seconds
                    //Convert milliseconds into hour,minute and seconds
                    val hms = java.lang.String.format(
                        "%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(
                                millisUntilFinished
                            )
                        ),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                            TimeUnit.MILLISECONDS.toMinutes(
                                millisUntilFinished
                            )
                        )
                    )
                    binding.remainingTimeForNextPrayerValue.text = hms
                }

                override fun onFinish() {

                }
            }
            timer.start()
        } else {
            return
        }
    }

    override fun renderLoading(show: Boolean) {
        if (show) {
            progressDialog.show()
            ViewUtils.showProgressDialog(
                progressDialog!!
            )
        } else
            progressDialog.dismiss()
        Log.d("renderData", "data $show")
    }

    override fun renderNetworkFailure() {
        Log.d("renderData", "data")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun initLocationListener() {
        if (!fusedLocationClient.lastLocation.equals(null)) {

            fusedLocationClient =
                activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
            fusedLocationClient?.lastLocation?.addOnSuccessListener {
                if (it == null) {
                    val getCity = vm.getCityFromPreferences()
                    if (getCity == null)
                        getUserLocation()
                    else
                        initPrayerTimes(it.latitude, it.longitude)
                } else {

                    if (getAndSetCurrentCityFromLatLon(
                            it.latitude.toString(),
                            it.longitude.toString()
                        )
                            .equals("Not Found")
                    ) {

                        binding.deviceCurrentLocation.text =
                            getString(R.string.couldnt_find_your_address)
                    } else {
                        binding.deviceCurrentLocation.text = getAndSetCurrentCityFromLatLon(
                            it.latitude.toString(),
                            it.longitude.toString()
                        )
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        initQiblaDirection(it.latitude, it.longitude)
                        initPrayerTimes(it.latitude, it.longitude)
                    }
                }
            }
            fusedLocationClient?.lastLocation?.addOnFailureListener {
                Toast.makeText(requireContext(), "Err: " + it.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    var apiCall = false

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initPrayerTimes(latitude: Double, longitude: Double) {
        linearLayoutManager = LinearLayoutManager(requireContext())
        vm.getPrayerTimesForSpecificDate(localTime, requireContext())
            .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                if (!prayer.isNullOrEmpty() && !apiCall && context != null) {
                    getPrayerTimesFromDatabase(prayer)
                    binding.prayerTimesList.layoutManager = linearLayoutManager
                    binding.prayerTimesList.isNestedScrollingEnabled = false
                    renderLoading(false)

                } else if (latitude != 0.0 && longitude != 0.0) {
                    vm.getAlAdahanAPI(
                        latitude.toString(),
                        longitude.toString(),
                        "5",
                        monthOfTheYear,
                        currentYear
                    )

                    vm.viewStateAlAdahan.observe(this, { viewState ->
                        when (viewState) {
                            is AlAdahanViewState.Loading -> {
                                renderLoading(viewState.show)
                            }
                            is AlAdahanViewState.NetworkFailure -> {
                                renderNetworkFailure()
                            }
                            is AlAdahanViewState.Data -> {
                                renderParentTimings(viewState.data)
                                apiCall = true
                                renderLoading(false)
                            }
                        }
                    })
//                    fusedLocationClient.removeLocationUpdates(locationCallback);


                    initQiblaDirection(latitude, longitude)
                    binding.deviceCurrentLocation.text =
                        getAndSetCurrentCityFromLatLon(latitude.toString(), longitude.toString())
                }
            })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPrayerTimesFromDatabase(prayer: List<PrayerTimeModel>) {
        binding.dateHijri.text =
            prayer[0].hijriDate
        val currentHourDateFormat: DateFormat = SimpleDateFormat("HH:mm")
        val currentHour = currentHourDateFormat.format(cal.time)
        val dateFormatForChecking: DateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy")
        val currentDateForChecking = dateFormatForChecking.format(cal.time)
        nextPrayerIs = nextPrayer(prayer.toMutableList(), currentHour)
        if (nextPrayerIs != 300) {

            val nextPrayerTime =
                prayer[nextPrayerIs].time + " " + prayer[nextPrayerIs].date
            val remainingTimeForNextPrayer =
                remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
            binding.remainingTimeForNextPrayer.text =
                getString(R.string.remaining_time_for) + getNameOfPrayer(
                    requireContext(), nextPrayerIs
                )

            counterForNextPrayer(remainingTimeForNextPrayer)

            binding.prayerTimesList.adapter =
                PrayerAdapter(requireContext(), prayer, nextPrayerIs, this, arrayList)

        } else if (context != null) {
            vm.getPrayerTimesForSpecificDate(nextDay, requireContext())
                .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                    if (!prayer.isNullOrEmpty() && context != null) {
                        binding.remainingTimeForNextPrayer.text =
                            getString(R.string.remaining_time_for) + getNameOfPrayer(
                                requireContext(),
                                prayer[0].prayerId
                            )

                        val nextPrayerTime = prayer[0].time + " " + prayer[0].date
                        val remainingTimeForNextPrayer =
                            remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
                        /*  binding.remainingTimeForNextPrayerValue.text =
                              remainingTimeForNextPrayer*/
                        counterForNextPrayer(remainingTimeForNextPrayer)
                        Log.d("remainingTime 3", remainingTimeForNextPrayer)
                        binding.prayerTimesList.adapter =
                            PrayerAdapter(requireContext(), prayer, nextPrayerIs, this, arrayList)
                    }
                })
        }
    }

    private fun getAndSetCurrentCityFromLatLon(latitude: String, longitude: String): String {
        if (context != null) {
            val addresses: List<Address>
            val geocoder = Geocoder(requireContext(), Locale(vm.preference.getLanguage()))
            try {
                Log.d("currentLat", " $latitude , $longitude")

                addresses = geocoder.getFromLocation(
                    latitude.toDouble(),
                    longitude.toDouble(),
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                Log.d("currentLat", " $latitude , $addresses")

                if (!addresses.isNullOrEmpty()) {
                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    var city: String = " "
                    if (addresses[0].locality != null)
                        city = addresses[0].getLocality()
                    val country: String = addresses[0].getCountryName()
                    val currentCity = "$country, $city"
                    vm.preference.setCity(currentCity)

                    return currentCity
                }
            } catch (ex: InvocationTargetException) {
                Log.d("exceptionFound", "${ex.message}")
            } catch (ex: IOException) {
                Log.d("exceptionFound", "${ex.message}")
            }

        }

        return "Not Found"

    }

    @SuppressLint("MissingPermission")
    private fun getLocationUpdates() {
        getUserLocation()
        locationRequest = LocationRequest()
        locationRequest.interval = 20 * 1000
        locationRequest.fastestInterval = 10000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        /*      fusedLocationClient.requestLocationUpdates(locationRequest,
                  locationCallback,
                  Looper.getMainLooper())*/

//        Log.d("asdasdasd" ,"is called" + fusedLocationClient.lastLocation.toString())
        locationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                if (!locationResult.equals(null)) {
                    if (locationResult.locations.isNotEmpty()) {
                        val location =
                            locationResult.lastLocation
//                        initPrayerTimes(location.latitude,location.longitude)
                    } else {

                        fusedLocationClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )

                        Log.d("asdasdasd", fusedLocationClient.lastLocation.toString())

                    }
                } else {

                    fusedLocationClient.requestLocationUpdates(
                        locationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                    )

                    Log.d("asdasdasd", fusedLocationClient.lastLocation.toString())

                }
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initQiblaDirection(latitude: Double, longitude: Double) {
        userLocation.latitude = latitude
        userLocation.longitude = longitude
        Log.d("getLatitude", " : " + latitude + " , " + longitude)
        if (context != null) {
            sensorManager =
                requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
            sensorManager.registerListener(object : SensorEventListener {
                override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

                }

                @SuppressLint("SetTextI18n")
                override fun onSensorChanged(sensorEvent: SensorEvent?) {
                    val degree: Float = sensorEvent?.values?.get(0)?.roundToInt()?.toFloat()!!
                    var head: Float = sensorEvent.values?.get(0)?.roundToInt()?.toFloat()!!

                    val destLocation = Location("Destination Location")
                    destLocation.latitude = QIBLA_LATITUDE
                    destLocation.longitude = QIBLA_LONGITUDE

                    var bearTo = userLocation.bearingTo(destLocation)

                    val geoField = GeomagneticField(
                        userLocation.latitude.toFloat(),
                        userLocation.longitude.toFloat(),
                        userLocation.altitude.toFloat(),
                        System.currentTimeMillis()
                    )

                    head -= geoField.declination

                    if (bearTo < 0) {
                        bearTo += 360
                    }

                    var direction = bearTo - head

                    if (direction < 0) {
                        direction += 360
                    }

                    tvHeading.text = "Heading : $degree + degrees"

                    Log.d(
                        TAG,
                        "Needle Degree : $currentNeedleDegree, Direction : $direction"
                    )
                    binding.qiblahDirection.text = currentNeedleDegree.toString()

                    needleAnimation = RotateAnimation(
                        currentNeedleDegree,
                        direction,
                        Animation.RELATIVE_TO_SELF,
                        .5f,
                        Animation.RELATIVE_TO_SELF,
                        .5f
                    )
                    needleAnimation.fillAfter = true
                    needleAnimation.duration = 200

                    binding.ivQiblaDirection.startAnimation(needleAnimation)
                    currentNeedleDegree = direction
                    currentDegree = -degree

                    if (currentNeedleDegree <= 10 || currentNeedleDegree >= 350) {
                        context?.resources?.let {
                            binding.ivQiblaDirection.setColorFilter(
                                it?.getColor(
                                    R.color.logoOrangeColor
                                )
                            )
                        }

                    } else {
                        context?.resources?.getColor(R.color.backgroundGreen)?.let {
                            binding.ivQiblaDirection.setColorFilter(
                                it
                            )
                        }

                    }

                }
            }, sensor, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.remainingTimeForNextPrayerValue.text = ""
        getUserLocation()
        renderLoading(true)
    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {

        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            smallestDisplacement = 100f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            Log.d("getuserlocation", " getuserlocation: entered1")
        }

        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(this.requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {

            val flag = it.locationSettingsStates?.isLocationUsable
            if (flag == true) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        val location = locationResult.lastLocation
                        location?.let {
                            Log.e("onLocationResult", " onLocationResult: $location")
                            initLocationListener()
                        }

                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback!!,
                    Looper.getMainLooper()
                )
            } else {
                Log.e("flagLocation", " is calse")
            }

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                Log.d(null, "setuserlocation: location failure")
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(this.requireActivity(), 0x1)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }


    }

    override fun prayerId(id: Int) {
        if (id == 0 && vm.preference.getFajr())
            vm.preference.setFajr(false)
        else if (id == 0 && !vm.preference.getFajr())
            vm.preference.setFajr(true)
        else if (id == 1 && vm.preference.getSunRise())
            vm.preference.setSunRise(false)
        else if (id == 1 && !vm.preference.getSunRise())
            vm.preference.setSunRise(true)
        else if (id == 2 && vm.preference.getDuhr())
            vm.preference.setDuhr(false)
        else if (id == 2 && !vm.preference.getDuhr())
            vm.preference.setDuhr(true)
        else if (id == 3 && vm.preference.getAsr())
            vm.preference.setAsr(false)
        else if (id == 3 && !vm.preference.getAsr())
            vm.preference.setAsr(true)
        else if (id == 4 && vm.preference.getMaghrib())
            vm.preference.setMaghrib(false)
        else if (id == 4 && !vm.preference.getMaghrib())
            vm.preference.setMaghrib(true)
        else if (id == 5 && vm.preference.getIsha())
            vm.preference.setIsha(false)
        else if (id == 5 && !vm.preference.getIsha())
            vm.preference.setIsha(true)
//        Glide.with(this).load(R.drawable.app_widget_background).into()
        Log.d("ldksldksd", " ${vm.preference.getFajr()} prayer  Fajr")
        Log.d("ldksldksd", " ${vm.preference.getDuhr()} prayer  Duhr")
        Log.d("ldksldksd", " ${vm.preference.getAsr()} prayer  Asr")
        Log.d("ldksldksd", " ${vm.preference.getMaghrib()} prayer  Maghrib")
    }
}