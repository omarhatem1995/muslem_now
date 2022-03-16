package com.myapplication.ui.sidemenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.myapplication.R
import com.myapplication.databinding.ActivitySebhaBinding
import com.myapplication.ui.fragments.home.HomeViewModel

class SebhaActivity : AppCompatActivity() {

    lateinit var binding: ActivitySebhaBinding
    private val vm: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_sebha)
        binding.sebhaViewModel = vm

        if(vm.preference.getLanguage().equals("ar")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
        }else if (vm.preference.getLanguage().equals("en")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.backArrowImageView.setOnClickListener {
            finish()
        }

        binding.sebhaCounterClearerButton.setOnClickListener {
            binding.sebhaCounterTextView.text = "0"
        }

        binding.sebhaCounterTextView.setOnClickListener {
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
    }
}