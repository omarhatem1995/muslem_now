package com.myapplication.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.myapplication.R
import com.myapplication.databinding.FragmentSideMenuBinding
import com.myapplication.ui.settings.SettingsInAppActivity
import com.myapplication.ui.sidemenu.*


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

        if(vm.preference.getLanguage().equals("ar")){
            binding.backFragmentButton.setImageResource(R.drawable.ic_baseline_arrow_forward_24)
            binding.arrowQibla.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowSettings.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowSebha.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowNamesOfAllah.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowNearByMosques.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowRadio.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowSahihBukharyImage.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
            binding.arrowThe40s.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)

        }else if (vm.preference.getLanguage().equals("en")){
            binding.backFragmentButton.setImageResource(R.drawable.ic_baseline_arrow_back_24)
            binding.arrowQibla.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowSettings.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowSebha.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowNamesOfAllah.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowNearByMosques.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowRadio.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowSahihBukharyImage.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
            binding.arrowThe40s.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
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

        binding.constraintSebha.setOnClickListener {
            val intent = Intent(context, RosaryActivity::class.java)
            context?.startActivity(intent)
        }
        binding.constraintNamesOfAllah.setOnClickListener {
            val intent = Intent(context, NamesOfAllahActivity::class.java)
            context?.startActivity(intent)
        }
        binding.constraintNearByMosques.setOnClickListener {
            val intent = Intent(context, NearByMosquesActivity::class.java)
            context?.startActivity(intent)
        }
        binding.constraintRadio.setOnClickListener {
            val intent = Intent(context, NearByMosquesActivity::class.java)
            context?.startActivity(intent)
        }

        binding.constraintSahihBukhary.setOnClickListener {
            val intent = Intent(context, SahihBukharyActivity::class.java)
            intent.putExtra("bukharyOr40s","bukhary")
            context?.startActivity(intent)
        }
        binding.constraintThe40s.setOnClickListener {
            val intent = Intent(context, SahihBukharyActivity::class.java)
            intent.putExtra("bukharyOr40s","the40s")
            context?.startActivity(intent)
        }

        return binding.root
    }

    private fun sendBack() {
        if (requireFragmentManager().getBackStackEntryCount() > 0) {
            requireFragmentManager().popBackStackImmediate()
        }    }

}