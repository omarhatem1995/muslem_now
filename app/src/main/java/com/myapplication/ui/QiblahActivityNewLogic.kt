package com.myapplication.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.myapplication.R
import com.myapplication.databinding.ActivityQiblahBinding
import com.myapplication.ui.Compass.CompassListener
import com.myapplication.ui.fragments.home.HomeViewModel

class QiblahActivityNewLogic : AppCompatActivity() {
    lateinit var binding: ActivityQiblahBinding
    private val qiblahViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_qiblah)
        binding.homeViewmodel = qiblahViewModel
        setupCompass()
    }

    private lateinit var compass : Compass
    override fun onStart() {
        super.onStart()
        Log.d("asdlaskdasd", "start compass")
        compass.start(this)
    }
    override fun onPause() {
        super.onPause()
        compass.stop()
    }
    private fun setupCompass() {

        compass = Compass(this)
        fetch_GPS()
        val cl: CompassListener = object : CompassListener {
            override fun onNewAzimuth(azimuth: Float) {
                adjustGambarDial(azimuth)
                adjustArrowQiblat(azimuth)
            }
        }
        compass.setListener(cl)
    }
    fun adjustGambarDial(azimuth: Float) {
        val an: Animation = RotateAnimation(
            -currentAzimuth,
            -azimuth,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        binding.qiblahDirectionImageView.startAnimation(an)
    }
    private var currentAzimuth = 0f

    fun adjustArrowQiblat(azimuth: Float) {
        val an: Animation = RotateAnimation(
            -currentAzimuth,
            -azimuth,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        currentAzimuth = azimuth
        an.duration = 500
        an.repeatCount = 0
        an.fillAfter = true
        binding.qiblahDirectionImageView.startAnimation(an)
//        if (QiblaDegree > 0) {
//            binding.qiblahDirectionImageView.setVisibility(View.VISIBLE)
//        } else {
            binding.qiblahDirectionImageView.setVisibility(View.INVISIBLE)
            binding.qiblahDirectionImageView.setVisibility(View.GONE)
//        }
    }
    var gps: GPSTracker? = null

    fun fetch_GPS() {
        var result = 0.0
        gps = GPSTracker(this)
        if (gps!!.canGetLocation()) {
            val lat_saya: Double = gps!!.getLatitude()
            val lon_saya: Double = gps!!.getLongitude()
            if (lat_saya < 0.001 && lon_saya < 0.001) {
                binding.qiblahDirectionImageView.setVisibility(View.INVISIBLE)
                binding.qiblahDirectionImageView.setVisibility(View.GONE)
//                text_up.setText("")
//                text_down.setText(resources.getString(R.string.locationunready))
            } else {
                val longitude2 =
                    39.826209 // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                val latitude2 =
                    Math.toRadians(21.422507) // Kaabah Position https://www.latlong.net/place/kaaba-mecca-saudi-arabia-12639.html
                val latitude1 = Math.toRadians(lat_saya)
                val longDiff = Math.toRadians(longitude2 - lon_saya)
                val y = Math.sin(longDiff) * Math.cos(latitude2)
                val x = Math.cos(latitude1) * Math.sin(latitude2) - Math.sin(latitude1) * Math.cos(
                    latitude2
                ) * Math.cos(longDiff)
                result = (Math.toDegrees(Math.atan2(y, x)) + 360) % 360
                val result2 = result.toFloat()
                binding.qiblahDegree.setText(
                     " " + result2 + " "
                    )
                Log.d("lasdksaldksaldkasde2", " $result  , $result2")

                binding.qiblahDirectionImageView.setVisibility(View.VISIBLE)
            }
        } else {
            binding.qiblahDirectionImageView.setVisibility(View.INVISIBLE)
            binding.qiblahDirectionImageView.setVisibility(View.GONE)
            binding.qiblaTextView.setText("")
//            binding.qiblaTextView.setText(resources.getString(R.string.gpsplz))
        }
    }

}