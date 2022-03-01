package com.myapplication.ui.fragments.quran

import android.app.Dialog
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
import com.myapplication.databinding.QuranSoundBottomSheetBinding
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import java.io.IOException
import android.graphics.drawable.Drawable
import android.util.Log
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector


class QuranSoundBottomSheet : BottomSheetDialogFragment()
//    , Player.EventListener
{
    lateinit var binding: QuranSoundBottomSheetBinding
    private val viewModel: QuranViewModel by viewModels()
    var ayahNumber: String? = null
    lateinit var mediaPlayer: MediaPlayer
    lateinit var selectedSheikh: String
    val MESHARY = "Meshary"
    val ABDELBASET = "AbdelBaset"
    val AJAMI = "Ajami"
    val MENSHAWY = "Menshawy"

    //    lateinit var player: SimpleExoPlayer
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

        when {
            viewModel.preference.getQuranSheikh() == ABDELBASET -> {
                abdelbasetClickListener()
            }
            viewModel.preference.getQuranSheikh() == MESHARY -> {
                mesharyClickListener()
            }
            viewModel.preference.getQuranSheikh() == AJAMI -> {
                ajamiClickListener()
            }
            viewModel.preference.getQuranSheikh() == MENSHAWY -> {
                menshawyClickListener()
            }
            else -> {
                mesharyClickListener()
            }
        }

        binding.showResultsBTN.setOnClickListener {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                this.dismiss()
                when (selectedSheikh) {
                    MESHARY -> {
                        mediaPlayer.setDataSource("https://cdn.islamic.network/quran/audio/128/ar.alafasy/$ayahNumber.mp3")
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    }
                    ABDELBASET -> {
                        mediaPlayer.setDataSource("https://everyayah.com/data/Abdul_Basit_Mujawwad_128kbps/$suraNumber$ayaInSura.mp3")
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    }
                    AJAMI -> {
                        mediaPlayer.setDataSource("https://everyayah.com/data/ahmed_ibn_ali_al_ajamy_128kbps/$suraNumber$ayaInSura.mp3")
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    }
                    MENSHAWY -> {
                        mediaPlayer.setDataSource("https://everyayah.com/data/Minshawy_Mujawwad_192kbps/$suraNumber$ayaInSura.mp3")
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    }
                    else -> {
                        mediaPlayer.setDataSource("https://cdn.islamic.network/quran/audio/128/ar.alafasy/$ayahNumber.mp3")
                        mediaPlayer.prepare()
                        mediaPlayer.start()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
//            this.dismiss()
            Log.v("TAG", "Music is streaming")
        }
        binding.stopBTN.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }

        }
        binding.abdelbasetTv.setOnClickListener {
            abdelbasetClickListener()
        }
        binding.ajamiTv.setOnClickListener {
            ajamiClickListener()
        }
        binding.mesharyTv.setOnClickListener {
            mesharyClickListener()
        }
        binding.menshawyTv.setOnClickListener {
            menshawyClickListener()
        }

        return binding.root
    }

    private fun mesharyClickListener() {
        viewModel.preference.setQuranSheikh(MESHARY)
        selectedSheikh = MESHARY
        context?.getColor(R.color.logoOrangeColor)?.let { it1 ->
            binding.mesharyTv.setTextColor(
                it1
            )
        }
        val img = context!!.resources.getDrawable(R.drawable.ic_baseline_check_24)
        binding.mesharyTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

        val imgUncheck = context!!.resources.getDrawable(R.drawable.ic_baseline_uncheck_24)
        binding.abdelbasetTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.abdelbasetTv.setTextColor(
                it1
            )
        }
        binding.ajamiTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.ajamiTv.setTextColor(
                it1
            )
        }
        binding.menshawyTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.menshawyTv.setTextColor(
                it1
            )
        }
    }


    private fun menshawyClickListener() {
        viewModel.preference.setQuranSheikh(MENSHAWY)
        selectedSheikh = MENSHAWY
        context?.getColor(R.color.logoOrangeColor)?.let { it1 ->
            binding.menshawyTv.setTextColor(
                it1
            )
        }
        val img = context!!.resources.getDrawable(R.drawable.ic_baseline_check_24)
        binding.menshawyTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

        val imgUncheck = context!!.resources.getDrawable(R.drawable.ic_baseline_uncheck_24)
        binding.abdelbasetTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.abdelbasetTv.setTextColor(
                it1
            )
        }
        binding.ajamiTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.ajamiTv.setTextColor(
                it1
            )
        }
        binding.mesharyTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.mesharyTv.setTextColor(
                it1
            )
        }
    }

    private fun abdelbasetClickListener() {
        viewModel.preference.setQuranSheikh(ABDELBASET)
        selectedSheikh = ABDELBASET
        context?.getColor(R.color.logoOrangeColor)?.let { it1 ->
            binding.abdelbasetTv.setTextColor(
                it1
            )
        }
        val img = context!!.resources.getDrawable(R.drawable.ic_baseline_check_24)
        binding.abdelbasetTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

        val imgUncheck = context!!.resources.getDrawable(R.drawable.ic_baseline_uncheck_24)
        binding.mesharyTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.mesharyTv.setTextColor(
                it1
            )
        }
        binding.menshawyTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.menshawyTv.setTextColor(
                it1
            )
        }

        binding.ajamiTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.ajamiTv.setTextColor(
                it1
            )
        }
    }

    private fun ajamiClickListener() {
        viewModel.preference.setQuranSheikh(AJAMI)
        selectedSheikh = AJAMI
        context?.getColor(R.color.logoOrangeColor)?.let { it1 ->
            binding.ajamiTv.setTextColor(
                it1
            )
        }
        val img = context!!.resources.getDrawable(R.drawable.ic_baseline_check_24)
        binding.ajamiTv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null)

        val imgUncheck = context!!.resources.getDrawable(R.drawable.ic_baseline_uncheck_24)
        binding.mesharyTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.mesharyTv.setTextColor(
                it1
            )
        }
        binding.menshawyTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.menshawyTv.setTextColor(
                it1
            )
        }
        binding.abdelbasetTv.setCompoundDrawablesWithIntrinsicBounds(
            imgUncheck,
            null,
            null,
            null
        )

        context?.getColor(R.color.black)?.let { it1 ->
            binding.abdelbasetTv.setTextColor(
                it1
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    var suraNumberAndAyaInSura: String? = null
    var suraNumber: String? = null
    var ayaInSura: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.AppBottomSheetDialogTheme)
        ayahNumber = arguments?.getString("pageNumber").toString()
        suraNumberAndAyaInSura = arguments?.getString("suraNumberAndAyaInSuraNumber").toString()
        suraNumber = suraNumberAndAyaInSura?.substringBefore("+")
        ayaInSura = suraNumberAndAyaInSura?.substringAfter("+")

        Log.d("getValuesOfIntent", " ${suraNumber?.length} , ${ayaInSura?.length}")
//        Toast.makeText(context,"$suraNumber , $ayaInSura",Toast.LENGTH_LONG).show()
        if (suraNumber?.length == 2) {
            suraNumber = "0$suraNumber"
        } else if (suraNumber?.length == 1) {
            suraNumber = "00$suraNumber"
        }
        if (ayaInSura?.length == 2) {
            ayaInSura = "0$ayaInSura"
        } else if (ayaInSura?.length == 1) {
            ayaInSura = "00$ayaInSura"
        }
    }

}