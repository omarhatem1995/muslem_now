package com.zaker.ui


import android.os.Bundle

import android.app.Activity
import android.app.Dialog
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.zaker.R

import android.view.LayoutInflater
import com.zaker.databinding.DialogChangeLanguageBinding


class CustomDialogClass
    (var c: Activity) : Dialog(c), View.OnClickListener {
    var d: Dialog? = null
    lateinit var binding :DialogChangeLanguageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(
                context
            ), R.layout.dialog_change_language, null, false
        )
        setContentView(binding.root)

        binding.btnYes?.setOnClickListener(this)
        binding.btnNo?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.getId()) {
            R.id.btn_yes -> changeLanguage()
            R.id.btn_no -> dismiss()
            else -> {
            }
        }
        dismiss()
    }

    fun changeLanguage(){
        Toast.makeText(context,"clicked yes", Toast.LENGTH_LONG).show()

    }
}