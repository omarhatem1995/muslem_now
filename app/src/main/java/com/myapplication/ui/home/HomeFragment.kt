package com.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.os.Build
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
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.myapplication.databinding.FragmentHomeBinding
import com.myapplication.ui.home.HomeViewModel
import kotlin.math.roundToInt
import android.content.Intent
import android.provider.Settings

import android.content.DialogInterface

import android.location.LocationManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.myapplication.data.entities.model.Datum
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.entities.model.Timings
import com.myapplication.domain.usecases.ui.AlAdahanUseCases
import com.myapplication.ui.entities.AlAdahanViewState
import com.myapplication.ui.home.PrayerAdapter
import java.text.DateFormat
import java.text.SimpleDateFormat

import java.util.*
import kotlin.collections.ArrayList


class HomeFragment : Fragment() , AlAdahanUseCases.View {

    lateinit var binding: FragmentHomeBinding
    private lateinit var vm: HomeViewModel

    companion object {
        const val TAG = "MainActivity"
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
    lateinit var ivQiblaDirection : ImageView
    lateinit var tvHeading : TextView
    lateinit var needleAnimation: RotateAnimation
    lateinit var fusedLocationClient : FusedLocationProviderClient
    lateinit var recyclerViewPrayer : RecyclerView
    lateinit var prayerAdapter : PrayerAdapter
    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var currentTime : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vm = activity?.run{
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

        var view : View = inflater.inflate(R.layout.fragment_home, container, false)
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

        val localTime = date.format(currentLocalTime)

        val homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)

        binding.homeViewmodel = homeViewModel
        binding.lifecycleOwner = this

        vm.getAlAdahanAPI("51.508515","-51.508515","2",
            "4","2021")

        vm.viewStateAlAdahan.observe(viewLifecycleOwner,{
                viewState -> when (viewState){
            is AlAdahanViewState.Loading ->{
                renderLoading(viewState.show)
            }
            is AlAdahanViewState.NetworkFailure -> {
                renderNetworkFailure()
            }
            is AlAdahanViewState.Data -> {
                Log.d("dataViewState", "is called in categories: "+viewState.data?.get(0))
                renderParentTimings(viewState.data)
            }
        }
        })
        currentTime.text = localTime

//        val apiInterface = AlAdahanGateway.provideGateWay().getAladahan("51.508515","-51.508515","2",
//            "4","2021")

//        aladahanGateway.getAladahan("51.508515","-51.508515","2",
//        "4","2021")


        initLocationPermissions()
        return view
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == HomeFragment.ACCESS_FINE_LOCATION_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocationListener()
            }
        }
    }

    private fun initLocationPermissions() {
        if (Build.VERSION.SDK_INT >= HomeFragment.MARSHMALLOW) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if(checkGPSEnabled()) {
                    initLocationListener()
                    Log.d("firstPermission", " is called")
                }
            } else {
                requestPermission()
                Log.d("firstPermission" , " is ss called")
            }
        } else {
            initLocationListener()
            Log.d("firstPermission" , " is ee called")
        }
    }

    private fun checkGPSEnabled(): Boolean{
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps_enabled = false
        var network_enabled = false

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: java.lang.Exception) {
        }

        if (!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder(requireContext())
                .setMessage(R.string.gps_network_not_enabled)
                .setPositiveButton(R.string.open_location_settings,
                    DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                        requireContext().startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    })
                .setNegativeButton(R.string.Cancel, null)
                .show()
            return false
        }
        return true
    }

    override fun onResume() {
        super.onResume()
        if(checkGPSEnabled()){
            initLocationListener()
        }
    }
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private fun getLocationUpdates()
    {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        locationRequest = LocationRequest()
        locationRequest.interval = 50000
        locationRequest.fastestInterval = 50000
        locationRequest.smallestDisplacement = 170f // 170 m = 0.1 mile
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY //set according to your app function
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

    private fun initLocationListener() {
        val fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
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
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }

    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.location_permission_title))
                .setMessage(getString(R.string.location_permission_message))
                .setPositiveButton("Grant") { dialog, _ ->
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(
                        requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        HomeFragment.ACCESS_FINE_LOCATION_REQ_CODE
                    )
                }
                .setNegativeButton("Deny") { dialog, _ ->
                    dialog.dismiss()
                    handlePermissionDenied()
                }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                HomeFragment.ACCESS_FINE_LOCATION_REQ_CODE
            )
        }
    }

    private fun handlePermissionDenied() {

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

                Log.d(HomeFragment.TAG, "Needle Degree : $currentNeedleDegree, Direction : $direction")

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

    override fun renderParentTimings(data: List<Datum>) {
        Log.d("renderData" , "data" + data.get(0).timings)
        linearLayoutManager  = LinearLayoutManager(requireContext())
        Log.d("languageList" , "is called")

        var fajr = data.get(0).timings.fajr
        var fajrAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Fajr" , fajr , R.drawable.ic_volume_high)
        fajrAdahanModel.name = "Fajr"

        var sunRise = data.get(1).timings.sunrise
        var sunRiseAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Sunrise" , sunRise , R.drawable.ic_volume_high)
        sunRiseAdahanModel.name = "Sunrise"

        var duhr = data.get(2).timings.dhuhr
        var dhurAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Dhur" , duhr , R.drawable.ic_volume_high)
        dhurAdahanModel.name = "Dhur"

        var asr = data.get(3).timings.dhuhr
        var asrAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Asr" , asr , R.drawable.ic_volume_high)
        asrAdahanModel.name = "Asr"

        var sunSet = data.get(4).timings.asr
        var sunSetAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Asr" , sunSet , R.drawable.ic_volume_high)
        sunSetAdahanModel.name = "Sunset"

        var maghrib = data.get(5).timings.sunset
        var maghribAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Maghrib" , maghrib , R.drawable.ic_volume_high)
        maghribAdahanModel.name = "maghrib"

        var isha = data.get(6).timings.dhuhr
        var ishaAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
            "Isha" , isha , R.drawable.ic_volume_high)
        ishaAdahanModel.name = "Isha"

        var imsak = data.get(7).timings.maghrib
        var imsakAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Imsak" , imsak , R.drawable.ic_volume_high)
        imsakAdahanModel.name = "imsak"

        var midnight = data.get(8).timings.dhuhr
        var midnightAdahanModel = PrayerTimeModel(R.drawable.ic_elfajr,
        "Midnight" , midnight , R.drawable.ic_volume_high)
        midnightAdahanModel.name = "midnight"

        val supplierNames1: MutableList<PrayerTimeModel> = ArrayList()
        supplierNames1.add(0,fajrAdahanModel)
        supplierNames1.add(1,sunRiseAdahanModel)
        supplierNames1.add(2,dhurAdahanModel)
        supplierNames1.add(3,asrAdahanModel)
        supplierNames1.add(4,sunSetAdahanModel)
        supplierNames1.add(5,maghribAdahanModel)
        supplierNames1.add(6,ishaAdahanModel)
        supplierNames1.add(7,imsakAdahanModel)
        supplierNames1.add(8,midnightAdahanModel)

        recyclerViewPrayer.adapter = PrayerAdapter(requireContext(),supplierNames1)
        recyclerViewPrayer.isNestedScrollingEnabled = false
        recyclerViewPrayer.layoutManager =linearLayoutManager
    }

    override fun renderLoading(show: Boolean) {
        Log.d("renderData" , "data" + show)
    }

    override fun renderNetworkFailure() {
        Log.d("renderData" , "data" )
    }

}