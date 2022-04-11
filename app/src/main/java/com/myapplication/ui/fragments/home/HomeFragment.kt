package com.myapplication.ui.fragments.home

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.hardware.*
import android.location.Address
import android.location.Location
import android.os.Bundle
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
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import com.myapplication.LocaleUtil.Companion.getNameOfPrayerInArabic
import com.myapplication.LocaleUtil.Companion.nextPrayer
import com.myapplication.LocaleUtil.Companion.remainingTimeForNextPrayer
import java.util.concurrent.TimeUnit

import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.tasks.Task

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.myapplication.LocaleUtil.Companion.applyLocalizedContext
import com.myapplication.ui.sidemenu.QiblahActivity
import com.myapplication.R
import com.myapplication.data.repositories.SharedPreferencesRepository
import com.myapplication.ui.ViewUtils
import java.io.IOException
import java.lang.reflect.InvocationTargetException


class HomeFragment : Fragment(), AlAdahanUseCases.View, PrayerSoundClickListener,
    SensorEventListener {

    lateinit var binding: FragmentHomeBinding

    var prayerList: MutableList<PrayerTimeModel> = ArrayList()
    var nextPrayersId: Int = 0

    companion object {
        const val MARSHMALLOW = 23

        const val ACCESS_FINE_LOCATION_REQ_CODE = 35

        const val QIBLA_LATITUDE = 21.3891
        const val QIBLA_LONGITUDE = 39.8579
    }

    var currentDegree: Float = 0f
    var currentNeedleDegree: Float = 0f
    var countDownTimer: CountDownTimer? = null

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var preferences: SharedPreferencesRepository

    var sensorManager: SensorManager? = null
    var sensor: Sensor? = null
    var userLocation: Location? = null
    lateinit var tvHeading: TextView
    var needleAnimation: RotateAnimation? = null
    var fusedLocationClient: FusedLocationProviderClient? = null
    lateinit var currentTime: TextView
    lateinit var localTime: String
    var monthOfTheYear = "null"
    var currentYear = "null"
    private val homeFragmentViewModel: HomeViewModel by viewModels()
    val NOTIFICATION_CHANNEL_ID = "10001"
    private val default_notification_channel_id = "default"
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
    lateinit var nextDay: String
    lateinit var progressDialog: Dialog
    var arrayList: MutableList<Boolean> = ArrayList()
    var apiCall = false

    lateinit var nextDayForCalculations: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        sensorManager =
            requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeFragmentViewModel.preference.getLanguage()?.let { context?.let { it1 -> applyLocalizedContext(it1, it) } }
        var view: View = inflater.inflate(R.layout.fragment_home, container, false)
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_home, container, false
        )
        binding.homeViewmodel = homeFragmentViewModel
