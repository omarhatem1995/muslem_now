package com.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import android.location.Address
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.myapplication.databinding.FragmentHomeBinding
import com.myapplication.ui.fragments.home.HomeViewModel
import kotlin.math.roundToInt

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.myapplication.data.entities.model.Datum
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.domain.usecases.ui.AlAdahanUseCases
import com.myapplication.ui.entities.AlAdahanViewState
import com.myapplication.ui.fragments.home.PrayerAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import android.location.Geocoder
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import com.myapplication.LocaleUtil.Companion.getNameOfPrayer
import com.myapplication.LocaleUtil.Companion.nextPrayer
import com.myapplication.LocaleUtil.Companion.remainingTimeForNextPrayer
import java.util.concurrent.TimeUnit


class HomeFragment : Fragment(), AlAdahanUseCases.View {

    lateinit var binding: FragmentHomeBinding
    private lateinit var vm: HomeViewModel

    companion object {
        const val TAG = "HomeFragmentLog"
        const val MARSHMALLOW = 23

        const val ACCESS_FINE_LOCATION_REQ_CODE = 35

        const val QIBLA_LATITUDE = 21.3891
        const val QIBLA_LONGITUDE = 39.8579
    }

    var currentDegree: Float = 0f
    var currentNeedleDegree: Float = 0f

    lateinit var sensorManager: SensorManager
    lateinit var sensor: Sensor
    lateinit var userLocation: Location
    lateinit var tvHeading: TextView
    lateinit var needleAnimation: RotateAnimation
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var recyclerViewPrayer: RecyclerView
    lateinit var prayerAdapter: PrayerAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var currentTime: TextView
    lateinit var localTime: String
    var monthOfTheYear = "null"
    var currentYear = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = activity?.run {
            ViewModelProviders.of(this)[HomeViewModel::class.java]
        } ?: throw Exception("Invalid Activity")
        requireActivity().getWindow().setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        )
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

        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        binding.homeViewmodel = homeViewModel
        binding.lifecycleOwner = this
        userLocation = Location("User Location")

        initLocationListener()
        getDateAndTime()

        vm.viewStateAlAdahan.observe(viewLifecycleOwner, { viewState ->
            when (viewState) {
                is AlAdahanViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is AlAdahanViewState.NetworkFailure -> {
                    renderNetworkFailure()
                }
                is AlAdahanViewState.Data -> {
                    renderParentTimings(viewState.data)
                }
            }
        })
