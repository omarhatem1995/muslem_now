package com.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.myapplication.data.core.workmanager.MuslemApp
import com.myapplication.ui.MainViewModel
import com.myapplication.ui.azkar.AzkarFragment
import com.myapplication.ui.fragments.home.HomeFragment
import com.myapplication.ui.fragments.quran.QuranFragment
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch
@DelicateCoroutinesApi
@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    lateinit var bottomNavigationView: BottomNavigationView
    var firstFragment = HomeFragment()
    var quranFragment = QuranFragment()
    var azkarFragment = AzkarFragment()

    @InternalCoroutinesApi
    @DelicateCoroutinesApi
    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(
            this,
            MainViewModel.Factory(this.application as MuslemApp)
        )[MainViewModel::class.java]

    }




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceFragment(firstFragment)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        mLocationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval((1000 * 2).toLong())
            .setFastestInterval((1000 * 1).toLong())
        bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> replaceFragment(firstFragment)
                R.id.quran -> replaceFragment(quranFragment)
                R.id.azkar -> replaceFragment(azkarFragment)
            }
            true
        }


        lifecycleScope.launch {
            if (!viewModel.getAlarmState())
            {
                viewModel.setSalahAlarm()
            }

        }
//viewModel.workState.observe(this) {
//
//    if (it != null) {
//        if (it.isNotEmpty()) {
//            if (it.last().state == WorkInfo.State.FAILED || it.last().state == WorkInfo.State.BLOCKED) {
//                lifecycleScope.launch {
//                    viewModel.setSalahAlarm()
//                }
//            }
//        }
//        }else{
//        lifecycleScope.launch {
//            viewModel.setSalahAlarm()
//        }
//    }
//
//
//
//}

    }


    private fun replaceFragment(fragment: Fragment) {
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.flFragment, fragment)
            transaction.commit()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkLocationPermissionCall()) {
            getLocationPermission()
            Log.d("checkLocationPermission", "d " + checkLocationPermissionCall())
        }else {
            enableGPS()
        }
    }

    private val REQUEST_CHECK_SETTINGS = 1
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private var mLocationPermissionGranted = false
    private val LOCATION_PERMISSION_REQUEST_CODE = 1234

    private fun getLocationPermission() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) { //handling location permission 10
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            if (checkLocationPermissionCall()) {
                mLocationPermissionGranted = true
                ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) { //handling location permission 11 and higher
            if (checkLocationPermissionCall()) {
                mLocationPermissionGranted = true
                locationPermissionGranted()
            } else locationPermissionDeined()
        } else {
            val permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (ContextCompat.checkSelfPermission(
                        this.applicationContext,
                        COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    mLocationPermissionGranted = true
                    ActivityCompat.requestPermissions(
                        this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE
                    )
                } else ActivityCompat.requestPermissions(
                    this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else ActivityCompat.requestPermissions(
                this,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    if (checkLocationPermissionResult(grantResults)) {
                        locationPermissionGranted()
                    } else locationPermissionDeined()
                } else {
                    if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            if (ContextCompat.checkSelfPermission(
                                    this.applicationContext,
                                    COARSE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                            ) {
                                mLocationRequest?.let {
                                    fusedLocationProviderClient!!.requestLocationUpdates(
                                        it,
                                        mLocationCallback,
                                        Looper.myLooper()!!
                                    )
                                } // permission already checked before starting service
                                mLocationPermissionGranted = true
                                enableGPS()
                            } else locationPermissionDeined()
                        } else locationPermissionDeined()
                    } else locationPermissionDeined()
                }
                return
            }
        }
    }

    private fun checkLocationPermissionResult(grantResults: IntArray): Boolean {
        return if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                ContextCompat.checkSelfPermission(
                        this.applicationContext,
                        COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            } else false
        } else false
    }

    private fun checkLocationPermissionCall(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("checkLocationPermission", " 1 " + true)
            ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
    }

    private val fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null

    @SuppressLint("MissingPermission")
    private fun locationPermissionGranted() {
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
        enableGPS()

    }

    private var currentLocation: Location? = null
    private var lastKnownLocation: Location? = null

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            if (location != null && currentLocation != null) {
                currentLocation = location
            }
        }
    }
    fun convertLatLongToLocation(latLng: LatLng): Location? {
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        return location
    }

    fun locationPermissionDeined() {
        mLocationPermissionGranted = false
        openAppSettings()
    }

    fun openAppSettings() {
        AlertDialog.Builder(this)
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
    private fun checkGPSEnabled(): Boolean{
        val lm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setMessage(R.string.gps_network_not_enabled)
                .setPositiveButton(R.string.open_location_settings,
                    DialogInterface.OnClickListener { paramDialogInterface, paramInt ->
                        this.startActivity(
                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        )
                    })               .setNegativeButton(R.string.Cancel, null)
                .show()
            return false
        }
        return true
    }

    protected fun enableGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            this
        ) { locationSettingsResponse: LocationSettingsResponse? -> }
        task.addOnFailureListener(
            this
        ) { e: Exception? ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(
                        this,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: SendIntentException) {
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