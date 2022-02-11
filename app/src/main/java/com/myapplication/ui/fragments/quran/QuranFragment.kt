package com.myapplication.ui.fragments.quran

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.LocaleUtil
import com.myapplication.R
import com.myapplication.data.entities.model.QuranIndexModel
import com.myapplication.databinding.FragmentAzkarBinding
import com.myapplication.databinding.FragmentQuranBinding
import com.myapplication.ui.azkar.AzkarAdapter
import com.myapplication.ui.azkar.AzkarViewModel
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

class QuranFragment : Fragment() {

    lateinit var recyclerViewAzkar: RecyclerView
    private val vm: QuranViewModel by viewModels()
    lateinit var binding: FragmentQuranBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_quran, container, false
        )
        binding.quranFragment = vm

        loadJSONFromAsset()

        return binding.root
    }

    fun addToModel() {

    }

    lateinit var surahModel: QuranIndexModel
    lateinit var quranModelList: MutableList<QuranIndexModel>

    fun loadJSONFromAsset() {
        var json: String? = null
        try {
            val inputStream: InputStream? = context?.assets?.open("surah.json")
            json = inputStream?.bufferedReader().use { it?.readText() }
            var jsonArray = JSONArray(json)
            var quranModelList: MutableList<QuranIndexModel> = ArrayList()
            for (i in 0 until jsonArray.length()) {
                var jsonObject = jsonArray.getJSONObject(i)

                var place = jsonObject.getString("place")
                var titleAr = jsonObject.getString("titleAr")
                var title = jsonObject.getString("title")
                var count = jsonObject.getString("count")
                var page = jsonObject.getString("page")
                var type = jsonObject.getString("type")
                var index = jsonObject.getString("index")
                var indexOut = jsonObject.getJSONArray("juz").getJSONObject(0).getString("index")
                surahModel = QuranIndexModel(
                    place, type, count.toInt(), title, titleAr,
                    index, null, page,indexOut, null
                )
                quranModelList.add(surahModel)
            }

            val adapter =
                vm.preference.getLanguage()?.let {
                    QuranJuzAdapter(it,quranModelList) { item, quranModelList ->

                    }
                }
            binding.quranIndexRecyclerView.adapter = adapter
            quranModelList = quranModelList.distinctBy { it.indexOut }.toMutableList()
            adapter?.submitList(quranModelList)

        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("jsonText", "error " + ex.message)

        }
    }

}

