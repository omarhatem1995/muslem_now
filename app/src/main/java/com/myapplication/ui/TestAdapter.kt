package com.myapplication.ui

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.QuranVersesEntity
import java.net.URLEncoder
import android.graphics.Typeface
import android.text.Html


class TestAdapter(mContext: Context, dataItem: List<QuranVersesEntity>) : RecyclerView.Adapter<TestAdapter.AzkarViewHolder>() {

    var mContext: Context? = mContext
    var mData: List<QuranVersesEntity> = dataItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzkarViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.quran_item, parent, false)
        return AzkarViewHolder(v)
    }

    override fun onBindViewHolder(holder: AzkarViewHolder, position: Int) {
        val rowItem = mData!![position]
        val customTypeface = Typeface.createFromAsset(mContext?.assets, "p1.ttf")
        holder.textAzkar.text = rowItem.mobileCode

//        /*   holder.textAzkar.setTypeface(mContext?.let { ResourcesCompat.getFont(it, R.font.p1) })
//        URLEncoder.encode(holder.textAzkar.getText().toString(),"UTF-8")*/
//        val font = Typeface.createFromAsset(mContext?.assets, "p1.ttf")
        holder.textAzkar.typeface = customTypeface
        Log.d("lopgatItem", " ${rowItem.unicode} , ${rowItem.text} , ${rowItem.aya}")
    /*    holder.imageAzkar.setOnClickListener {
            val intent = Intent(mContext, AzkarActivity::class.java)
            intent.putExtra("category" , zekrItem)
            intent.flags = FLAG_ACTIVITY_SINGLE_TOP
            mContext?.startActivity(intent)
        }*/
    }


    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    class AzkarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textAzkar: TextView

        init {
            textAzkar = itemView.findViewById(R.id.aya)

        }
    }
}