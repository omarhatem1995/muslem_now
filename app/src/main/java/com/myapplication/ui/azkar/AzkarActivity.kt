package com.myapplication.ui.azkar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.AlAzkarListModel
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream

class AzkarActivity : AppCompatActivity() {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var recyclerViewAzkar: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zekr)
        recyclerViewAzkar = findViewById<RecyclerView>(R.id.azkar_recyclerView_List)

        linearLayoutManager = LinearLayoutManager(this)
        loadJSONFromAsset()
    }


    fun loadJSONFromAsset() {
        var json: String? = null

        try {
            val inputStream: InputStream = assets.open("azkar.json")
            json = inputStream.bufferedReader().use { it.readText() }

            Log.d("jsonText", json)
            var jsonArray = JSONArray(json)

            val supplierNames1: MutableList<String> = ArrayList()
            for (i in 0..jsonArray.length()-1) {
                var jsonObj = jsonArray.getJSONObject(i)
                if (jsonObj.getString("category").equals("أذكار الصباح")) {
                    supplierNames1.add(jsonObj.getString("zekr"))
                }
                recyclerViewAzkar.adapter = AzkarListAdapter(this, supplierNames1)
            }
            recyclerViewAzkar.layoutManager = linearLayoutManager
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("jsonText", "error" + ex.printStackTrace())

        }
    }
}