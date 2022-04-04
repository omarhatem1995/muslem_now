package com.myapplication.ui.sidemenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.myapplication.R
import com.myapplication.databinding.ActivityRosaryBinding
import com.myapplication.ui.fragments.home.HomeViewModel

class RosaryActivity : AppCompatActivity() {

    lateinit var binding: ActivityRosaryBinding
    private val electronicRosaryViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_rosary)
        binding.sebhaViewModel = electronicRosaryViewModel

        if(electronicRosaryViewModel.preference.getLanguage().equals("ar")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
        }else if (electronicRosaryViewModel.preference.getLanguage().equals("en")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
        }
        binding.sebhaCounterTextView.text = electronicRosaryViewModel.preference.getRosary()

        binding.backArrowImageView.setOnClickListener {
            finish()
        }

        binding.sebhaCounterClearerButton.setOnClickListener {
            binding.sebhaCounterTextView.text = "0"
            electronicRosaryViewModel.preference.setRosary("0")
        }

        binding.sebhaCounterTextView.setOnClickListener {
            counterIncrease()
        }

        binding.rosaryConstraintLayout.setOnClickListener {
            counterIncrease()
        }

        binding.sebhaImageView.setOnClickListener {
            counterIncrease()
        }

    }

    private fun counterIncrease() {
        var counter = binding.sebhaCounterTextView.text.toString().toInt()
        counter += 1
        binding.sebhaCounterTextView.text = counter.toString()
        electronicRosaryViewModel.preference.setRosary("$counter")
    }
}