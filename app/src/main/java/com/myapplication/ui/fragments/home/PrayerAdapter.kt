package com.myapplication.ui.fragments.home

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.PrayerTimeModel
import androidx.core.graphics.drawable.DrawableCompat

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.myapplication.LocaleUtil.Companion.getNameOfPrayer
import com.myapplication.LocaleUtil.Companion.getTimeAMandPM


class PrayerAdapter(mContext: Context, dataItem: List<PrayerTimeModel?>? , nextPrayerIs : Int) :
    RecyclerView.Adapter<PrayerAdapter.PrayerViewHolder>() {

    var mContext: Context = mContext
    var mData: List<PrayerTimeModel?>? = dataItem
    var nextPrayerIs = nextPrayerIs

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.prayer_item, parent, false)
        return PrayerViewHolder(v)
    }

    override fun onBindViewHolder(holder: PrayerViewHolder, position: Int) {
        val prayerItem = mData!![position]

        if (prayerItem != null) {
            holder.imagePrayer.setBackgroundResource(prayerItem.image)
            if(prayerItem.imageStatus == 1)
            holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
            else
                holder.imageStatus.setBackgroundColor(R.drawable.ic_volume_mute)

            if (prayerItem.prayerId == 3) {
                holder.imagePrayer.setBackgroundResource(prayerItem.image)
                holder.imagePrayer.setColorFilter(Color.BLACK)
                val unwrappedDrawable: Drawable = holder.imagePrayer.getBackground()
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    ContextCompat.getColor(mContext, R.color.textColorGreen2)
                )
            }
            Log.d("nextPrayerIs22" , " = " + prayerItem.date + " , " + prayerItem.name)
            if(prayerItem?.prayerId == nextPrayerIs){
                holder.namePrayer.setTextColor(ContextCompat.getColor(mContext,R.color.textSelectedPrayerColor))
                holder.timePrayer.setTextColor(ContextCompat.getColor(mContext,R.color.textSelectedPrayerColor))
                val unwrappedDrawable: Drawable = holder.imagePrayer.getBackground()
                val unwrappedDrawable2: Drawable = holder.imageStatus.getBackground()
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                val wrappedDrawable2 = DrawableCompat.wrap(unwrappedDrawable2)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    ContextCompat.getColor(mContext, R.color.textSelectedPrayerColor)
                )
                DrawableCompat.setTint(
                    wrappedDrawable2,
                    ContextCompat.getColor(mContext, R.color.textSelectedPrayerColor)
                )
            }else {
                val unwrappedDrawable: Drawable = holder.imagePrayer.getBackground()
                val unwrappedDrawable2: Drawable = holder.imageStatus.getBackground()
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                val wrappedDrawable2 = DrawableCompat.wrap(unwrappedDrawable2)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    ContextCompat.getColor(mContext, R.color.textColorGreen2)
                )
                DrawableCompat.setTint(
                    wrappedDrawable2,
                    ContextCompat.getColor(mContext, R.color.textColorGreen2)
                )
            }
        }

        holder.imageStatus.setOnClickListener {
            if (prayerItem!!.imageStatus == 1) {
                holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
            }else {
                holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
            }
            }

        if (prayerItem?.time != null)
            holder.timePrayer.text = getTimeAMandPM(prayerItem?.time, mContext)

        if (prayerItem?.prayerId != null)
            holder.namePrayer.text = getNameOfPrayer(mContext, prayerItem?.prayerId)
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
        }
    }
}