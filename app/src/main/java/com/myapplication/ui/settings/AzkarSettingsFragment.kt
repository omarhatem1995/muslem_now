package com.myapplication.ui.settings

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.myapplication.R
import com.myapplication.databinding.FragmentAzkarSettingsBinding
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.myapplication.data.core.workmanager.AzkarReciever


class AzkarSettingsFragment : Fragment() {
    lateinit var binding: FragmentAzkarSettingsBinding
    private val vm: SettingsViewModel by viewModels()
    var myHourSabah: String = "0"
    var myHourMasaa: String = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_azkar_settings, container, false
        )
        binding.settingsAzkar = vm
        binding.lifecycleOwner = this

        loadAzkarSwitches()
        changeAzkarSabahAndMasaaTiming()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun changeAzkarSabahAndMasaaTiming() {
        binding.azkarSabahTimeTv.text = vm.preference.getAzkarSabahTiming()
        binding.azkarMasaaTimeTv.text = vm.preference.getAzkarMasaaTiming()
        binding.editAzkarSabahTimeImage.setOnClickListener {

            val cal = Calendar.getInstance()

            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                /* myHour = cal.get(Calendar.HOUR_OF_DAY)
                 myMinute = cal.get(Calendar.MINUTE)*/
                binding.azkarSabahTimeTv.text = SimpleDateFormat("HH:mm").format(cal.time)
                myHourSabah = SimpleDateFormat("HH:mm").format(cal.time)
                vm.preference.setAzkarSabahTiming("$myHourSabah")
                setAlarmAzkarSabahDaily(myHourSabah, "AzkarSabah")
            }
            TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }
        binding.editAzkarMasaaTimeImage.setOnClickListener {

            val cal = Calendar.getInstance()

            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)

                binding.azkarMasaaTimeTv.text = SimpleDateFormat("HH:mm").format(cal.time)
                myHourMasaa = SimpleDateFormat("HH:mm").format(cal.time)
                vm.preference.setAzkarMasaaTiming("$myHourMasaa")
                setAlarmAzkarSabahDaily(myHourMasaa, "AzkarMasaa")

            }
            TimePickerDialog(
                context,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                false
            ).show()
        }
    }

    private fun loadAzkarSwitches() {
        binding.switchAzkarSabah.isChecked = vm.preference.getAzkarSabah()
        binding.switchAzkarMasaa.isChecked = vm.preference.getAzkarMasaa()
        if (binding.switchAzkarSabah.isChecked) {
            binding.azkarSabahConstraintTiming.visibility = View.VISIBLE
            binding.chooseTimeForAzkar.visibility = View.VISIBLE
        } else {
            binding.azkarSabahConstraintTiming.visibility = View.GONE
            binding.chooseTimeForAzkar.visibility = View.GONE
        }

        if (binding.switchAzkarMasaa.isChecked) {
            binding.azkarMasaaConstraintTiming.visibility = View.VISIBLE
            binding.chooseTimeForAzkarMasaa.visibility = View.VISIBLE
        } else {
            binding.azkarMasaaConstraintTiming.visibility = View.GONE
            binding.chooseTimeForAzkarMasaa.visibility = View.GONE
        }
        binding.switchAzkarSabah.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.azkarSabahConstraintTiming.visibility = View.VISIBLE
                binding.chooseTimeForAzkar.visibility = View.VISIBLE
                vm.preference.setAzkarSabah(true)
            } else {
                binding.azkarSabahConstraintTiming.visibility = View.GONE
                binding.chooseTimeForAzkar.visibility = View.GONE
                vm.preference.setAzkarSabah(false)
            }
        }
        binding.switchAzkarMasaa.setOnCheckedChangeListener { buttonView, isChecked ->

            if (isChecked) {
                binding.azkarMasaaConstraintTiming.visibility = View.VISIBLE
                binding.chooseTimeForAzkarMasaa.visibility = View.VISIBLE
                vm.preference.setAzkarMasaa(true)
            } else {
                binding.azkarMasaaConstraintTiming.visibility = View.GONE
                binding.chooseTimeForAzkarMasaa.visibility = View.GONE
                vm.preference.setAzkarSabah(false)
            }
        }

    }


    //    val app: MuslemApp = activity as MuslemApp
//    var hasPermission: Boolean? = true

//    val alarmManager: AlarmManager = context<MuslemApp>().getSystemService(Context.ALARM_SERVICE) as AlarmManager

     fun setAlarmAzkarSabahDaily(azkarTime: String, azkarType: String) {
        val intent = Intent(context, AzkarReciever::class.java)
        separatenumbers(azkarTime)

        val hour = separatenumbers(azkarTime)[0]
        val minutes = separatenumbers(azkarTime)[1]
        val alarmManager = activity!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = minutes
        calendar[Calendar.SECOND] = 0

        var startUpTime = calendar.timeInMillis
        if (System.currentTimeMillis() > startUpTime) {
            startUpTime = startUpTime + 24 * 60 * 60 * 1000
        }
        intent.action = azkarType
        var notificationId = 0
        if (azkarType.equals("AzkarSabah")) {
            intent.putExtra("azkar", "Azkar El Sabah")
            notificationId = 1
        } else if (azkarType.equals("AzkarMasaa")) {
            intent.putExtra("azkar", "Azkar El Masaa")
            notificationId = 2
        }

        val pendingIntent = PendingIntent.getBroadcast(
            activity,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            startUpTime,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
        alarmManager.setAlarmClock(alarmInfo, pendingIntent)

    }

    fun separatenumbers(time: String): ArrayList<Int> {
        val hour = time.substringBefore(':').toInt()
        val min = time.substringAfter(':').toInt()
        val timeArray: ArrayList<Int> = arrayListOf()
        timeArray.add(hour)
        timeArray.add(min)


        return timeArray

    }


}