//        binding.dateGeorgian.text = localTime

        return binding.root
    }
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))

    lateinit var nextDay: String
    lateinit var nextDayForCalculations: String
    private fun getDateAndTime() {
        val SAUDI_ARABIA = DateTimeZone.forID("Asia/Riyadh")
        val georgianFullDateFormat: DateFormat = SimpleDateFormat("EEEE dd MMMM yyyy", Locale("ar"))
        val georgianDateFormatForInsertion: DateFormat =
            SimpleDateFormat("dd-MM-yyyy", Locale("en"))
        val georgianDateFormatForChecking: DateFormat =
            SimpleDateFormat("hh:mm dd-MM-yyyy", Locale("en"))

        val currentLocalTime = cal.time
        val monthOfTheyYearFormat: DateFormat = SimpleDateFormat("MM")
        val currentYearFormat: DateFormat = SimpleDateFormat("yyyy")
        val sdf = SimpleDateFormat("EEEE")

        localTime = georgianDateFormatForInsertion.format(cal.time)

        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val dayOfTheWeek = cal.get(Calendar.DAY_OF_WEEK)

        val nextDayCal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
        nextDayCal.add(Calendar.DAY_OF_MONTH, 1)
        nextDay = georgianDateFormatForInsertion.format(nextDayCal.time)
        nextDayForCalculations = georgianDateFormatForChecking.format(nextDayCal.time)

        val dtISO = DateTime(year, month + 1, day, 23, 0, 0, 0)

        monthOfTheYear = monthOfTheyYearFormat.format(currentLocalTime)
        currentYear = currentYearFormat.format(currentLocalTime)

        binding.dateGeorgian.text = georgianFullDateFormat.format(cal.time).toString()

        /*         binding.dateHijri.text = "" + dtISO
                     .withChronology(IslamicChronology.getInstance(SAUDI_ARABIA)).dayOfMonth + " " + hijriMonthName(dtISO)+ " " + dtISO
                     .withChronology(IslamicChronology.getInstance(SAUDI_ARABIA)).year*/

    }

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
                "Isha", getTimeOnlyForPrayer(data.get(i).timings.isha), 1, date ,
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
                Log.d("currentTime", " is " + localTime + " , " + date)
                binding.dateHijri.text =
                    data[i].date.hijri.day + " " + data[i].date.hijri.month.ar +
                            " " + data[i].date.hijri.year

                nextPrayerIs = nextPrayer(prayerList, currentHour)
                Log.d("nextPrayerIs", " " + nextPrayerIs.toString())
                for (i in 0..prayerList.size - 1) {
                    if (nextPrayerIs == prayerList[i].prayerId) {
                        Log.d(
                            "nextPrayerIs", " the If" + nextPrayerIs + " , " +
                                    prayerList[i].prayerId + " , " + prayerList[i].name + " ," +
                                    prayerList[i].time
                        )

                        val nextPrayerTime = prayerList[i].time + " " + prayerList[i].date
                        Log.d("nextPrayerTim" , nextPrayerTime)
                        val remainingTimeForNextPrayer =
                            remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
                        binding.remainingTimeForNextPrayer.text =
                            getString(R.string.remaining_time_for) + getNameOfPrayer(
                                requireContext(), i
                            )
                        binding.remainingTimeForNextPrayerValue.text = remainingTimeForNextPrayer

                        counterForNextPrayer(remainingTimeForNextPrayer)
                        flagFoundPrayerId = true

                        binding.prayerTimesList.adapter =
                            PrayerAdapter(requireContext(), prayerList, nextPrayerIs)
                    }
/*
                        vm.getPrayerTimes(requireContext())
                            .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                                Log.d("uuu222sd", "sdsd" + prayer)
                            })*/
                }
                if (!flagFoundPrayerId) {
                    Log.d("nextPrayerIs", " the else" + flagFoundPrayerId)
                    vm.getPrayerTimesForSpecificDate(nextDay, requireContext())
                        .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                            Log.d("uuuiasd", " entered else sdsd " + prayer)

                            if (!prayer.isNullOrEmpty()) {
                                binding.remainingTimeForNextPrayer.text =
                                    getString(R.string.remaining_time_for) + getNameOfPrayer(
                                        requireContext(),
                                        prayer[0].prayerId
                                    )

                                Log.d(
                                    "nextPrayerName", getNameOfPrayer(
                                        requireContext(),
                                        prayer[0].prayerId
                                    )
                                )

                                val nextPrayerTime = prayer[0].time + " " + prayer[0].date
                                Log.d(
                                    "prayerList",
                                    "" + nextPrayerIs + " , " + nextPrayerTime
                                )
                                val remainingTimeForNextPrayer =
                                    remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
                                binding.remainingTimeForNextPrayerValue.text =
                                    remainingTimeForNextPrayer
                                counterForNextPrayer(remainingTimeForNextPrayer)

                                binding.prayerTimesList.adapter =
                                    PrayerAdapter(requireContext(), prayerList, nextPrayerIs)
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
        if (!remainingHours.equals(0)) {
            totalConvertedMinutes = remainingHours * 60
        }
        Log.d("yourMinutes", " is " + time)

        if (time.length == 5) {
            Log.d("yourMinutes", " is " + time)
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
                    Log.d("countDown", " + " + hms) //set text
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
        Log.d("renderData", "data" + show)
    }

    override fun renderNetworkFailure() {
        Log.d("renderData", "data")
    }

    private fun initLocationListener() {
        fusedLocationClient =
            activity?.let { LocationServices.getFusedLocationProviderClient(it) }!!
        fusedLocationClient?.lastLocation?.addOnSuccessListener {
            if (it == null) {
                getLocationUpdates()
            } else {
                Log.d(
                    TAG,
                    "User Location : Lat : ${it.latitude} Long : ${it.longitude}"
                )
                getCurrentCityFromLatLon(it.latitude.toString(), it.longitude.toString())
                initQiblaDirection(it.latitude, it.longitude)
            }
        }
        fusedLocationClient?.lastLocation?.addOnFailureListener {
            Toast.makeText(requireContext(), "Err: " + it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun getCurrentCityFromLatLon(latitude: String, longitude: String) {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(requireContext(), Locale("ar"))

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
            binding.deviceCurrentLocation.text = country + getString(R.string.comma) + city
        }
/*

        val state: String = addresses[0].getAdminArea()
*/
/*
        val postalCode: String = addresses[0].getPostalCode()
        val knownName: String = addresses[0].getFeatureName()
*/

    }

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private fun getLocationUpdates() {

        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 10000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority =
            LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
        Log.d(TAG, " is not" + locationRequest)
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                if (locationResult.locations.isNotEmpty()) {
                    // get latest location
                    val location =
                        locationResult.lastLocation
                    // use your location object
                    // get latitude , longitude and other info from this
                }


            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initQiblaDirection(latitude: Double, longitude: Double) {
        userLocation.latitude = latitude
        userLocation.longitude = longitude

        linearLayoutManager = LinearLayoutManager(requireContext())
        vm.getPrayerTimesForSpecificDate(localTime, requireContext())
            .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                if(!prayer.isNullOrEmpty()){
                    Log.d("currentDay", prayer[0].date)
                    binding.dateHijri.text =
                        prayer[0].hijriDate
                    val currentHourDateFormat: DateFormat = SimpleDateFormat("HH:mm")
                    val currentHour = currentHourDateFormat.format(cal.time)
                    val dateFormatForChecking: DateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy")
                    val currentDateForChecking = dateFormatForChecking.format(cal.time)
                    nextPrayerIs = nextPrayer(prayer.toMutableList(), currentHour)
                    if(nextPrayerIs!=300) {

                        val nextPrayerTime =
                            prayer[nextPrayerIs].time + " " + prayer[nextPrayerIs].date
                        val remainingTimeForNextPrayer =
                            remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
                        binding.remainingTimeForNextPrayer.text =
                            getString(R.string.remaining_time_for) + getNameOfPrayer(
                                requireContext(), nextPrayerIs
                            )
                        binding.remainingTimeForNextPrayerValue.text = remainingTimeForNextPrayer

                        counterForNextPrayer(remainingTimeForNextPrayer)

                        binding.prayerTimesList.adapter =
                            PrayerAdapter(requireContext(), prayer, nextPrayerIs)

                    }else{
                        vm.getPrayerTimesForSpecificDate(nextDay, requireContext())
                            .observe(requireActivity(), androidx.lifecycle.Observer { prayer ->
                                Log.d("uuuiasd", " entered else sdsd " + prayer)

                                if (!prayer.isNullOrEmpty()) {
                                    binding.remainingTimeForNextPrayer.text =
                                        getString(R.string.remaining_time_for) + getNameOfPrayer(
                                            requireContext(),
                                            prayer[0].prayerId
                                        )

                                    Log.d(
                                        "nextPrayerName", getNameOfPrayer(
                                            requireContext(),
                                            prayer[0].prayerId
                                        )
                                    )

                                    val nextPrayerTime = prayer[0].time + " " + prayer[0].date
                                    Log.d(
                                        "prayerList",
                                        "" + nextPrayerIs + " , " + nextPrayerTime
                                    )
                                    val remainingTimeForNextPrayer =
                                        remainingTimeForNextPrayer(currentDateForChecking, nextPrayerTime)
                                    binding.remainingTimeForNextPrayerValue.text =
                                        remainingTimeForNextPrayer
                                    counterForNextPrayer(remainingTimeForNextPrayer)

                                    binding.prayerTimesList.adapter =
                                        PrayerAdapter(requireContext(), prayer, nextPrayerIs)
                                }
                            })
                    }
                    binding.prayerTimesList.layoutManager = linearLayoutManager
                    binding.prayerTimesList.isNestedScrollingEnabled = false

                }else {
                    vm.getAlAdahanAPI(
                        userLocation.latitude.toString(), userLocation.longitude.toString(), "5",
                        monthOfTheYear, currentYear
                    )
                }
            })

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        sensorManager.registerListener(object : SensorEventListener {
            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }

            @SuppressLint("SetTextI18n")
            override fun onSensorChanged(sensorEvent: SensorEvent?) {
                val degree: Float = sensorEvent?.values?.get(0)?.roundToInt()?.toFloat()!!
                var head: Float = sensorEvent.values?.get(0)?.roundToInt()?.toFloat()!!

                val destLocation = Location("Destination Location")
                destLocation.latitude = HomeFragment.QIBLA_LATITUDE
                destLocation.longitude = HomeFragment.QIBLA_LONGITUDE

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
                    HomeFragment.TAG,
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

            }
        }, sensor, SensorManager.SENSOR_DELAY_GAME)
    }
}