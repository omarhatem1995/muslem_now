package com.myapplication.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.myapplication.LocaleUtil
import com.myapplication.QiblahActivity
import com.myapplication.R
import com.myapplication.databinding.FragmentHomeBinding
import com.myapplication.databinding.FragmentSideMenuBinding
import com.myapplication.ui.settings.SettingsActivity
import com.myapplication.ui.settings.SettingsInAppActivity


class SideMenuFragment : Fragment() {
    lateinit var binding: FragmentSideMenuBinding
    private val vm: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_side_menu, container, false
        )
        binding.sidemenuViewModel = vm
        binding.lifecycleOwner = this

        val locale = "ar"
        if(locale.equals("ar")){
            binding.backFragmentButton.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
            binding.arrowQibla.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowSettings.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)

        }else {
            binding.backFragmentButton.setImageResource(R.drawable.ic_baseline_arrow_back_24)
            binding.arrowQibla.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowSettings.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
        }

        binding.backFragmentButton.setOnClickListener {
            sendBack()
        }

        binding.linearQiblah.setOnClickListener {
            val intent = Intent(context, QiblahActivity::class.java)
            context?.startActivity(intent)
        }

        binding.linearSettings.setOnClickListener {
            val intent = Intent(context, SettingsInAppActivity::class.java)
            context?.startActivity(intent)
        }

        return binding.root
    }

    private fun sendBack() {
        fragmentManager?.popBackStack()
    }

}