package com.myapplication.ui.sidemenu

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.myapplication.R
import com.myapplication.databinding.ActivityNamesOfAllahBinding
import com.myapplication.ui.adapter.NamesOfAllahAdapter
import com.myapplication.ui.fragments.home.HomeViewModel
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

class NamesOfAllahActivity : AppCompatActivity() {
    lateinit var binding: ActivityNamesOfAllahBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_names_of_allah)
        binding.namesOfAllahViewModel = viewModel
        binding.lifecycleOwner = this

        if(viewModel.preference.getLanguage().equals("ar")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_forward_ios_24)
        }else if (viewModel.preference.getLanguage().equals("en")){
            binding.backArrowImageView.setImageResource(R.drawable.ic_baseline_arrow_back_ios_24)
        }

        binding.backArrowImageView.setOnClickListener { finish() }

        var json: String? = null
        try {
            val inputStream: InputStream = assets.open("names_of_allah.json")
            json = inputStream.bufferedReader().use { it.readText() }
            val data = JSONObject(json)
            var jsonArray = JSONArray(data.getString("data"))
            var namesOfALlah =  ArrayList<String>()
            for (i in 0 until jsonArray.length()) {
            var nameJsonObject = jsonArray.getJSONObject(i).getString("name")
                namesOfALlah.add(nameJsonObject)
            }
            binding.recyclerViewNamesOfAllah.adapter =  NamesOfAllahAdapter(applicationContext,namesOfALlah)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }
}