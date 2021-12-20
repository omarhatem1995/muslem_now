package com.myapplication.ui.azkar

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R




class AzkarAdapter(mContext: Context, dataItem: List<String>) : RecyclerView.Adapter<AzkarAdapter.AzkarViewHolder>() {

    var mContext: Context? = mContext
    var mData: List<String> = dataItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzkarViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.azkar_item, parent, false)
        return AzkarViewHolder(v)
    }

    override fun onBindViewHolder(holder: AzkarViewHolder, position: Int) {
        val zekrItem = mData!![position]

        holder.textAzkar.text = zekrItem

        holder.imageAzkar.setOnClickListener {
            val intent = Intent(mContext, AzkarActivity::class.java)
            intent.putExtra("category" , zekrItem)
            mContext?.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    class AzkarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageAzkar: ImageView
        var textAzkar: TextView

        init {
            imageAzkar = itemView.findViewById(R.id.zekrBackgroundImageView)
            textAzkar = itemView.findViewById(R.id.zekrTextView)

        }
    }
}