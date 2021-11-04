package com.myapplication

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
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
import android.widget.ImageView
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
    lateinit var ivQiblaDirection: ImageView
    lateinit var tvHeading: TextView
    lateinit var needleAnimation: RotateAnimation
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var recyclerViewPrayer: RecyclerView
    lateinit var prayerAdapter: PrayerAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var currentTime: TextView
    lateinit var localTime: String

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
        ivQiblaDirection = view.findViewById<ImageView>(R.id.ivQiblaDirection)
        recyclerViewPrayer = view.findViewById<RecyclerView>(R.id.prayer_times_list)
        currentTime = view.findViewById<TextView>(R.id.current_time)

        needleAnimation = RotateAnimation(
            currentNeedleDegree,
            0f,
            Animation.RELATIVE_TO_SELF,
            .5f,
            Animation.RELATIVE_TO_SELF,
            .5f
        )

        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
        val currentLocalTime = cal.time
        val date: DateFormat = SimpleDateFormat("HH:mm a")
        date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

        localTime = date.format(currentLocalTime)

        Log.d("c : is", localTime)

        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        binding.homeViewmodel = homeViewModel
        binding.lifecycleOwner = this

        vm.getAlAdahanAPI(
            "51.508515", "-51.508515", "2",
            "10", "2021"
        )

        vm.viewStateAlAdahan.observe(viewLifecycleOwner, { viewState ->
            when (viewState) {
                is AlAdahanViewState.Loading -> {
                    renderLoading(viewState.show)
                }
                is AlAdahanViewState.NetworkFailure -> {
                    renderNetworkFailure()
                }
                is AlAdahanViewState.Data -> {
                    Log.d("dataViewState", "is called in categories: " + viewState.data?.get(0))
                    Log.d("dataViewState", "is called in categories: " + viewState.data?.get(0))
                    renderParentTimings(viewState.data)
                }
            }
        })
        currentTime.text = localTime

        initLocationListener()
        return view
    }

    override fun renderParentTimings(data: List<Datum>) {
        val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
        val currentLocalTime = cal.time
        val dateFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy")
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1:00"))
        val localTime = dateFormat.format(currentLocalTime)
        linearLayoutManager = LinearLayoutManager(requireContext())

        for (i in 0..data.size - 1) {

            var date = data.get(i).date.gregorian.date

            var fajrAdahanModel = PrayerTimeModel(
                0, R.drawable.ic_elfajr,
                "Fajr", data.get(i).timings.fajr, R.drawable.ic_volume_high, date
            )

            var sunRiseAdahanModel = PrayerTimeModel(
                1, R.drawable.ic_elfajr,
                "Sunrise", data.get(i).timings.sunrise, R.drawable.ic_volume_high, date
            )

            var dhurAdahanModel = PrayerTimeModel(
                2, R.drawable.ic_elfajr,
                "Dhur", data.get(i).timings.dhuhr, R.drawable.ic_volume_high, date
            )

            var asrAdahanModel = PrayerTimeModel(
                3, R.drawable.ic_elfajr,
                "Asr", data.get(i).timings.asr, R.drawable.ic_volume_high, date
            )

            var sunSetAdahanModel = PrayerTimeModel(
                4, R.drawable.ic_elfajr,
                "SunSet", data.get(i).timings.sunset, R.drawable.ic_volume_high, date
            )

            var maghribAdahanModel = PrayerTimeModel(
                5, R.drawable.ic_elfajr,
                "Maghrib", data.get(i).timings.maghrib, R.drawable.ic_volume_high, date
            )

            var ishaAdahanModel = PrayerTimeModel(
                6, R.drawable.ic_elfajr,
                "Isha", data.get(i).timings.dhuhr, R.drawable.ic_volume_high, date
            )

            var imsakAdahanModel = PrayerTimeModel(
                7, R.drawable.ic_elfajr,
                "Imsak", data.get(i).timings.imsak, R.drawable.ic_volume_high, date
            )

            var midnightAdahanModel = PrayerTimeModel(
                8, R.drawable.ic_elfajr,
                "Midnight", data.get(i).timings.midnight, R.drawable.ic_volume_high, date
            )

            val supplierNames1: MutableList<PrayerTimeModel> = ArrayList()
            supplierNames1.add(0, fajrAdahanModel)
            supplierNames1.add(1, sunRiseAdahanModel)
            supplierNames1.add(2, dhurAdahanModel)
            supplierNames1.add(3, asrAdahanModel)
            supplierNames1.add(4, sunSetAdahanModel)
            supplierNames1.add(5, maghribAdahanModel)
            supplierNames1.add(6, ishaAdahanModel)
            supplierNames1.add(7, imsakAdahanModel)
            supplierNames1.add(8, midnightAdahanModel)

            vm.savePrayerTimes(requireContext(), supplierNames1)
            Log.d("savePrayer", supplierNames1.toString())
//            if (localTime.equals(date)) {
                recyclerViewPrayer.adapter = PrayerAdapter(requireContext(), supplierNames1)
                recyclerViewPrayer.isNestedScrollingEnabled = false
                recyclerViewPrayer.layoutManager = linearLayoutManager
//            }
        }

        /*   vm.getPrayerTimes(requireContext()).observe(requireActivity(), androidx.lifecycle.Observer {
               prayer -> Log.d("uuuiasd", "sdsd" + prayer)
           })*/

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
                initQiblaDirection(it.latitude, it.longitude)
            }
        }
        fusedLocationClient?.lastLocation?.addOnFailureListener {
            Toast.makeText(requireContext(), "Err: " + it.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }

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

    private fun initQiblaDirection(latitude: Double, longitude: Double) {
        userLocation = Location("User Location")
        userLocation.latitude = latitude
        userLocation.longitude = longitude

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

                ivQiblaDirection.startAnimation(needleAnimation)

                currentNeedleDegree = direction
                currentDegree = -degree

            }
        }, sensor, SensorManager.SENSOR_DELAY_GAME)
    }
}