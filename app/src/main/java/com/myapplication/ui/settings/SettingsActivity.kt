package com.myapplication.ui.settings

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.myapplication.MainActivity
import com.myapplication.R
import com.myapplication.data.entities.model.Languages
import com.myapplication.data.entities.model.MoazenModel
import com.myapplication.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    lateinit var mediaPlayer : MediaPlayer
    var firstTime = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.settingsViewmodel = settingsViewModel

        getLanugages()
//        runMediaPlayer()

        binding.skipNow.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }

    }

   /* private fun runMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this,R.raw.meshary)
        if(!mediaPlayer.isPlaying){
            mediaPlayer.start()
        }
    }*/

    private fun timerTask(){

    }

    private fun getLanugages() {
        val arabic = Languages(getString(R.string.arabic) , R.drawable.arabic)
        val english = Languages(getString(R.string.english) , R.drawable.arabic)
        var arrayLanguages = ArrayList<Languages>()
        arrayLanguages.add(arabic)
        arrayLanguages.add(english)

        var languagesAdapter = LanguagesAdapter(this,arrayLanguages)
        binding.spinner1.adapter = languagesAdapter

        val meshary = MoazenModel(getString(R.string.meshary),R.raw.meshary)
        val hosary = MoazenModel("getString(R.string.english)",R.raw.meshary)
        var arrayMoazen = ArrayList<MoazenModel>()
        arrayMoazen.add(meshary)
        arrayMoazen.add(hosary)

        var moazenAdapter = MoazenAdapter(this,arrayMoazen)
        binding.spinnerMoazen.adapter = moazenAdapter
        var userSelect = false
        binding.spinnerMoazen.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> userSelect = true
                }

                return v?.onTouchEvent(event) ?: true
            }
        })
        binding.spinnerMoazen.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(userSelect) {
                    Log.d("mediaPlacyer", " first Time " + firstTime)
                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.meshary)
                    startMediaPlayerSample(mediaPlayer)
                    userSelect = false
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            }
    }

    private fun startMediaPlayerSample(mediaPlayer: MediaPlayer){
        val cntr_aCounter: CountDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
//                if(mediaPlayer.isPlaying)
//                    mediaPlayer.stop()
                Log.d("onTick" , " is called")

                    mediaPlayer.start()
            }

            override fun onFinish() {
                //code fire after finish
                mediaPlayer.stop()
            }
        }
        cntr_aCounter.start ()
    }
}