package com.myapplication.ui.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R

class NamesOfAllahAdapter(mContext: Context, dataItem: List<String>) :
    RecyclerView.Adapter<NamesOfAllahAdapter.NamesOfAllahViewHolder>() {
    var mContext: Context? = mContext
    var mData: List<String> = dataItem
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NamesOfAllahViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.name_of_allah_item, parent, false)
        return NamesOfAllahViewHolder(v)
    }

    override fun onBindViewHolder(holder: NamesOfAllahViewHolder, position: Int) {
        val nameItem = mData!![position]
        holder.textNameOfAllah.text = nameItem

    }


    override fun getItemCount(): Int {
        return mData!!.size
    }

    class NamesOfAllahViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textNameOfAllah: TextView = itemView.findViewById(R.id.nameOfAllahTextView)

    }
}