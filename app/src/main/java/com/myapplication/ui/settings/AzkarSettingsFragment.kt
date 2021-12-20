package com.myapplication.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.myapplication.R
import com.myapplication.databinding.FragmentAzkarSettingsBinding

class AzkarSettingsFragment : Fragment() {
    lateinit var binding: FragmentAzkarSettingsBinding
    private val vm: SettingsViewModel by viewModels()
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


        // Inflate the layout for this fragment
        return binding.root
    }

}