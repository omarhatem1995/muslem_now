package com.myapplication.ui.azkar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.AlAzkarListModel

class AzkarListAdapter(mContext: Context, dataItem: List<String>) : RecyclerView.Adapter<AzkarListAdapter.AzkarViewHolder>() {

    var mContext: Context? = mContext
    var mData: List<String> = dataItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzkarViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.azkar_list_item, parent, false)
        return AzkarViewHolder(v)
    }

    override fun onBindViewHolder(holder: AzkarViewHolder, position: Int) {
        val zekrItem = mData!![position]

        holder.azkarName.text = zekrItem
    }


    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    class AzkarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var azkarName: TextView

        init {
            azkarName = itemView.findViewById(R.id.azkar_name)
            Log.d("languageList" , " view Holder")

        }
    }
}