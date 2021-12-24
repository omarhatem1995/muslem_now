package com.myapplication.widgets

import android.annotation.SuppressLint
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.location.Location
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.RemoteViews
import android.content.ComponentName
import android.util.Log
import java.util.*
import android.app.PendingIntent

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.*
import android.widget.Toast

import android.net.Uri
import android.view.View
import android.view.animation.Animation
import com.google.android.gms.location.LocationServices
import com.myapplication.MainActivity
import com.myapplication.QiblahActivity
import com.myapplication.R
import com.myapplication.data.core.workmanager.MuslemApp
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlin.math.roundToInt


/**
 * Implementation of App Widget functionality.
 */
class QiblaWidget : AppWidgetProvider() {
    companion object {
        const val TAG = "MainActivity"
        const val MARSHMALLOW = 23

        const val ACCESS_FINE_LOCATION_REQ_CODE = 35

        const val QIBLA_LATITUDE = 21.3891
        const val QIBLA_LONGITUDE = 39.8579
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val views =
            RemoteViews(context!!.packageName, com.myapplication.R.layout.qibla_widget).also {
            }
//        var x = initLocationListener(context)

//        views.setTextViewText(com.myapplication.R.id.tvQiblahDegreesWidget, x.toString())

        super.onReceive(context, intent)

    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val thisWidget = ComponentName(
            context,
            QiblaWidget::class.java
        )
        val sharedPrefFile = "kotlinsharedpreference"

        val sharedPreferences: SharedPreferences = context
            .getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE)
        val allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget)
        val number: Int = Random().nextInt(100)

        val remoteViews = RemoteViews(
            context.packageName,
            com.myapplication.R.layout.qibla_widget
        )
        val intent = Intent(context, QiblaWidget::class.java)


        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        remoteViews.setOnClickPendingIntent(
            com.myapplication.R.id.tvQiblahDegreesWidget,
            pendingIntent
        )
        remoteViews.setTextViewText(com.myapplication.R.id.tvQiblahDegreesWidget, number.toString())
        Log.w("WidgetExample", " : " + number.toString())
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in allWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }



        for (i in 0 until appWidgetIds.size) {
            val currentWidgetId = appWidgetIds[i]
            val url = "http://www.tutorialspoint.com"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse(url)
            intent.putExtra("sddd", "initLocationListener(context)")
            val pending = PendingIntent.getActivity(context, 0, intent, 0)
            val views = RemoteViews(context.packageName, com.myapplication.R.layout.qibla_widget)

            val intentUpdate = Intent(context, QiblaWidget::class.java)
            intentUpdate.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            val idArray = intArrayOf(currentWidgetId)
            intentUpdate.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, idArray);
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            var x = 0.0
            var y = 0.0

            val pendingUpdate = PendingIntent.getBroadcast(
                context, currentWidgetId, intentUpdate,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            if (!fusedLocationClient.lastLocation.equals(null)) {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    Log.d(
                        "QiblahActivitdslsdsd",
                        "User Location : Lat : ${it.latitude} Long : ${it.longitude}"
                    )
                    x = it.latitude
                    y = it.longitude
                }
                fusedLocationClient.lastLocation.addOnFailureListener {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
                Log.d(
                    "QiblahActivity.TAG,",
                    " is not null ${initQiblaDirection(context, x, y).toString()}"
                )

            } else {
                Log.d("QiblahActivity.TAG,", " is null")

                views.setOnClickPendingIntent(com.myapplication.R.id.ivQiblaCompass, pendingUpdate)
                views.setTextViewText(
                    com.myapplication.R.id.tvQiblahDegreesWidget,
                    "initQiblaDirection(context,x,y).toString()"
                )
            }
//            Log.d("dldldlddldl" , initQiblaDirection(context,
//            20.3000,23.440).toString())

            views.setOnClickPendingIntent(com.myapplication.R.id.ivQiblaCompass, pendingUpdate)
            if (!fusedLocationClient.lastLocation.equals(null)) {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    Log.d(
                        "QiblahActivitdslsdsd",
                        "User Location : Lat : ${it.latitude} Long : ${it.longitude}"
                    )
//                    var preference = SharedPreferencesRepository()

                    val editor:SharedPreferences.Editor =  sharedPreferences.edit()
                    editor.putString("latitude", it.latitude.toString())
                    editor.putString("longitude", it.longitude.toString())
                    editor.apply()
                    editor.commit()
                }
                fusedLocationClient.lastLocation.addOnFailureListener {
                    Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
                }
                val latitude = sharedPreferences.getString("latitude","30.007702")
                val longitude = sharedPreferences.getString("longitude","30.007702")
                Log.d("lskdlskdsldklsd" , longitude.toString())
                val stringFormat = String.format("%.2f", initQiblaDirection(context,
                    latitude.toString().toDouble(), longitude.toString().toDouble()))
                views.setTextViewText(
                    com.myapplication.R.id.tvQiblahDegreesWidget,
                    stringFormat
                )
                val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.image_qibla)
                views.setImageViewBitmap(R.id.qiblahDirectionImageView, bitmap.rotated(initQiblaDirection(context, latitude.toString().toDouble(),
                    longitude.toString().toDouble())))
               /* views.setImageViewResource(R.id.qiblahDirectionImageView,
                    initQiblaDirectionImage(context,views,20.3330,20.300,R.drawable.image_qibla))*/
            }
            appWidgetManager.updateAppWidget(currentWidgetId, views)
            Toast.makeText(context, "widget added", Toast.LENGTH_SHORT).show()

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
lateinit var qiblahDirectionImageView: ImageView
lateinit var needleAnimation: RotateAnimation

