package com.myapplication.ui.fragments.radio

import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapplication.R
import java.io.IOException

class RadioFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
        }
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
//            this.dismiss()
//                        mediaPlayer?.setDataSource("https://everyayah.com/data/Alafasy_128kbps/PageMp3s/Page$pageInQuran.mp3")
                        mediaPlayer?.prepare()
                        mediaPlayer?.start()

        } catch (e: IOException) {
            e.printStackTrace()
        }
        return inflater.inflate(R.layout.activity_radio, container, false)
    }

}