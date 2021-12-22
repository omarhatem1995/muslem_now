package com.myapplication.ui.azkar

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import org.json.JSONArray
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.databinding.ActivityZekrBinding
import java.io.*
import java.lang.StringBuilder
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AzkarActivity : AppCompatActivity() {

    lateinit var recyclerViewAzkar: RecyclerView
    lateinit var zekrCategory: TextView
    lateinit var binding: ActivityZekrBinding
    private val viewModel: AzkarViewModel by viewModels()
    lateinit var recyclerViewState: Parcelable


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zekr)
        binding.azkar = viewModel
        binding.lifecycleOwner = this
        recyclerViewAzkar = findViewById<RecyclerView>(R.id.azkar_recyclerView_List)
        zekrCategory = findViewById<TextView>(R.id.azkarCategory)
        recyclerViewState = binding.azkarRecyclerViewList.layoutManager?.onSaveInstanceState()!!
        loadJSONFromAsset()
/*        viewModel.update(AzkarModel(1,"asdasd","asdasd","sdlsd",
        2,3,"sdsd","21-12-2021"))*/
    }

    fun fileExists(context: Context, filename: String?): Boolean {
        val file = context.getFileStreamPath(filename)
        return if (file == null || !file.exists()) {
            Log.d("filesasdlkasd", " doesnt exists")
            false
        } else {
            Log.d("filesasdlkasd", " exists")
            true
        }
    }

    fun readInput(id: Int): String {
        var ret = ""
        if (fileExists(this, "savedAzkar.json")) {

            try {
                val inputStream: InputStream = this.openFileInput("savedAzkar.json")
                if (inputStream != null) {
                    val inputStreamReader = InputStreamReader(inputStream)
                    val bufferedReader = BufferedReader(inputStreamReader)
                    var receiveString: String? = ""
                    val stringBuilder = StringBuilder()
                    while (bufferedReader.readLine().also { receiveString = it } != null) {
                        stringBuilder.append("\n").append(receiveString)
                        Log.d("foundLoasdkasd", "found: " + receiveString)
                        val obj = JSONObject(receiveString)
                        if (id == obj.getString("id").toInt()) {
                            ret = obj.getString("counter")
                        } else {
                            ret = "300"
                        }
                        Log.d("foundLoasdkasd", "found: " + obj.getString("id"))
                        Log.d("foundLoasdkasd", "found: " + obj.getString("counter"))
                    }

                    inputStream.close()
//                ret = stringBuilder.toString()
                }
            } catch (e: FileNotFoundException) {
                Log.e("login activity", "File not found: " + e.toString())
            } catch (e: IOException) {
                Log.e("login activity", "Can not read file: $e")
            }
        }
        return ret
    }

    lateinit var localTime: String
    val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
    var flagPress = true
    val updateAzkarModel = arrayListOf<AzkarModel>()
    fun loadJSONFromAsset() {
        val georgianDateFormatForInsertion: DateFormat =
            SimpleDateFormat("dd-MM-yyyy", Locale("en"))
        localTime = georgianDateFormatForInsertion.format(cal.time)
        val dateSaved = viewModel.preference.getDate()
        val name = intent.getStringExtra("category")
        zekrCategory.text = name
        if (dateSaved == null || dateSaved?.equals(localTime) != true) {
            viewModel.preference.setDate(localTime)
            var json: String? = null
            var json2: String? = null
            try {
                val inputStream: InputStream = assets.open("azkar.json")
                json = inputStream.bufferedReader().use { it.readText() }
                var jsonArray = JSONArray(json)
                val azkar: MutableList<AzkarModel> = ArrayList()
                val allAzkar: MutableList<AzkarModel> = ArrayList()
                for (i in 0..jsonArray.length() - 1) {

                    var jsonObj = jsonArray.getJSONObject(i)
                    val zekrCount = jsonObj.getString("count")
                    var zekrCountInteger = 0
                    if (!zekrCount.equals(""))
                        zekrCountInteger = zekrCount.toInt()
                    val zekr = AzkarModel(
                        jsonObj.getString("id").toInt(),
                        jsonObj.getString("zekr"),
                        jsonObj.getString("description"),
                        jsonObj.getString("category"),
                        zekrCountInteger,
                        jsonObj.getString("userPressedCount").toInt(),
                        jsonObj.getString("reference"),
                        localTime
                    )
                    allAzkar.add(zekr)
                    if (jsonObj.getString("category").equals(name)) {
                        azkar.add(zekr)
                    }
                    val adapter =  AzkarListAdapter2 { type, data ->
                        when (type) {
                            Constants.INCREASEADAPTER -> {
                                        updateAzkarModel.add(data)
                            }
                            Constants.RESETADAPTER -> {
                                updateAzkarModel.add(data)
                                viewModel.update(2, 3, updateAzkarModel)
                            }
                        }
                    }
                    adapter.submitList(azkar)
                    binding.azkarRecyclerViewList.adapter = adapter
                }
                viewModel.saveAzkar(this, allAzkar)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        } else {
            if (name != null) {
                viewModel.getSpecificDayAzkar(name).observe(this, { it ->
                    if (!it.isNullOrEmpty()) {
                        val adapter = AzkarListAdapter2 { type, data ->
                            when (type) {
                                Constants.INCREASEADAPTER -> {
                                    updateAzkarModel.add(data)
                                }
                                Constants.RESETADAPTER -> {
                                    updateAzkarModel.add(data)
                                    viewModel.update(2, 3, updateAzkarModel)
                                }
                            }
                        }
                            adapter.submitList(it)
                            binding.azkarRecyclerViewList.adapter = adapter
                            binding.azkarRecyclerViewList.adapter?.stateRestorationPolicy =
                                RecyclerView.Adapter.StateRestorationPolicy.ALLOW
                            binding.azkarRecyclerViewList.getLayoutManager()
                                ?.onRestoreInstanceState(recyclerViewState)
                    }

                })

            }

        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.update(2, 3, updateAzkarModel)
    }
}