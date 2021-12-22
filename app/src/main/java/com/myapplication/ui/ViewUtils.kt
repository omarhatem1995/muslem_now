package com.myapplication.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.snackbar.Snackbar
import com.myapplication.R
import java.io.ByteArrayOutputStream

object ViewUtils {
    fun Context.getColorCompat(color: Int) = ContextCompat.getColor(this, color)


    fun highlight(title: String, value: String, textview: TextView) {
        val sentence = " <font color='#2a384d'>$title</font> $value"
        textview.text = HtmlCompat.fromHtml(sentence, HtmlCompat.FROM_HTML_MODE_LEGACY)

    }


    fun Context.glidePlaceHolder(): CircularProgressDrawable {
        return CircularProgressDrawable(this).apply {
            strokeWidth = 5f
            centerRadius = 30f
            setColorSchemeColors(getColorCompat(R.color.backGroundColor))
            start()
        }
    }


    fun Context.getAppVersion(): String? {
        var version: String? = null
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            version = pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return version
    }

    fun Context?.toast(@StringRes textId: Int, duration: Int = Toast.LENGTH_SHORT) =
        this?.let { Toast.makeText(it, textId, duration).show() }


    fun snackBar(view: View?, context: Context, message: String) {
        Snackbar.make(view!!, message, Snackbar.LENGTH_LONG)
            .setActionTextColor(ContextCompat.getColor(context, R.color.white))
            .show()

    }

    fun disableView(view: View) {
        view.alpha = 0.5F
        view.isClickable = false
        view.isEnabled = false
    }

    fun enableView(view: View) {
        view.alpha = 1F
        view.isClickable = true
        view.isEnabled = true

    }

    fun showProgressDialog(progressDialog: Dialog) {
        progressDialog.setContentView(R.layout.dialog_ordering)
//        progressDialog.window?.setLayout(
//            WindowManager.LayoutParams.WRAP_CONTENT,
//            WindowManager.LayoutParams.WRAP_CONTENT
//        )
//        progressDialog.window?.setGravity(Gravity.CENTER)
//        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun fromBitmapToBase64(image: Bitmap): String? {
        val bitmapByteArray = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 20, bitmapByteArray)
        val byteArray = bitmapByteArray.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)

    }

    fun fromBase64ToBitmap(encodedImage: String?): Bitmap? {
        val decodedString: ByteArray = Base64.decode(encodedImage, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun proceedToActivity(context: Context, activity: Activity, toActivity: Activity) {
        val i = Intent(context, toActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        context.startActivity(i)
        activity.finish()

    }


}