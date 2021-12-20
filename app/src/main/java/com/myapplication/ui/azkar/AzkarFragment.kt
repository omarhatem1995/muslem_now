package com.myapplication.ui.azkar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        var view : View = inflater.inflate(R.layout.fragment_azkar, container, false)
        recyclerViewAzkar = view.findViewById<RecyclerView>(R.id.azkar_recyclerview)

        layoutManager  = LinearLayoutManager(requireContext())
        loadJSONFromAsset()
        /*val supplierNames1: MutableList<AlAzkarListModel> = ArrayList()
        var azkarElSabah = AlAzkarListModel("أذكار الصباح", R.drawable.ic_image_qiblah)
        var azkarElMasaa = AlAzkarListModel("أذكار المساء", R.drawable.ic_image_qiblah)
        var azkarElEstikazMnElNoum = AlAzkarListModel("أذكار الاستيقاظ من النوم", R.drawable.ic_image_qiblah)
        var doaaLbsElThoubElGedid = AlAzkarListModel("دعاء لبس الثوب الجديد", R.drawable.ic_image_qiblah)
        var zekrAfterElWodoo = AlAzkarListModel("الذكر بعد الفراغ من الوضوء", R.drawable.ic_image_qiblah)
        var zekrElKhorougMnElMnzel = AlAzkarListModel("الذكر عند الخروج من المنزل", R.drawable.ic_image_qiblah)
        var azkarElAzan = AlAzkarListModel("أذكار الآذان", R.drawable.ic_image_qiblah)
        var doaaElEstftah = AlAzkarListModel("دعاء الاستفتاح", R.drawable.ic_image_qiblah)
        var doaaElRokoo3 = AlAzkarListModel("دعاء الركوع", R.drawable.ic_image_qiblah)
        var doaaElRaf3MnElRokoo3 = AlAzkarListModel("دعاء الرفع من الركوع", R.drawable.ic_image_qiblah)
        var doaaElSogoud = AlAzkarListModel("دعاء السجود", R.drawable.ic_image_qiblah)
        supplierNames1.add(0,azkarElSabah)
        supplierNames1.add(1,azkarElMasaa)
        supplierNames1.add(2,azkarElEstikazMnElNoum)
        supplierNames1.add(3,doaaLbsElThoubElGedid)
        supplierNames1.add(4,zekrAfterElWodoo)
        supplierNames1.add(5,zekrElKhorougMnElMnzel)
        supplierNames1.add(6,azkarElAzan)
        supplierNames1.add(7,doaaElEstftah)
        supplierNames1.add(8,doaaElRokoo3)
        supplierNames1.add(9,doaaElRaf3MnElRokoo3)
        supplierNames1.add(10,doaaElSogoud)*/

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