package com.myapplication.ui.fragments.quran

import android.media.AudioManager
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
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.widget.Toast
import java.io.IOException

import android.graphics.drawable.Drawable





class QuranSoundBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: QuranSoundBottomSheetBinding
    private val viewModel: QuranViewModel by viewModels()
    var ayahNumber : String? = null
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

//        Log.d("getAyahNumber", "ayah : $ayahNumber")
        binding.showResultsBTN.setOnClickListener {
            val mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                mediaPlayer.setDataSource("https://cdn.islamic.network/quran/audio/128/ar.alafasy/$ayahNumber.mp3")
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            this.dismiss()
            Log.v("TAG","Music is streaming")
        }
        binding.abdelbasetTv.setOnClickListener {
            context?.getColor(R.color.logoOrangeColor)?.let { it1 ->
                binding.abdelbasetTv.setTextColor(
                    it1
                )
            }
            val img = context!!.resources.getDrawable(R.drawable.ic_baseline_check_24)
            binding.abdelbasetTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

            val imgUncheck = context!!.resources.getDrawable(R.drawable.ic_baseline_uncheck_24)
            binding.mesharyTv.setCompoundDrawablesWithIntrinsicBounds(imgUncheck, null, null, null)

            context?.getColor(R.color.black)?.let { it1 ->
                binding.mesharyTv.setTextColor(
                    it1
                )
            }
        }
        binding.mesharyTv.setOnClickListener {
            context?.getColor(R.color.logoOrangeColor)?.let { it1 ->
                binding.mesharyTv.setTextColor(
                    it1
                )
            }
            val img = context!!.resources.getDrawable(R.drawable.ic_baseline_check_24)
            binding.mesharyTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

            val imgUncheck = context!!.resources.getDrawable(R.drawable.ic_baseline_uncheck_24)
            binding.abdelbasetTv.setCompoundDrawablesWithIntrinsicBounds(imgUncheck, null, null, null)

            context?.getColor(R.color.black)?.let { it1 ->
                binding.abdelbasetTv.setTextColor(
                    it1
                )
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Log.d("getAyahNumber", "ayah : $ayahNumber")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
    /*    var audioPlayer = MediaPlayer()
        var audioFileUri =
            Uri.parse("https://cdn.islamic.network/quran/audio/128/ar.alafasy/32.mp3")
        audioPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        context?.let { audioPlayer.setDataSource(it, audioFileUri) }
//        audioPlayer.prepareAsync();
        audioPlayer.start()*/
        ayahNumber = arguments?.getString("pageNumber").toString()
//        Toast.makeText(context,"$ayahNumber",Toast.LENGTH_LONG).show()


        }

}