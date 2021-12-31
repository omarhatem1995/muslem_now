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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.myapplication.LocaleUtil
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.databinding.ActivitySettingsBinding
import com.myapplication.databinding.ActivitySplashBinding
import com.myapplication.ui.settings.SettingsActivity
import com.myapplication.ui.settings.SettingsViewModel
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
    lateinit var binding: ActivitySplashBinding
    private val splashViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        checkPermissions()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.splashViewModel = splashViewModel

        val connMgr = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var mobileDataEnabled = false // Assume disabled

        if(!splashViewModel.preference.getFirstLaunch()){
            splashViewModel.preference.setFirstLaunch(true)
            splashViewModel.preference.setLanguage("en")
        }

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

        val cmClass = Class.forName(connMgr.javaClass.name)

        val method: Method = cmClass.getDeclaredMethod("getMobileDataEnabled")
        method.setAccessible(true); // Make the method callable

        mobileDataEnabled = method.invoke(connMgr) as Boolean
        Log.d("mobileDataEnabled : ", "" + mobileDataEnabled)
        Log.d(Companion.DEBUG_TAG, "Wifi connected: $isWifiConn")
        Log.d(Companion.DEBUG_TAG, "Mobile connected: $isMobileConn")
        splashViewModel.preference.getLanguage()?.let { LocaleUtil.applyLocalizedContext(this, it) }
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
                        this,
                        getString(R.string.Required_permission_not_granted_exiting),
                        Toast.LENGTH_LONG
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
                if (splashViewModel.preference.getSettings())
                    startActivity(Intent(this, MainActivity::class.java))
                else
                    startActivity(Intent(this, SettingsActivity::class.java))
                finish()

            }

    }

    companion object {
        private const val DEBUG_TAG = "NetworkStatusExample"
    }

}