package com.zaker.ui.sidemenu

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.hardware.*
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.zaker.databinding.ActivityQiblahBinding
import com.zaker.ui.fragments.home.HomeFragment
import com.zaker.ui.fragments.home.HomeViewModel
import kotlin.math.roundToInt
import android.hardware.SensorManager
import android.hardware.SensorEventListener
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.zaker.R


class QiblahActivity : AppCompatActivity() , SensorEventListener{
    companion object {
        const val MARSHMALLOW = 23
        const val ACCESS_FINE_LOCATION_REQ_CODE = 35
    }

    lateinit var binding: ActivityQiblahBinding
    private val qiblahViewModel: HomeViewModel by viewModels()

    var currentDegree: Float = 0f
    var currentNeedleDegree: Float = 0f

    var sensorManager : SensorManager? = null
    var sensor: Sensor? = null
    var userLocation: Location? = null
    var qiblahDirectionImageView: ImageView? = null
    var needleAnimation: RotateAnimation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qiblah)
        binding.homeViewmodel = qiblahViewModel
        mLocationRequest = LocationRequest.create()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        qiblahDirectionImageView = findViewById<ImageView>(R.id.qiblahDirectionImageView)
        needleAnimation = RotateAnimation(
            currentNeedleDegree,
            0f,
            Animation.RELATIVE_TO_SELF,
            .5f,
            Animation.RELATIVE_TO_SELF,
            .5f
        )
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ORIENTATION)
        userLocation = Location("User Location")

        initLocationPermissions()
    }
    var firstTime = false
    override fun onResume() {
        super.onResume()
        if(firstTime) {
            enableGPS()
            firstTime = true
        }

        sensorManager?.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

    }

    override fun onStop() {
        super.onStop()
        sensorManager?.unregisterListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_FINE_LOCATION_REQ_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                initLocationListener()
            }
        }
    }
    private var mLocationPermissionGranted = false

    private fun initLocationPermissions() {
        if (Build.VERSION.SDK_INT >= MARSHMALLOW) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionGranted = true
                enableGPS()
            } else {
                locationPermissionDeined()
            }
        } else {
            locationPermissionDeined()
        }
    }
    private fun locationPermissionDeined() {
        mLocationPermissionGranted = false
        openAppSettings()
    }
    private fun openAppSettings() {
        android.app.AlertDialog.Builder(this)
            .setTitle(R.string.app_location_permission)
            .setMessage(R.string.location_permission_msg)
            .setPositiveButton(android.R.string.ok,
                DialogInterface.OnClickListener { dialog, which ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                })
            .setIcon(R.drawable.common_google_signin_btn_icon_dark)
            .setCancelable(false)
            .show()
    }
    private val fusedLocationProviderClient: FusedLocationProviderClient? = null

    fun openGPS(){
        mLocationRequest?.let {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                initLocationListener()
                return
            }
            if (fusedLocationProviderClient != null) {
                mLocationRequest?.let {
                    fusedLocationProviderClient.requestLocationUpdates(
                        it,
                        mLocationCallback,
                        Looper.myLooper()!!
                    )
                }
            } // permission already checked before starting service
            mLocationPermissionGranted = true
        }
        enableGPS()
    }
    private var currentLocation: Location? = null

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            if (currentLocation != null) {
                currentLocation = location
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun initLocationListener() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener {
            initQiblaDirection(it.latitude, it.longitude)
        }
        fusedLocationClient.lastLocation.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.location_permission_title))
                .setMessage(getString(R.string.location_permission_message))
                .setPositiveButton("Grant") { dialog, _ ->
                    dialog.dismiss()
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        ACCESS_FINE_LOCATION_REQ_CODE
                    )
                }
                .setNegativeButton("Deny") { dialog, _ ->
                    dialog.dismiss()
                    handlePermissionDenied()
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_FINE_LOCATION_REQ_CODE
            )
        }
    }

    private fun handlePermissionDenied() {

    }

    private fun initQiblaDirection(latitude: Double, longitude: Double) {
        userLocation = Location("User Location")

        userLocation?.latitude = latitude
        userLocation?.longitude = longitude
        sensorManager?.registerListener(
            this,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
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

        binding.tvQiblahDegrees.text = "Heading : $degree + degrees"

        binding.tvQiblahDegrees.text = currentNeedleDegree.toString()

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

        binding.qiblahDirectionImageView.startAnimation(needleAnimation)
        if (direction != null) {
            currentNeedleDegree = direction
        }
        currentDegree = -degree

        if (currentNeedleDegree <= 10 || currentNeedleDegree >= 350) {
            binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.logoOrangeColor))
            binding.tvQiblahDegrees.setTextColor(getColor(R.color.logoOrangeColor))
        } else {

            binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.backgroundGreen))

            binding.tvQiblahDegrees.setTextColor(getColor(R.color.textColorQiblahDegrees))

        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
    private var mLocationRequest: LocationRequest? = null
    private val REQUEST_CHECK_SETTINGS = 1

    private fun enableGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            this
        ) {
            openGPS()

        }
        task.addOnFailureListener(
            this
        ) { e: Exception? ->
            if (e is ResolvableApiException) {
                try {

                    e.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Toast.makeText(this, sendEx.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CHECK_SETTINGS -> when (resultCode) {
                RESULT_OK -> {
                }
                RESULT_CANCELED -> {
                }
            }
        }
    }

}