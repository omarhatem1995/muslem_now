package com.myapplication.ui.home

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.PrayerTimeModel

class PrayerAdapter(mContext: Context, dataItem: List<PrayerTimeModel?>?) : RecyclerView.Adapter<PrayerAdapter.PrayerViewHolder>() {

    var mContext: Context? = mContext
    var mData: List<PrayerTimeModel?>? = dataItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.prayer_item, parent, false)
        Log.d("languageList" , " view Holder")
        return PrayerViewHolder(v)
    }

    override fun onBindViewHolder(holder: PrayerViewHolder, position: Int) {
        val prayerItem = mData!![position]
        Log.d("languageList", " mData : " + prayerItem?.name)

        if (prayerItem != null) {
            holder.imagePrayer.setBackgroundResource(prayerItem.image)
            holder.imageStatus.setBackgroundResource(prayerItem.imageStatus)
        }
        holder.namePrayer.setText(prayerItem?.name)
        holder.timePrayer.setText(prayerItem?.time)
    }


    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    class PrayerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imagePrayer: ImageView
        var imageStatus: ImageView
        var namePrayer: TextView
        var timePrayer: TextView

        init {
            imagePrayer = itemView.findViewById(R.id.icon_image)
            namePrayer = itemView.findViewById(R.id.prayer_name)
            imageStatus = itemView.findViewById(R.id.icon_status_image)
            timePrayer = itemView.findViewById(R.id.prayer_time)
            Log.d("languageList" , " view Holder")

        }
    }
}