/*
fun initQiblah(){
    needleAnimation = RotateAnimation(
        currentNeedleDegree,
        0f,
        Animation.RELATIVE_TO_SELF,
        .5f,
        Animation.RELATIVE_TO_SELF,
        .5f
    )

}
private fun initLocationListener(context: Context):Float {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var direction = 0f
    fusedLocationClient.lastLocation.addOnSuccessListener {
        Log.d(QiblahActivity.TAG, "User Location : Lat : ${it.latitude} Long : ${it.longitude}")
        direction = initQiblaDirection(context,it.latitude, it.longitude)
    }
    fusedLocationClient.lastLocation.addOnFailureListener {
        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
    }
    return direction
}
private fun initQiblaDirection(context:Context ,latitude: Double, longitude: Double):Float {
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
            destLocation.latitude = HomeFragment.QIBLA_LATITUDE
            destLocation.longitude = HomeFragment.QIBLA_LONGITUDE

            var bearTo = userLocation.bearingTo(destLocation)

            val geoField = GeomagneticField(
                userLocation.latitude.toFloat(),
                userLocation.longitude.toFloat(),
                userLocation.altitude.toFloat(),
                System.currentTimeMillis()
            )
            Log.d("asdlasdlasld", longitude.toString())
            head -= geoField.declination

            if (bearTo < 0) {
                bearTo += 360
            }

            var direction = bearTo - head

            if (direction < 0) {
                direction += 360
            }

//            binding.tvQiblahDegrees.text = "Heading : $degree + degrees"

            Log.d(
                "HomeFragment.TAG",
                "Needle Degree : $currentNeedleDegree, Direction : $direction"
            )
//            binding.tvQiblahDegrees.text = currentNeedleDegree.toString()

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

//            binding.qiblahDirectionImageView.startAnimation(needleAnimation)
            currentNeedleDegree = direction
            currentDegree = -degree

//            if (currentNeedleDegree <= 10 || currentNeedleDegree >= 350) {
//                binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.logoOrangeColor))
//                binding.tvQiblahDegrees.setTextColor(getColor(R.color.logoOrangeColor))
//            } else {
//
//                binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.backgroundGreen))
//
//                binding.tvQiblahDegrees.setTextColor(getColor(R.color.textColorQiblahDegrees))
//

//            }

        }
    }, sensor, SensorManager.SENSOR_DELAY_GAME)
    return currentNeedleDegree
}*/
/*private fun initQiblaDirectionImage(
    context: Context, view: RemoteViews, latitude: Double, longitude: Double,
    image: Int
): Int {
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
            destLocation.latitude = HomeFragment.QIBLA_LATITUDE
            destLocation.longitude = HomeFragment.QIBLA_LONGITUDE

            var bearTo = userLocation.bearingTo(destLocation)

            val geoField = GeomagneticField(
                userLocation.latitude.toFloat(),
                userLocation.longitude.toFloat(),
                userLocation.altitude.toFloat(),
                System.currentTimeMillis()
            )
            Log.d("asdlasdlasld", longitude.toString())
            head -= geoField.declination

            if (bearTo < 0) {
                bearTo += 360
            }

            var direction = bearTo - head

            if (direction < 0) {
                direction += 360
            }

//            binding.tvQiblahDegrees.text = "Heading : $degree + degrees"

            Log.d(
                "HomeFragment.TAG",
                "Needle Degree : $currentNeedleDegree, Direction : $direction"
            )
//            binding.tvQiblahDegrees.text = currentNeedleDegree.toString()

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

//            binding.qiblahDirectionImageView.startAnimation(needleAnimation)
            currentNeedleDegree = direction
            currentDegree = -degree
//            qiblahDirectionImageView

            qiblahDirectionImageView.setImageResource(image)

            Log.d("sdlskdslkd" ,qiblahDirectionImageView.startAnimation(needleAnimation).toString())
//            if (currentNeedleDegree <= 10 || currentNeedleDegree >= 350) {
//                binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.logoOrangeColor))
//                binding.tvQiblahDegrees.setTextColor(getColor(R.color.logoOrangeColor))
//            } else {
//
//                binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.backgroundGreen))
//
//                binding.tvQiblahDegrees.setTextColor(getColor(R.color.textColorQiblahDegrees))
//

//            }

        }
    }, sensor, SensorManager.SENSOR_DELAY_GAME)
    return image
}*/

