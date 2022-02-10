package com.myapplication.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.myapplication.R
import com.myapplication.databinding.ActivityTestBinding
import com.myapplication.databinding.ActivityZekrBinding
import com.myapplication.ui.azkar.AzkarListAdapter
import com.myapplication.ui.azkar.AzkarViewModel
import com.myapplication.ui.fragments.quran.QuranViewModel

class TestActivity : AppCompatActivity() {
    lateinit var binding: ActivityTestBinding
    private val viewModel: QuranViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test)
        binding.test = viewModel
        binding.lifecycleOwner = this
        viewModel.getSpecificTest(1)
        viewModel.viewStateAzkar.observe(this,{
            binding.testRecyclerViewList.adapter =  TestAdapter(this,it)/*
            adapter.submitList(it)
            binding.azkarRecyclerViewList.adapter = adapter*/

        })
        }
}