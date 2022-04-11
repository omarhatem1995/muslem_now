package com.myapplication.ui.sidemenu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.myapplication.R
import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse
import com.myapplication.databinding.ActivityNearByMosquesBinding
import com.myapplication.domain.usecases.ui.GooglePlacesUseCases
import com.myapplication.ui.ViewUtils
import com.myapplication.ui.adapter.MosquesAdapter
import com.myapplication.ui.entities.GooglePlacesViewState
import com.myapplication.ui.fragments.home.HomeViewModel

class NearByMosquesActivity : AppCompatActivity() , GooglePlacesUseCases.View {

    lateinit var binding: ActivityNearByMosquesBinding
    private val nearByViewModel: HomeViewModel by viewModels()

    lateinit var progressDialog: Dialog

    private val fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    var fusedLocationClient: FusedLocationProviderClient? = null

    private var mLocationPermissionGranted = false
    private var mLocationRequest: LocationRequest? = null
    private val REQUEST_CHECK_SETTINGS = 1

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_near_by_mosques)
        binding.nearbyMosquesViewModel = nearByViewModel

        mLocationRequest = LocationRequest.create()
        progressDialog = Dialog(this)
        if(nearByViewModel.preference.getLanguage().equals("ar")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
        }else if (nearByViewModel.preference.getLanguage().equals("en")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.backArrowImageView.setOnClickListener { finish() }
        binding.refreshNearByButton.setOnClickListener { initLocationPermissions() }
        initLocationPermissions()

    }
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            currentLocation = Location("yourLocation")
                currentLocation = location
                initLocationListener()

        }
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
        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener {

            val flag = it.locationSettingsStates?.isLocationUsable
            if (flag == true) {
                locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
//                        val location = locationResult.lastLocation
                        initLocationListener()

                    }
                }
                fusedLocationClient?.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } else {
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
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }


    }
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
                    } else {
                        fusedLocationProviderClient?.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.getMainLooper()
                        )
                    }
                }
            }
        }

    }

    private fun initLocationPermissions() {
        if (Build.VERSION.SDK_INT >= QiblahActivity.MARSHMALLOW) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissionGranted = true
                enableGPS()
                getLocationUpdates()
            } else {
                locationPermissionDeined()
                binding.refreshNearByButton.visibility = View.VISIBLE
            }
        } else {
            locationPermissionDeined()
            binding.refreshNearByButton.visibility = View.VISIBLE
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

    private fun enableGPS() {
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest!!)
        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())
        task.addOnSuccessListener(
            this
        ) {
            initLocationListener()
        }
        task.addOnFailureListener(
            this
        ) { e: Exception? ->
            binding.refreshNearByButton.visibility = View.VISIBLE
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
                return
            }
            if (fusedLocationProviderClient != null) {
                mLocationRequest?.let {
                    fusedLocationProviderClient.requestLocationUpdates(
                        it,
                        mLocationCallback,
                        Looper.myLooper()!!
                    )
//                    initLocationListener()

                }
            } // permission already checked before starting service
            mLocationPermissionGranted = true
        }
        enableGPS()
    }

    @SuppressLint("MissingPermission")
    private fun initLocationListener() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.lastLocation.addOnSuccessListener {
            getMosquesFromGoogleAPI(it?.latitude.toString(),it?.longitude.toString())
        }
        fusedLocationClient!!.lastLocation.addOnFailureListener {
            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMosquesFromGoogleAPI(latitude:String?,longitude:String?) {
        nearByViewModel.getGooglePlacesAPI("$latitude,$longitude")
        nearByViewModel.viewStateGooglePlaces.observe(this
        ) { viewState ->
            when (viewState) {
                is GooglePlacesViewState.Data -> {
                    renderMosques(viewState.data)
                    renderLoading(false)
                }
                is GooglePlacesViewState.Loading -> renderLoading(viewState.show)
                is GooglePlacesViewState.NetworkFailure -> renderNetworkFailure()
            }
        }
    }

    override fun renderMosques(data: GooglePlacesResponse) {
        binding.recyclerViewNearbyMosques.adapter =
            data.resultGooglePlaces?.let { MosquesAdapter(this, it) }
        binding.refreshNearByButton.visibility = View.GONE
    }

    override fun renderLoading(show: Boolean) {
        binding.isLoading = show
    }

    override fun renderNetworkFailure() {
        binding.refreshNearByButton.visibility = View.VISIBLE
    }
}