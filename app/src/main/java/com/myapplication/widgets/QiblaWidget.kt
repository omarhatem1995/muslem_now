package com.myapplication.widgets

import android.Manifest
import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.location.Location
import android.widget.RemoteViews
import android.app.PendingIntent

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationServices
import com.myapplication.R
import kotlin.math.roundToInt
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationCallback


/**
 * Implementation of App Widget functionality.
 */
open class QiblaWidget : AppWidgetProvider() {
    companion object {
        const val QIBLA_LATITUDE = 21.3891
        const val QIBLA_LONGITUDE = 39.8579
    }
    var widgetCurrentId = 0
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent!!.action != null) {
            val extras = intent!!.extras
            if (extras != null) {
                super.onReceive(context, intent)
                val widgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID,
                )
                val intent = Intent(context, QiblaWidget::class.java)
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                val sharedPrefFile = "kotlinsharedpreference"

                val sharedPreferences: SharedPreferences? = context?.getSharedPreferences(sharedPrefFile,
                    Context.MODE_PRIVATE)
                val views = RemoteViews(context?.packageName, R.layout.qibla_widget)
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)

                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            return
                        }
                        if (!fusedLocationClient.lastLocation.equals(null)) {
                            fusedLocationClient.lastLocation.addOnSuccessListener {
                            }
                            fusedLocationClient.lastLocation.addOnFailureListener {
                                Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT)
                                    .show()
                            }

                        } else {

                        }
                    }
                }

                val locationCallback2 = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        super.onLocationResult(locationResult)

                        if (ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {

                            return
                        }
                        fusedLocationClient.lastLocation.addOnSuccessListener {
                            val editor: SharedPreferences.Editor? = sharedPreferences?.edit()
                            editor?.putString("latitude", it.latitude.toString())
                            editor?.putString("longitude", it.longitude.toString())
                            editor?.apply()
                            editor?.commit()
                        }
                        fusedLocationClient.lastLocation.addOnFailureListener {
                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                val latitude = sharedPreferences?.getString("latitude", "30.007702")
                val longitude = sharedPreferences?.getString("longitude", "30.007702")

                val stringFormat = String.format(
                    "%.2f", initQiblaDirection(
                        context,
                        latitude.toString().toDouble(), longitude.toString().toDouble()
                    )
                )
                views.setTextViewText(
                    R.id.tvQiblahDegreesWidget,
                    stringFormat
                )
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_qibla)
                views.setImageViewBitmap(
                    R.id.qiblahDirectionImageView, bitmap.rotated(
                        initQiblaDirection(
                            context, latitude.toString().toDouble(),
                            longitude.toString().toDouble()
                        )
                    )
                )
                if (latitude.equals("30.007702") && longitude.equals("30.007702"))
                    Toast.makeText(
                        context,
                        context.getString(R.string.please_open_your_gps),
                        Toast.LENGTH_SHORT
                    ).show()
                else
                    Toast.makeText(
                        context,
                        context.getString(R.string.qibla_refreshed),
                        Toast.LENGTH_SHORT
                    ).show()
                // do something for the widget that has appWidgetId = widgetId
                val appWidgetManager = AppWidgetManager.getInstance(context)
                appWidgetManager.updateAppWidget(widgetId, views)


            }

        } else {
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (i in 0 until appWidgetIds.size) {
            val sharedPrefFile = "kotlinsharedpreference"

            val sharedPreferences: SharedPreferences = context
                .getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)

            val currentWidgetId = appWidgetIds[i]
            widgetCurrentId = currentWidgetId
            val views = RemoteViews(context.packageName, R.layout.qibla_widget)

            val intentUpdate = Intent(Intent.ACTION_VIEW)
            intentUpdate.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val idArray = intArrayOf(currentWidgetId)
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, currentWidgetId);
            intentUpdate.putExtra("Random", Math.random() * 1000); // Add a random integer to stop the Intent being ignored.
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

            val pendingUpdate = PendingIntent.getBroadcast(
                context, currentWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    if (!fusedLocationClient.lastLocation.equals(null)) {
                        fusedLocationClient.lastLocation.addOnSuccessListener {
                        }
                        fusedLocationClient.lastLocation.addOnFailureListener {
                            Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        }

                    } else {
                    }
                }
                }

            views.setOnClickPendingIntent(R.id.qiblahRefresh, pendingUpdate)
            val locationCallback2 = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)

                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        val editor: SharedPreferences.Editor = sharedPreferences.edit()
                        editor.putString("latitude", it.latitude.toString())
                        editor.putString("longitude", it.longitude.toString())
                        editor.apply()
                        editor.commit()
                    }
                    fusedLocationClient.lastLocation.addOnFailureListener {
                        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val latitude = sharedPreferences.getString("latitude", "30.007702")
            val longitude = sharedPreferences.getString("longitude", "30.007702")

            val stringFormat = String.format(
                "%.2f", initQiblaDirection(
                    context,
                    latitude.toString().toDouble(), longitude.toString().toDouble()
                )
            )
            views.setTextViewText(
                R.id.tvQiblahDegreesWidget,
                stringFormat
            )
            val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_qibla)
            views.setImageViewBitmap(
                R.id.qiblahDirectionImageView, bitmap.rotated(
                    initQiblaDirection(
                        context, latitude.toString().toDouble(),
                        longitude.toString().toDouble()
                    )
                )
            )
            appWidgetManager.updateAppWidget(idArray, views)
            if (latitude.equals("30.007702") && longitude.equals("30.007702"))
                Toast.makeText(context, context.getString(R.string.please_open_your_gps), Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(
                    context,
                    context.getString(R.string.qibla_refreshed),
                    Toast.LENGTH_SHORT
                ).show()
        }

    }

}

fun Bitmap.rotated(angle: Float): Bitmap {
    val source = this
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
}

var currentDegree: Float = 0f
var currentNeedleDegree: Float = 0f

lateinit var sensorManager: SensorManager
lateinit var sensor: Sensor
lateinit var userLocation: Location

private fun initQiblaDirection(context: Context, latitude: Double, longitude: Double): Float {
    userLocation = Location("User Location")
    userLocation.latitude = latitude
    userLocation.longitude = longitude
    sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION)
    sensorManager.registerListener(object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

        }

        @SuppressLint("SetTextI18n")
        override fun onSensorChanged(sensorEvent: SensorEvent?) {
            val degree: Float = sensorEvent?.values?.get(0)?.roundToInt()?.toFloat()!!
            var head: Float = sensorEvent.values?.get(0)?.roundToInt()?.toFloat()!!

            val destLocation = Location("Destination Location")
            destLocation.latitude = QiblaWidget.QIBLA_LATITUDE
            destLocation.longitude = QiblaWidget.QIBLA_LONGITUDE

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

            currentNeedleDegree = direction
            currentDegree = -degree


        }
    }, sensor, SensorManager.SENSOR_DELAY_GAME)
    return currentNeedleDegree
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {

}