private fun initQiblaDirection(context: Context, latitude: Double, longitude: Double): Float {
    userLocation = Location("User Location")
    userLocation.latitude = latitude
    userLocation.longitude = longitude
    Log.d("asdlaskdasd" , userLocation.latitude.toString())
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
            Log.d("asdlasdlasld", userLocation.latitude.toString())
            head -= geoField.declination

            if (bearTo < 0) {
                bearTo += 360
            }

            var direction = bearTo - head

            if (direction < 0) {
                direction += 360
            }

//            binding.tvQiblahDegrees.text = "Heading : $degree + degrees"

            Log.d(
                "HomeFragment.TAG",
                "Needle Degree : $currentNeedleDegree, Direction : $direction"
            )
//            binding.tvQiblahDegrees.text = currentNeedleDegree.toString()
/*

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
*/

//            binding.qiblahDirectionImageView.startAnimation(needleAnimation)
            currentNeedleDegree = direction
            currentDegree = -degree

            Log.d("currentDegreeIS", " $currentDegree , $currentNeedleDegree")
//            if (currentNeedleDegree <= 10 || currentNeedleDegree >= 350) {
//                binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.logoOrangeColor))
//                binding.tvQiblahDegrees.setTextColor(getColor(R.color.logoOrangeColor))
//            } else {
//
//                binding.qiblahDirectionImageView.setColorFilter(getColor(R.color.backgroundGreen))
//
//                binding.tvQiblahDegrees.setTextColor(getColor(R.color.textColorQiblahDegrees))
//

//            }

        }
    }, sensor, SensorManager.SENSOR_DELAY_GAME)
    return currentNeedleDegree
}

private fun initLocationListener(context: Context): Float {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var direction = 0f
    fusedLocationClient.lastLocation.addOnSuccessListener {
        Log.d(QiblahActivity.TAG, "User Location : Lat : ${it.latitude} Long : ${it.longitude}")
//        direction = initQiblaDirection(context,it.latitude, it.longitude)
    }
    fusedLocationClient.lastLocation.addOnFailureListener {
        Toast.makeText(context, it.localizedMessage, Toast.LENGTH_SHORT).show()
    }
    return direction
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
//    val widgetText = context.getString(R.string.appwidget_text)
//    // Construct the RemoteViews object
//    val views = RemoteViews(context.packageName, R.layout.qibla_widget)
//    views.setTextViewText(R.id.appwidget_text, widgetText)
//
//    // Instruct the widget manager to update the widget
//    appWidgetManager.updateAppWidget(appWidgetId, views)


}
