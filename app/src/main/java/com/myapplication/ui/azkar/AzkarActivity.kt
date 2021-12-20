package com.myapplication.ui.azkar

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import org.json.JSONArray
import java.io.IOException
import java.io.InputStream
import android.widget.TextView
import com.myapplication.data.entities.model.AzkarModel


class AzkarActivity : AppCompatActivity() {

    lateinit var linearLayoutManager: LinearLayoutManager
    lateinit var recyclerViewAzkar: RecyclerView
    lateinit var zekrCategory: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_zekr)
        recyclerViewAzkar = findViewById<RecyclerView>(R.id.azkar_recyclerView_List)
        zekrCategory = findViewById<TextView>(R.id.azkarCategory)

        linearLayoutManager = LinearLayoutManager(this)
//        readJsonAsset("azkar.json")

        loadJSONFromAsset()
    }


    fun loadJSONFromAsset() {
        var json: String? = null
        val name = intent.getStringExtra("category")
        zekrCategory.text = name
        try {
            val inputStream: InputStream = assets.open("azkar.json")
            json = inputStream.bufferedReader().use { it.readText() }

            Log.d("jsonText", " : "+json)
            var jsonArray = JSONArray(json)

            val supplierNames1: MutableList<AzkarModel> = ArrayList()
            for (i in 0..jsonArray.length()-1) {
                var jsonObj = jsonArray.getJSONObject(i)
                val zekr = AzkarModel(jsonObj.getString("zekr"),jsonObj.getString("description"),
                jsonObj.getString("count"))
                if (jsonObj.getString("category").equals(name)) {
                    supplierNames1.add(zekr)
                }
                recyclerViewAzkar.adapter = AzkarListAdapter(this, supplierNames1)
            }
            recyclerViewAzkar.layoutManager = linearLayoutManager
        } catch (ex: IOException) {
            ex.printStackTrace()
            Log.d("jsonText", "error " + ex.message)

        }
        /*val mFolder = File("$filesDir/azkar.json")
        val imgFile = File(mFolder.getAbsolutePath().toString() + "/someimage.png")
        if (!mFolder.exists()) {
            mFolder.mkdir()
        }
        if (!imgFile.exists()) {
            imgFile.createNewFile()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(imgFile)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }*/
    }
    @Throws(IOException::class)
    fun Context.readJsonAsset(fileName: String): String {
        val inputStream = assets.open(fileName)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        return String(buffer, Charsets.UTF_8)
    }
}