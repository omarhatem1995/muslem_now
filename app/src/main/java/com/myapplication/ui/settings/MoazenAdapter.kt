package com.myapplication.ui.settings

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.myapplication.R
import com.myapplication.data.entities.model.MoazenModel

class MoazenAdapter(context: Context, moazen: ArrayList<MoazenModel>) : BaseAdapter() {
    var context = context
    var moazen = moazen
    override fun getCount(): Int {
        return moazen.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var rootView : View = LayoutInflater.from(context).
        inflate(R.layout.item_moazen,p2,false)

        var textView : TextView = rootView.findViewById(R.id.moazenTextView)
//        var imageView : ImageView = rootView.findViewById(R.id.languageImage)

        textView.text = moazen[p0].name
        Log.d("textView.text" , textView.text.toString())
//        imageView.setImageResource(languages[p0].image)

        return rootView
    }
}