//        vm.preference.getLanguage()?.let { context?.let { it1 -> applyLocalizedContext(it1, it) } }

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

    fun initViews() {
        userLocation = Location("User Location")
        arrayList.add(0, homeFragmentViewModel.preference.getFajr())
        arrayList.add(1, homeFragmentViewModel.preference.getDuhr())
        arrayList.add(2, homeFragmentViewModel.preference.getSunRise())
        arrayList.add(3, homeFragmentViewModel.preference.getAsr())
        arrayList.add(4, homeFragmentViewModel.preference.getMaghrib())
        arrayList.add(5, homeFragmentViewModel.preference.getIsha())
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
                R.anim.fragment_sidemenu_exit_animation,
                R.anim.fragment_sidemenu_enter_animation,
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
        val georgianFullDateFormatEn: DateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale("en"))
        val georgianDateFormatForInsertion: DateFormat =
            SimpleDateFormat("dd-MM-yyyy", Locale("en"))
        val georgianDateFormatForChecking: DateFormat =
            SimpleDateFormat("hh:mm dd-MM-yyyy", Locale("en"))

        val currentLocalTime = cal.time
        val monthOfTheyYearFormat: DateFormat = SimpleDateFormat("MM")
        val currentYearFormat: DateFormat = SimpleDateFormat("yyyy")

        localTime = georgianDateFormatForInsertion.format(cal.time)

        val nextDayCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
        nextDayCal.add(Calendar.DAY_OF_MONTH, 1)
        nextDay = georgianDateFormatForInsertion.format(nextDayCal.time)
        nextDayForCalculations = georgianDateFormatForChecking.format(nextDayCal.time)

        monthOfTheYear = monthOfTheyYearFormat.format(currentLocalTime)
        currentYear = currentYearFormat.format(currentLocalTime)

        if(homeFragmentViewModel.preference.getLanguage().equals("ar")) {
            binding.dateGeorgian.text = georgianFullDateFormat.format(cal.time).toString()
        }else{
            binding.dateGeorgian.text = georgianFullDateFormatEn.format(cal.time).toString()

        }
    }

    private fun getTimeOnlyForPrayer(input: String): String {
        var firstFiveChars = ""
        if (input.length > 4) {
            firstFiveChars = input.substring(0, 5)
        }
        return firstFiveChars
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun renderParentTimings(data: List<Datum>) {
        var flagFoundPrayerId = false

        val currentHourDateFormat: DateFormat = SimpleDateFormat("HH:mm", Locale("en"))
        val dateFormatForChecking: DateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale("en"))
        val currentHour = currentHourDateFormat.format(cal.time)
        val currentDateForChecking = dateFormatForChecking.format(cal.time)

        for (i in data.indices) {

            var date = data[i].date.gregorian.date

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
            prayerList = ArrayList()
            prayerList.add(0, fajrAdahanModel)
            prayerList.add(1, sunRiseAdahanModel)
            prayerList.add(2, dhurAdahanModel)
            prayerList.add(3, asrAdahanModel)
            prayerList.add(4, maghribAdahanModel)
            prayerList.add(5, ishaAdahanModel)

            homeFragmentViewModel.savePrayerTimes(requireContext(), prayerList)
            if (localTime.equals(date)) {
                if(homeFragmentViewModel.preference.getLanguage().equals("ar")) {
                    binding.dateHijri.text =
                        data[i].date.hijri.day + " " + data[i].date.hijri.month.ar +
                                " " + data[i].date.hijri.year
                }else{
                    binding.dateHijri.text =
                        data[i].date.hijri.day + " " + data[i].date.hijri.month.en +
                                " " + data[i].date.hijri.year
                }

                nextPrayersId = nextPrayer(prayerList, currentHour)
                for (i in 0..prayerList.size - 1) {
                    if (nextPrayersId == prayerList[i].prayerId) {

                        val nextPrayerTime = prayerList[i].time + " " + prayerList[i].date
                        val remainingTimeForNextPrayer =
                            remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
                        binding.remainingTimeForNextPrayer.text =
                            getString(R.string.remaining_time_for) + " " + getNameOfPrayerInArabic(
                                requireContext(), i
                            )

                        counterForNextPrayer(remainingTimeForNextPrayer)
                        flagFoundPrayerId = true

                        binding.prayerTimesList.adapter =
                            homeFragmentViewModel.preference.getLanguage()?.let {
                                PrayerAdapter(
                                    requireContext(),
                                    prayerList,
                                    nextPrayersId,
                                    this,
                                    arrayList,
                                    it
                                )
                            }
                    }
                }
                if (!flagFoundPrayerId) {
                    homeFragmentViewModel.getPrayerTimesForSpecificDate(nextDay, requireContext())
                        .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                            if (!prayer.isNullOrEmpty() && context != null) {
                                binding.remainingTimeForNextPrayer.text =
                                    getString(R.string.remaining_time_for) + getNameOfPrayerInArabic(
                                        requireContext(),
                                        prayer[0].prayerId
                                    )
                                val nextPrayerTime = prayer[0].time + " " + prayer[0].date
                                val remainingTimeForNextPrayer =
                                    remainingTimeForNextPrayer(
                                        currentDateForChecking,
                                        nextPrayerTime
                                    )
                                counterForNextPrayer(remainingTimeForNextPrayer)
                                binding.prayerTimesList.adapter =
                                    homeFragmentViewModel.preference.getLanguage()?.let {
                                        PrayerAdapter(
                                            requireContext(),
                                            prayerList,
                                            nextPrayersId,
                                            this,
                                            arrayList,
                                            it
                                        )
                                    }
                            }
                        })
                }
            }

        }

        binding.prayerTimesList.isNestedScrollingEnabled = false
    }

    private fun counterForNextPrayer(time: String) {
        val remainingHours = time.substring(0, 2).toLong()
        var totalConvertedMinutes = 0L
        if (!remainingHours.equals(0)) {
            totalConvertedMinutes = remainingHours * 60
        }

        if (time.length == 5) {
            val yourMinutes = time.substring(3, 5).toLong()
            countDownTimer?.cancel()
            countDownTimer = object : CountDownTimer(
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
            countDownTimer?.start()
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
    }

    override fun renderNetworkFailure() {
        apiCall = false
        initLocationListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("MissingPermission")
    private fun initLocationListener() {
        fusedLocationClient =
            activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        fusedLocationClient?.lastLocation?.addOnSuccessListener {

            if (getAndSetCurrentCityFromLatLon(
                    it.latitude.toString(),
                    it.longitude.toString()
                ) == "Not Found"
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
                homeFragmentViewModel.preference.setLat(it.latitude.toString())
                homeFragmentViewModel.preference.setLong(it.longitude.toString())
                initQiblaDirection(it.latitude, it.longitude)
                initPrayerTimes(it.latitude, it.longitude)
            }
        }
        fusedLocationClient?.lastLocation?.addOnFailureListener {
            Toast.makeText(requireContext(), "Err: " + it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initPrayerTimes(latitude: Double, longitude: Double) {
//        linearLayoutManager = LinearLayoutManager(requireContext())
        homeFragmentViewModel.getPrayerTimesForSpecificDate(localTime, requireContext())
            .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                if (!prayer.isNullOrEmpty() && !apiCall && context != null) {
                    getPrayerTimesFromDatabase(prayer)
                    binding.prayerTimesList.isNestedScrollingEnabled = false
                    renderLoading(false)

                } else if (latitude != 0.0 && longitude != 0.0) {
                    homeFragmentViewModel.getAlAdahanAPI(
                        latitude.toString(),
                        longitude.toString(),
                        "5",
                        monthOfTheYear,
                        currentYear
                    )

                    homeFragmentViewModel.viewStateAlAdahan.observe(this) { viewState ->
                        when (viewState) {
                            is AlAdahanViewState.Loading -> {
                                renderLoading(viewState.show)
                            }
                            is AlAdahanViewState.NetworkFailure -> {
                                renderNetworkFailure()
                                renderLoading(false)
                            }
                            is AlAdahanViewState.Data -> {
                                renderParentTimings(viewState.data)
                                apiCall = true
                                renderLoading(false)
                            }
                            else -> renderLoading(false)
                        }
                    }

                    initQiblaDirection(latitude, longitude)
                }
                binding.deviceCurrentLocation.text =
                    getAndSetCurrentCityFromLatLon(latitude.toString(), longitude.toString())
            })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPrayerTimesFromDatabase(prayer: List<PrayerTimeModel>) {
        val date : String
        if(homeFragmentViewModel.preference.getLanguage().equals("ar"))
            date= prayer[0].hijriDate
        else
            date = prayer[0].hijriDateEn

        binding.dateHijri.text = date

        val currentHourDateFormat: DateFormat = SimpleDateFormat("HH:mm", Locale("en"))
        val currentHour = currentHourDateFormat.format(cal.time)
        val dateFormatForChecking: DateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale("en"))
        val currentDateForChecking = dateFormatForChecking.format(cal.time)
        nextPrayersId = nextPrayer(prayer.toMutableList(), currentHour)
        if (nextPrayersId != 300) {

            val nextPrayerTime =
                prayer[nextPrayersId].time + " " + prayer[nextPrayersId].date
            val remainingTimeForNextPrayer =
                remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
            binding.remainingTimeForNextPrayer.text =
                getString(R.string.remaining_time_for) + getNameOfPrayerInArabic(
                    requireContext(), nextPrayersId
                )

            counterForNextPrayer(remainingTimeForNextPrayer)

            binding.prayerTimesList.adapter =
                homeFragmentViewModel.preference.getLanguage()?.let {
                    PrayerAdapter(
                        requireContext(), prayer, nextPrayersId, this, arrayList,
                        it
                    )
                }

        } else if (context != null) {
            homeFragmentViewModel.getPrayerTimesForSpecificDate(nextDay, requireContext())
                .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                    if (!prayer.isNullOrEmpty() && context != null) {
                        binding.remainingTimeForNextPrayer.text =
                            getString(R.string.remaining_time_for) + getNameOfPrayerInArabic(
                                requireContext(),
                                prayer[0].prayerId
                            )

                        val nextPrayerTime = prayer[0].time + " " + prayer[0].date
                        val remainingTimeForNextPrayer =
                            remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)

                        counterForNextPrayer(remainingTimeForNextPrayer)
                        binding.prayerTimesList.adapter =
                            homeFragmentViewModel.preference.getLanguage()?.let {
                                PrayerAdapter(
                                    requireContext(), prayer, nextPrayersId, this, arrayList,
                                    it
                                )
                            }
                    }
                })
        }
    }

    private fun getAndSetCurrentCityFromLatLon(latitude: String, longitude: String): String {
        if (context != null) {
            val addresses: List<Address>
            val geocoder = Geocoder(requireContext(), Locale(homeFragmentViewModel.preference.getLanguage()))
            try {

                addresses = geocoder.getFromLocation(
                    latitude.toDouble(),
                    longitude.toDouble(),
                    1
                ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                if (!addresses.isNullOrEmpty()) {
                    val address: String =
                        addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    var city: String = " "
                    if (addresses[0].locality != null)
                        city = addresses[0].getLocality()
                    val country: String = addresses[0].getCountryName()
                    val currentCity = "$country, $city"
                    if (currentCity != homeFragmentViewModel.preference.getCity())
                        apiCall = true

                    homeFragmentViewModel.preference.setCity(currentCity)

                    return currentCity
                }
            } catch (ex: InvocationTargetException) {
            } catch (ex: IOException) {
            }

        }

        return "Not Found"

    }

    @SuppressLint("MissingPermission")
    private fun getLocationUpdates() {
        getUserLocation()
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
                        if (homeFragmentViewModel.preference.getLat().isNullOrBlank() ||
                            homeFragmentViewModel.preference.getLat().isNullOrEmpty()
                        ) {
                            fusedLocationClient?.requestLocationUpdates(
                                locationRequest,
                                locationCallback,
                                Looper.getMainLooper()
                            )
                        } else {
                            homeFragmentViewModel.preference.getLong()?.let { it1 ->
                                homeFragmentViewModel.preference.getLat()?.toDouble()?.let { it2 ->
                                    initPrayerTimes(
                                        it2,
                                        it1.toDouble()
                                    )
                                }
                            }
                        }

                    }
                } else {
                    if (homeFragmentViewModel.preference.getLat().isNullOrBlank() ||
                        homeFragmentViewModel.preference.getLat().isNullOrEmpty()
                    ) {
                    } else {
                        homeFragmentViewModel.preference.getLong()?.let { it1 ->
                            homeFragmentViewModel.preference.getLat()?.toDouble()?.let { it2 ->
                                initPrayerTimes(
                                    it2,
                                    it1.toDouble()
                                )
                            }
                        }
                    }

                }
            }
        }


    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun initQiblaDirection(latitude: Double, longitude: Double) {
        userLocation?.latitude = latitude
        userLocation?.longitude = longitude
        Log.d("getLat", " $latitude , $longitude")

        if (context != null) {
            sensorManager?.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val degree: Float = event?.values?.get(0)?.roundToInt()?.toFloat()!!
        var head: Float = event.values?.get(0)?.roundToInt()?.toFloat()!!

        val destLocation = Location("Destination Location")
        destLocation.latitude = HomeFragment.QIBLA_LATITUDE
        destLocation.longitude = HomeFragment.QIBLA_LONGITUDE

        var bearTo = userLocation?.bearingTo(destLocation)

        val geoField = userLocation?.latitude?.let {
            userLocation?.longitude?.let { it1 ->
                userLocation?.altitude?.let { it2 ->
                    GeomagneticField(
                        it.toFloat(),
                        it1.toFloat(),
                        it2.toFloat(),
                        System.currentTimeMillis()
                    )
                }
            }
        }

        head -= geoField?.declination!!

        if (bearTo != null) {
            if (bearTo < 0) {
                bearTo += 360
            }
        }


        var direction = bearTo?.minus(head)

        if (direction != null) {
            if (direction < 0) {
                direction += 360
            }
        }

        binding.qiblahDirection.text = "Heading : $degree + degrees"

        binding.qiblahDirection.text = currentNeedleDegree.toString()

        needleAnimation = direction?.let {
            RotateAnimation(
                currentNeedleDegree,
                it,
                Animation.RELATIVE_TO_SELF,
                .5f,
                Animation.RELATIVE_TO_SELF,
                .5f
            )
        }
        needleAnimation?.fillAfter = true
        needleAnimation?.duration = 200

        binding.ivQiblaDirection.startAnimation(needleAnimation)
        if (direction != null) {
            currentNeedleDegree = direction
        }
        currentDegree = -degree

        if (currentNeedleDegree <= 10 || currentNeedleDegree >= 350) {
            context?.getColor(R.color.logoOrangeColor)?.let {
                binding.ivQiblaDirection.setColorFilter(
                    it
                )
            }
            context?.getColor(R.color.logoOrangeColor)?.let {
                binding.qiblahDirection.setTextColor(
                    it
                )
            }
        } else {

            context?.getColor(R.color.backgroundGreen)?.let {
                binding.ivQiblaDirection.setColorFilter(
                    it
                )
            }

            context?.getColor(R.color.textColorQiblahDegrees)?.let {
                binding.qiblahDirection.setTextColor(
                    it
                )
            }


        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onPause() {
        super.onPause()
        countDownTimer?.cancel()
        binding.remainingTimeForNextPrayerValue.text = ""

    }

    override fun onResume() {
        super.onResume()
        renderLoading(true)
        if (!handleLocalPrayerData()) {
            getUserLocation()
            sensorManager?.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(this)

    }

    @SuppressLint("MissingPermission")
    fun getUserLocation() {
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            smallestDisplacement = 100f
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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
                            initLocationListener()
                        }

                    }
                }
                fusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
                handleLocalPrayerData()
            }

        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
//                    exception.startResolutionForResult(this.requireActivity(), 0x1)
                    handleLocalPrayerData()
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }


    }

    private fun handleLocalPrayerData(): Boolean {
        if (!homeFragmentViewModel.preference.getLat().isNullOrEmpty() || !homeFragmentViewModel.preference.getLat().isNullOrBlank()) {
            homeFragmentViewModel.preference.getLong()?.let { it1 ->
                homeFragmentViewModel.preference.getLat()?.toDouble()?.let { it2 ->
                    initPrayerTimes(
                        it2,
                        it1.toDouble()
                    )
                }
            }
            return true
        }
        return false
    }

    override fun prayerId(id: Int) {
        if (id == 0 && homeFragmentViewModel.preference.getFajr())
            homeFragmentViewModel.preference.setFajr(false)
        else if (id == 0 && !homeFragmentViewModel.preference.getFajr())
            homeFragmentViewModel.preference.setFajr(true)
        else if (id == 1 && homeFragmentViewModel.preference.getSunRise())
            homeFragmentViewModel.preference.setSunRise(false)
        else if (id == 1 && !homeFragmentViewModel.preference.getSunRise())
            homeFragmentViewModel.preference.setSunRise(true)
        else if (id == 2 && homeFragmentViewModel.preference.getDuhr())
            homeFragmentViewModel.preference.setDuhr(false)
        else if (id == 2 && !homeFragmentViewModel.preference.getDuhr())
            homeFragmentViewModel.preference.setDuhr(true)
        else if (id == 3 && homeFragmentViewModel.preference.getAsr())
            homeFragmentViewModel.preference.setAsr(false)
        else if (id == 3 && !homeFragmentViewModel.preference.getAsr())
            homeFragmentViewModel.preference.setAsr(true)
        else if (id == 4 && homeFragmentViewModel.preference.getMaghrib())
            homeFragmentViewModel.preference.setMaghrib(false)
        else if (id == 4 && !homeFragmentViewModel.preference.getMaghrib())
            homeFragmentViewModel.preference.setMaghrib(true)
        else if (id == 5 && homeFragmentViewModel.preference.getIsha())
            homeFragmentViewModel.preference.setIsha(false)
        else if (id == 5 && !homeFragmentViewModel.preference.getIsha())
            homeFragmentViewModel.preference.setIsha(true)

    }
}