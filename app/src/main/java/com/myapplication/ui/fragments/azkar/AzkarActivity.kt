package com.myapplication.ui.fragments.azkar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import org.json.JSONArray
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.myapplication.MainActivity
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.databinding.ActivityZekrBinding
import kotlinx.coroutines.InternalCoroutinesApi
import java.io.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class AzkarActivity : AppCompatActivity() {

    lateinit var binding: ActivityZekrBinding
    private val viewModel: AzkarViewModel by viewModels()
    lateinit var recyclerViewState: Parcelable
    lateinit var localTime: String
    private val cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+1:00"))
    private val updateAzkarModel = arrayListOf<AzkarModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_zekr)
        binding.azkar = viewModel
        binding.lifecycleOwner = this
        recyclerViewState = binding.azkarRecyclerViewList.layoutManager?.onSaveInstanceState()!!
        initAzkar()
    }

    fun initAzkar() {
        val georgianDateFormatForInsertion: DateFormat =
            SimpleDateFormat("dd-MM-yyyy", Locale("en"))
        localTime = georgianDateFormatForInsertion.format(cal.time)
        val dateSaved = viewModel.preference.getDate()
        val name = intent.getStringExtra("category")
        binding.azkarCategory.text = name
        if (dateSaved == null || !dateSaved?.equals(localTime)) {
            viewModel.preference.setDate(localTime)
            var json: String? = null
            try {
                val inputStream: InputStream = assets.open("azkar.json")
                json = inputStream.bufferedReader().use { it.readText() }
                var jsonArray = JSONArray(json)
                val azkar: MutableList<AzkarModel> = ArrayList()
                val allAzkar: MutableList<AzkarModel> = ArrayList()
                for (i in 0 until jsonArray.length()) {

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
                    val adapter =  AzkarListAdapter { type, data ->
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
                viewModel.getSpecificDayAzkar(name)
                    viewModel.viewStateAzkar.observe(this, {
                        if (it != null) {
                        val adapter = AzkarListAdapter { type, data ->
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
                            binding.azkarRecyclerViewList.layoutManager
                                ?.onRestoreInstanceState(recyclerViewState)
                    }

                })

            }

        }
    }

    @InternalCoroutinesApi
    override fun onBackPressed() {
        super.onBackPressed()
        if(isTaskRoot) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.update(2, 3, updateAzkarModel)
    }
}