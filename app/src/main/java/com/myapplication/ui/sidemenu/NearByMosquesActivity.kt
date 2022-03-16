package com.myapplication.ui.sidemenu

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.LocationServices
import com.myapplication.R
import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse
import com.myapplication.databinding.ActivityNearByMosquesBinding
import com.myapplication.domain.usecases.ui.GooglePlacesUseCases
import com.myapplication.ui.adapter.MosquesAdapter
import com.myapplication.ui.entities.GooglePlacesViewState
import com.myapplication.ui.fragments.home.HomeViewModel

class NearByMosquesActivity : AppCompatActivity() , GooglePlacesUseCases.View {
    lateinit var binding: ActivityNearByMosquesBinding
    private val nearByViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_near_by_mosques)
        binding.nearbyMosquesViewModel = nearByViewModel

        if(nearByViewModel.preference.getLanguage().equals("ar")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
        }else if (nearByViewModel.preference.getLanguage().equals("en")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.backArrowImageView.setOnClickListener { finish() }

        initLocationPermissions()

    }
    private fun initLocationPermissions() {
        if (Build.VERSION.SDK_INT >= QiblahActivity.MARSHMALLOW) {
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
//            initQiblaDirection(it.latitude, it.longitude)
            nearByViewModel.getGooglePlacesAPI("${it.latitude},${it.longitude}")
            nearByViewModel.viewStateGooglePlaces.observe(this,
            {
                    viewState -> when(viewState){
                        is GooglePlacesViewState.Data -> renderMosques(viewState.data)
                        is GooglePlacesViewState.Loading -> renderLoading(false)
                        is GooglePlacesViewState.NetworkFailure -> renderNetworkFailure()
                    }
            })
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
                        QiblahActivity.ACCESS_FINE_LOCATION_REQ_CODE
                    )
                }
                .setNegativeButton("Deny") { dialog, _ ->
                    dialog.dismiss()
                }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                QiblahActivity.ACCESS_FINE_LOCATION_REQ_CODE
            )
        }
    }

    override fun renderMosques(data: GooglePlacesResponse) {
        binding.recyclerViewNearbyMosques.adapter =
            data.resultGooglePlaces?.let { MosquesAdapter(this, it) }
    }

    override fun renderLoading(show: Boolean) {

    }

    override fun renderNetworkFailure() {

    }

//    fun initAPI(){
//
//        // Initialize the SDK
//        Places.initialize(applicationContext, "AIzaSyB_mbG3vnY5ABJA4Ay9L_yKbtFy5D5gltg")
//
//        // Create a new PlacesClient instance
//        val placesClient = Places.createClient(this)
//
//        val type = TYPE_MOSQUE
//
//        // Use fields to define the data types to return.
//        val placeFields: List<Place.Field> = listOf(Place.Field.NAME,Place.Field.TYPES)
//// Use the builder to create a FindCurrentPlaceRequest.
//        val request: FindCurrentPlaceRequest = FindCurrentPlaceRequest.newInstance(placeFields)
//// Call findCurrentPlace and handle the response (first check that the user has granted permission).
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
//            PackageManager.PERMISSION_GRANTED) {
//
//            val placeResponse = placesClient.findCurrentPlace(request)
//            placeResponse.addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val response = task.result
//                    for (placeLikelihood: PlaceLikelihood in response?.placeLikelihoods ?: emptyList()) {
//                        Log.i(
//                            TAG,
//                            "Place '${placeLikelihood.place.types?.get(0).toString()}' has likelihood: ${placeLikelihood.likelihood}"
//                        )
//                        if(placeLikelihood.place.types?.toString()?.contains("MOSQUE") == true)
//                            Log.i(
//                                TAG,
//                                "FOUND Place '${placeLikelihood.place.name}' has likelihood: ${placeLikelihood.likelihood}"
//                            )
//                    }
//                } else {
//                    val exception = task.exception
//                    if (exception is ApiException) {
//                        Log.e(TAG, "Place not found: ${exception.statusCode}")
//                    }
//                }
//            }
//        } else {
//            // A local method to request required permissions;
//            // See https://developer.android.com/training/permissions/requesting
////            getLocationPermission()
//        }
//
//
//    }
 /*   fun getNearByMosques(){
//        AIzaSyB_mbG3vnY5ABJA4Ay9L_yKbtFy5D5gltg

    }*/
}