package com.myapplication.ui.azkar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.AlAzkarListModel

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

        val supplierNames1: MutableList<AlAzkarListModel> = ArrayList()
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
        supplierNames1.add(10,doaaElSogoud)
        recyclerViewAzkar.adapter = AzkarAdapter(requireContext(),supplierNames1)
        recyclerViewAzkar.layoutManager =layoutManager

        return view
    }



}