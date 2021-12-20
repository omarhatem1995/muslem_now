package com.myapplication.ui.settings

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.myapplication.R
import com.myapplication.data.entities.model.Languages
import com.myapplication.data.entities.model.MoazenModel
import com.myapplication.databinding.ActivityInappSettingsBinding


class SettingsInAppActivity : AppCompatActivity() {
    lateinit var binding: ActivityInappSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    lateinit var mediaPlayer : MediaPlayer
    var firstTime : Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inapp_settings)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_inapp_settings)
        binding.settingsViewmodel = settingsViewModel

        getLanugages()
        getMoazen()
        mediaPlayer = MediaPlayer()

        binding.constraintAzkarSabahAndMasaa.setOnClickListener {
            val azkarSettingsFragment = AzkarSettingsFragment()

            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(R.anim.fragment_sidemenu_enter_animation,
                R.anim.fragment_sidemenu_exit_animation,R.anim.fragment_sidemenu_enter_animation,
                R.anim.fragment_sidemenu_exit_animation)
            transaction.addToBackStack(null)
            transaction.replace(R.id.frame_azkar_settings,azkarSettingsFragment,"BLANK").commit()
        }

        binding.fullAzanImageView.setImageResource(R.drawable.marked)
        binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)

        binding.fullAzanImageView.setOnClickListener {
            binding.fullAzanImageView.setImageResource(R.drawable.marked)
            binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
        }
        binding.fullAzanTextView.setOnClickListener{
            binding.fullAzanImageView.setImageResource(R.drawable.marked)
            binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
        }
        binding.takbiratAzanImageView.setOnClickListener {
            binding.fullAzanImageView.setImageResource(R.drawable.eclipse)
            binding.takbiratAzanImageView.setImageResource(R.drawable.marked)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
        }
        binding.takbiratAzanTextView.setOnClickListener {
            binding.fullAzanImageView.setImageResource(R.drawable.eclipse)
            binding.takbiratAzanImageView.setImageResource(R.drawable.marked)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
        }

    }

    private fun getMoazen() {
        val meshary = MoazenModel(getString(R.string.meshary),R.raw.meshary)
        val hosary = MoazenModel(getString(R.string.elhosary),R.raw.elhosary)
        var arrayMoazen = ArrayList<MoazenModel>()
        arrayMoazen.add(meshary)
        arrayMoazen.add(hosary)

        var moazenAdapter = MoazenAdapter(this,arrayMoazen)
        binding.spinnerMoazen.adapter = moazenAdapter
        var userSelect = false
        binding.spinnerMoazen.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                    }
                }
                userSelect = true
                return v?.onTouchEvent(event) ?: true
            }
        })

        binding.spinnerMoazen.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if(userSelect)
                if(p2 == 0) {
                    if(mediaPlayer.isPlaying)
                        mediaPlayer.stop()

                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.meshary)
                    startMediaPlayerSample(mediaPlayer)
                    userSelect = false
                    if(firstTime) {
                        mediaPlayer.stop()
                        firstTime = true
                    }
                }else if (p2 == 1){
                    if(mediaPlayer.isPlaying)
                        mediaPlayer.stop()

                    mediaPlayer = MediaPlayer.create(applicationContext, R.raw.elhosary)
                    startMediaPlayerSample(mediaPlayer)
                    userSelect = false
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

        }
    }

    private fun getLanugages() {
        val arabic = Languages(getString(R.string.arabic) , R.drawable.arabic)
        val english = Languages(getString(R.string.english) , R.drawable.ic_united_kingdom)
        var arrayLanguages = ArrayList<Languages>()
        arrayLanguages.add(arabic)
        arrayLanguages.add(english)

        var languagesAdapter = LanguagesAdapter(this,arrayLanguages)
        binding.spinner1.adapter = languagesAdapter

    }

    private fun startMediaPlayerSample(mediaPlayer: MediaPlayer){
        val cntr_aCounter: CountDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                    mediaPlayer.start()
            }

            override fun onFinish() {
                mediaPlayer.stop()
            }
        }
        cntr_aCounter.start ()
    }
}