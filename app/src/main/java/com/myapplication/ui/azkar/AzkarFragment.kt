package com.myapplication.ui.azkar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.LocaleUtil
import com.myapplication.R
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

/**
 * A simple [Fragment] subclass.
 * Use the [AzkarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class AzkarFragment : Fragment() {

    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerViewAzkar : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        context?.let { LocaleUtil.applyLocalizedContext(it,"ar") }
        var view : View = inflater.inflate(R.layout.fragment_azkar, container, false)
        recyclerViewAzkar = view.findViewById<RecyclerView>(R.id.azkar_recyclerview)

        layoutManager  = LinearLayoutManager(requireContext())
        loadJSONFromAsset()

        return view
    }

    fun loadJSONFromAsset() {
        var json: String? = null
        try {
            val inputStream: InputStream? = context?.assets?.open("categories.json")
            json = inputStream?.bufferedReader().use { it?.readText() }

            var jsonArray = JSONArray(json)

            var strings : MutableList<String> = ArrayList()
            for (i in 0..jsonArray.length()-1) {
                var jsonObj = jsonArray.getString(i)
                strings.add(jsonObj)
                recyclerViewAzkar.adapter = AzkarAdapter(requireContext(),strings)
            }
            recyclerViewAzkar.layoutManager =layoutManager
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("jsonText", "error " + ex.message)

        }
    }



}