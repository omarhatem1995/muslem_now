package com.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import com.google.android.gms.location.LocationServices
import com.myapplication.databinding.ActivityQiblahBinding
import com.myapplication.ui.fragments.home.HomeFragment
import com.myapplication.ui.fragments.home.HomeViewModel
import kotlin.math.roundToInt
import android.hardware.SensorManager
import android.hardware.SensorEventListener





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

    override fun onResume() {
        super.onResume()
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
                initLocationListener()
            }
        }
    }

    private fun initLocationPermissions() {
        if (Build.VERSION.SDK_INT >= MARSHMALLOW) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                initLocationListener()
            } else {
                requestPermission()
            }
        } else {
            initLocationListener()
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


}