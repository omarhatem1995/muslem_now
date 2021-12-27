package com.myapplication.ui.settings

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.myapplication.R
import com.myapplication.data.entities.model.Languages
import com.myapplication.data.entities.model.MoazenModel
import com.myapplication.databinding.ActivityInappSettingsBinding
import com.myapplication.ui.CustomDialogClass
import com.myapplication.MainActivity

import android.content.Intent
import com.myapplication.ui.splash.SplashActivity


class SettingsInAppActivity : AppCompatActivity() {
    lateinit var binding: ActivityInappSettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    lateinit var mediaPlayer: MediaPlayer
    lateinit var changeLanguageDialog: Dialog
    var firstTime: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inapp_settings)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_inapp_settings)
        binding.settingsViewmodel = settingsViewModel

        getLanguages()
        getMoazen()
        mediaPlayer = MediaPlayer()
        changeLanguageDialog = Dialog(this)

        Log.d("opensSameActivity ", " is Opened")
        binding.constraintAzkarSabahAndMasaa.setOnClickListener {
            val azkarSettingsFragment = AzkarSettingsFragment()

            val transaction = supportFragmentManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.fragment_sidemenu_enter_animation,
                R.anim.fragment_sidemenu_exit_animation, R.anim.fragment_sidemenu_enter_animation,
                R.anim.fragment_sidemenu_exit_animation
            )
            transaction.addToBackStack(null)
            transaction.replace(R.id.frame_azkar_settings, azkarSettingsFragment, "BLANK").commit()
        }

        binding.fullAzanImageView.setImageResource(R.drawable.marked)
        binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)

        binding.fullAzanImageView.setOnClickListener {
            binding.fullAzanImageView.setImageResource(R.drawable.marked)
            binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
        }
        binding.fullAzanTextView.setOnClickListener {
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
        val meshary = MoazenModel(getString(R.string.meshary), R.raw.meshary)
        val hosary = MoazenModel(getString(R.string.elhosary), R.raw.elhosary)
        var arrayMoazen = ArrayList<MoazenModel>()
        arrayMoazen.add(meshary)
        arrayMoazen.add(hosary)

        var moazenAdapter = MoazenAdapter(this, arrayMoazen)
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
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (userSelect)
                    if (p2 == 0) {
                        if (mediaPlayer.isPlaying)
                            mediaPlayer.stop()

                        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.meshary)
                        startMediaPlayerSample(mediaPlayer)
                        userSelect = false
                        if (firstTime) {
                            mediaPlayer.stop()
                            firstTime = true
                        }
                    } else if (p2 == 1) {
                        if (mediaPlayer.isPlaying)
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

    private fun getLanguages() {
        val arabic = Languages(getString(R.string.arabic), R.drawable.arabic)
        val english = Languages(getString(R.string.english), R.drawable.ic_united_kingdom)
        var arrayLanguages = ArrayList<Languages>()
        arrayLanguages.add(arabic)
        arrayLanguages.add(english)

        var languagesAdapter = LanguagesAdapter(this, arrayLanguages)
        binding.spinner1.adapter = languagesAdapter

        var languagesFlag = false
        var selectedItem =0
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemClickListener,
            AdapterView.OnItemSelectedListener {
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (languagesFlag) {
                    if (id == 1L && !settingsViewModel.preference.getLanguage().equals("en")) {
                        openChangeLanguageDialog("en")
                    } else if (id == 0L && !settingsViewModel.preference.getLanguage().equals("ar")) {
                        openChangeLanguageDialog("ar")
                    }
                } else {
                    if(settingsViewModel.preference.getLanguage().equals("en"))
                        selectedItem = 1
                    else
                        selectedItem = 0

                    binding.spinner1.setSelection(selectedItem)
                    languagesFlag = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }

    private fun openChangeLanguageDialog(language:String) {

        val root: View = layoutInflater.inflate(R.layout.dialog_change_language, null)
        changeLanguageDialog.setContentView(root)
        var buttonYes = root.findViewById<Button>(R.id.btn_yes)
        var buttonNo = root.findViewById<Button>(R.id.btn_no)
        changeLanguageDialog.setCancelable(false)
        changeLanguageDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        buttonNo.setOnClickListener {
            changeLanguageDialog.dismiss()
        }
        buttonYes.setOnClickListener {
            settingsViewModel.preference.setLanguage(language)
            var intent = Intent(this, SplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finishAffinity()
        }
        changeLanguageDialog.show()

    }

    override fun onPause() {
        super.onPause()
        if(changeLanguageDialog.isShowing)
        changeLanguageDialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(changeLanguageDialog.isShowing)
            changeLanguageDialog.dismiss()
    }

    private fun startMediaPlayerSample(mediaPlayer: MediaPlayer) {
        val cntr_aCounter: CountDownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mediaPlayer.start()
            }

            override fun onFinish() {
                mediaPlayer.stop()
            }
        }
        cntr_aCounter.start()
    }
}