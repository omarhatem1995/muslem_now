package com.myapplication.ui.settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.myapplication.R
import com.myapplication.data.entities.model.Languages

class LanguagesAdapter(context: Context, languages: ArrayList<Languages>) : BaseAdapter() {
    var context = context
    var languages = languages
    override fun getCount(): Int {
        return languages.size
    }

    override fun getItem(i: Int): Any {
        return i
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        var rootView : View = LayoutInflater.from(context).
        inflate(R.layout.item_language,p2,false)

        var textView : TextView = rootView.findViewById(R.id.languageTextView)
        var imageView : ImageView = rootView.findViewById(R.id.languageImage)

        textView.text = languages[p0].name
        imageView.setImageResource(languages[p0].image)

        return rootView
    }
}