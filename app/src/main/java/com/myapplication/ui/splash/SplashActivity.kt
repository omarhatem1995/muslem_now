package com.myapplication.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.myapplication.LocaleUtil
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.ui.settings.SettingsActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import java.lang.reflect.Method


class SplashActivity : AppCompatActivity() {
    private val REQUEST_CODE_ASK_PERMISSIONS = 1
    private val REQUIRED_SDK_PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkPermissions()

        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var mobileDataEnabled = false // Assume disabled


        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false
        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                if (this?.type?.equals(ConnectivityManager.TYPE_WIFI)!!) {
//                    isWifiConn = isWifiConn or isConnected
                    isWifiConn = isWifiConn or isConnectedOrConnecting
                }
                if (type == ConnectivityManager.TYPE_MOBILE) {
                    isMobileConn = isMobileConn or isConnected

                }
            }
        }
        Toast.makeText(this,"Wifi connected: $isWifiConn" +" , "+ "Mobile connected: $isMobileConn",Toast.LENGTH_LONG )
            .show()

        val cmClass = Class.forName(connMgr.javaClass.name)

        val method: Method = cmClass.getDeclaredMethod("getMobileDataEnabled")
        method.setAccessible(true); // Make the method callable

        mobileDataEnabled = method.invoke(connMgr) as Boolean
        Log.d("mobileDataEnabled : " ,""+ mobileDataEnabled)
        Toast.makeText(this,"Wifi connected: $isWifiConn" +" , "+ "Mobile connected: $isMobileConn",Toast.LENGTH_LONG )
            .show()
        Log.d(Companion.DEBUG_TAG, "Wifi connected: $isWifiConn")
        Log.d(Companion.DEBUG_TAG, "Mobile connected: $isMobileConn")
        LocaleUtil.applyLocalizedContext(this,"ar")
    }

    private fun checkPermissions() {
        val missingPermissions: MutableList<String> = ArrayList()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_ASK_PERMISSIONS -> {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // exit the app if one permission is not granted
                    Toast.makeText(
                        this, getString(R.string.Required_permission_not_granted_exiting), Toast.LENGTH_LONG
                    ).show()
                    finish()
                    return
                } else {
                    initSplash()
                }
            }
        }

    }

    @SuppressLint("CheckResult")
    fun initSplash() {
        Observable.timer(2, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe {
                startActivity(Intent(this, SettingsActivity::class.java))
                finish()

            }

    }

    companion object {
        private const val DEBUG_TAG = "NetworkStatusExample"
    }

}