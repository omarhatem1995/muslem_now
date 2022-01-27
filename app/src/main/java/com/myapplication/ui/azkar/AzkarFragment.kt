package com.myapplication.ui.azkar

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
import com.myapplication.databinding.FragmentAzkarBinding
import com.myapplication.databinding.FragmentHomeBinding
import com.myapplication.ui.fragments.home.HomeViewModel
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

/**
 * A simple [Fragment] subclass.
 * Use the [AzkarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class AzkarFragment : Fragment() {

    lateinit var recyclerViewAzkar : RecyclerView
    private val vm: AzkarViewModel by viewModels()
    lateinit var binding: FragmentAzkarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_azkar, container, false
        )
        binding.azkarFragment = vm

     /*   vm.preference.getLanguage()?.let { context?.let { it1 ->
            LocaleUtil.applyLocalizedContext(
                it1,
                it
            )
        } }*/

        loadJSONFromAsset()
        return binding.root
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
                binding.azkarRecyclerview.adapter = AzkarAdapter(requireContext(),strings)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("jsonText", "error " + ex.message)

        }
    }



}