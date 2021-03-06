package com.myapplication.ui.fragments.azkar

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.myapplication.R
import com.myapplication.databinding.FragmentAzkarBinding
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

/**
 * A simple [Fragment] subclass.
 * Use the [AzkarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class AzkarFragment : Fragment() {

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
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                searchQuery: CharSequence,
                start: Int,
                before: Int,
                count: Int
            ) {
                handleSearchOnTextChange(searchQuery)
            }

            override fun afterTextChanged(s: Editable) {}
        })
        loadJSONFromAsset()
        return binding.root
    }

    private fun handleSearchOnTextChange(searchQuery: CharSequence) {
        if (searchQuery.isNotEmpty()) {
            val filteredList = azkarCategoriesList.filter { p ->
                p.replace("أ","ا").contains(searchQuery, true)
            }.toList()
            loadAdapter(filteredList)
        }

        }
    var azkarCategoriesList : MutableList<String> = ArrayList()

    private fun loadJSONFromAsset() {
        var json: String? = null
        try {
            val inputStream: InputStream? = context?.assets?.open("categories.json")
            json = inputStream?.bufferedReader().use { it?.readText() }

            var jsonArray = JSONArray(json)

            for (i in 0 until jsonArray.length()) {
                var jsonObj = jsonArray.getString(i)
                azkarCategoriesList.add(jsonObj)
                loadAdapter(azkarCategoriesList)
            }
        } catch (ex: IOException) {
            ex.printStackTrace()

        }
    }

    private fun loadAdapter(list:List<String>){
        binding.azkarRecyclerview.adapter = AzkarAdapter(requireContext(),list)

    }



}