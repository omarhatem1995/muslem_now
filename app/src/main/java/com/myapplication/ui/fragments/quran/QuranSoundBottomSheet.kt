package com.myapplication.ui.fragments.quran

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.myapplication.R
import com.myapplication.databinding.FragmentHomeBinding
import com.myapplication.databinding.QuranSoundBottomSheetBinding
import com.myapplication.ui.fragments.home.HomeViewModel

class QuranSoundBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: QuranSoundBottomSheetBinding
    private val viewModel:QuranViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.quran_sound_bottom_sheet, container, false
        )
        binding.quranSoundBottomSheet = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME,R.style.AppBottomSheetDialogTheme)

    }
}