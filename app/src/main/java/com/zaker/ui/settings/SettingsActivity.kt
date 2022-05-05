package com.zaker.ui.settings

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zaker.MainActivity
import com.zaker.R
import com.zaker.common.Constants
import com.zaker.data.entities.model.Languages
import com.zaker.data.entities.model.MoazenModel
import com.zaker.databinding.ActivitySettingsBinding
import com.zaker.ui.splash.SplashActivity
import android.widget.CompoundButton
import kotlinx.coroutines.InternalCoroutinesApi


class SettingsActivity : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel: SettingsViewModel by viewModels()
    lateinit var mediaPlayer : MediaPlayer
    var firstTime : Boolean = false
    var azanType = Constants.FULL_AZAN
    var setAzkarAfterAzan = false
    var setAzkarSabahAndMasaa = false
    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.settingsViewmodel = settingsViewModel
        changeLanguageDialog = Dialog(this)

        getLanguages()
        getMoazen()
        mediaPlayer = MediaPlayer()


        binding.skipNow.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            settingsViewModel.preference.setSettings(true)
            settingsViewModel.preference.setAzanType(azanType)
            if(setAzkarAfterAzan)
            settingsViewModel.preference.setAzkarAfterAzan(setAzkarAfterAzan)
            settingsViewModel.preference.setAzkarSabah(setAzkarSabahAndMasaa)
            settingsViewModel.preference.setAzkarMasaa(setAzkarSabahAndMasaa)
            finish()

        }
        binding.azkarSabahAndMasaaSwitch.isSelected = false
        binding.azkarAfterAzanSwitch.isSelected = false

        binding.azkarAfterAzanSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            // do something, the isChecked will be
            // true if the switch is in the On position
            setAzkarAfterAzan = isChecked
        })

        binding.azkarSabahAndMasaaSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener{_, isChecked ->
            setAzkarSabahAndMasaa = isChecked
        })

        binding.fullAzanImageView.setImageResource(R.drawable.marked)
        binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)

        binding.fullAzanImageView.setOnClickListener {
            binding.fullAzanImageView.setImageResource(R.drawable.marked)
            binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
            azanType = Constants.FULL_AZAN
        }
        binding.fullAzanTextView.setOnClickListener{
            binding.fullAzanImageView.setImageResource(R.drawable.marked)
            binding.takbiratAzanImageView.setImageResource(R.drawable.eclipse)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
            azanType = Constants.FULL_AZAN
        }
        binding.takbiratAzanImageView.setOnClickListener {
            binding.fullAzanImageView.setImageResource(R.drawable.eclipse)
            binding.takbiratAzanImageView.setImageResource(R.drawable.marked)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
            azanType = Constants.TAKBIRAT_ONLY
        }
        binding.takbiratAzanTextView.setOnClickListener {
            binding.fullAzanImageView.setImageResource(R.drawable.eclipse)
            binding.takbiratAzanImageView.setImageResource(R.drawable.marked)
            binding.fullAzanTextView.setTextColor(resources.getColor(R.color.textColorGrey2))
            binding.takbiratAzanTextView.setTextColor(resources.getColor(R.color.textColorGreen))
            azanType = Constants.TAKBIRAT_ONLY
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
                    settingsViewModel.preference.setMoazen(Constants.AZANMESHARY)
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
                    settingsViewModel.preference.setMoazen(Constants.AZANELHOSARY)
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
        binding.languageSettings.adapter = languagesAdapter

        var languagesFlag = false
        var selectedItem :Int
        binding.languageSettings.onItemSelectedListener = object : AdapterView.OnItemClickListener,
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
                        openChangeLanguageAndRestartDialog("en")
                    } else if (id == 0L && !settingsViewModel.preference.getLanguage().equals("ar")) {
                        openChangeLanguageAndRestartDialog("ar")
                    }
                } else {
                    if(settingsViewModel.preference.getLanguage().equals("en"))
                        selectedItem = 1
                    else
                        selectedItem = 0

                    binding.languageSettings.setSelection(selectedItem)
                    languagesFlag = true
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

        }

    }
    lateinit var changeLanguageDialog: Dialog

    private fun openChangeLanguageAndRestartDialog(language:String) {

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
        changeLanguageDialog.dismiss()
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