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
import com.myapplication.LocaleUtil.Companion.setDrawable


class PrayerAdapter(mContext: Context, dataItem: List<PrayerTimeModel?>?, nextPrayerIs: Int ,prayerSoundClickListener: PrayerSoundClickListener,
list: MutableList<Boolean>) :
    RecyclerView.Adapter<PrayerAdapter.PrayerViewHolder>() {

    var mContext: Context = mContext
    var mData: List<PrayerTimeModel?>? = dataItem
    var nextPrayerIs = nextPrayerIs
    var prayerSoundClickListener = prayerSoundClickListener
    var list = list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.prayer_item, parent, false)
        return PrayerViewHolder(v)
    }

    override fun onBindViewHolder(holder: PrayerViewHolder, position: Int) {
        val prayerItem = mData!![position]

        if (prayerItem != null) {
                if(prayerItem.prayerId == 0 && list[0]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
                    holder.imageStatus.tag = true    //When you change the drawable
                }else if(prayerItem.prayerId == 0 && !list[0]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
                    holder.imageStatus.tag = false    //When
                }else if(prayerItem.prayerId == 1 && list[1]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
                    holder.imageStatus.tag = true
                }else if(prayerItem.prayerId == 1 && !list[1]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
                    holder.imageStatus.tag = false
                }else if(prayerItem.prayerId == 2 && list[2]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
                    holder.imageStatus.tag = true
                }else if(prayerItem.prayerId == 2 && !list[2]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
                    holder.imageStatus.tag = false
                }else if(prayerItem.prayerId == 3 && list[3]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
                    holder.imageStatus.tag = true
                }else if(prayerItem.prayerId == 3 && !list[3]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
                    holder.imageStatus.tag = false
                }else if(prayerItem.prayerId == 4 && list[4]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
                    holder.imageStatus.tag = true
                }else if(prayerItem.prayerId == 4 && !list[4]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
                    holder.imageStatus.tag = false
                }else if(prayerItem.prayerId == 5 && list[5]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
                    holder.imageStatus.tag = true
                }else if(prayerItem.prayerId == 5 && !list[5]){
                    holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
                    holder.imageStatus.tag = false
                }
            holder.imagePrayer.setBackgroundResource(setDrawable(prayerItem.prayerId))

            if (prayerItem.prayerId == 3) {
                holder.imagePrayer.setBackgroundResource(R.drawable.ic_elasr)
                holder.imagePrayer.setColorFilter(Color.BLACK)
                val unwrappedDrawable: Drawable = holder.imagePrayer.getBackground()
                val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable)
                DrawableCompat.setTint(
                    wrappedDrawable,
                    ContextCompat.getColor(mContext, R.color.textColorGreen2)
                )
            }
            if (prayerItem?.prayerId == nextPrayerIs ) {
                holder.namePrayer.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.textSelectedPrayerColor
                    )
                )
                holder.timePrayer.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.textSelectedPrayerColor
                    )
                )
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
            } else {
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
        Log.d("lasdklasdklwe", " ${holder.imageStatus.tag}")

        holder.imageStatus.setOnClickListener {
            if (prayerItem != null) {
                prayerSoundClickListener.prayerId(prayerItem.prayerId)
            }
            if (holder.imageStatus.tag.toString().trim() == "true") {
                holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_mute)
                holder.imageStatus.tag = false
            } else {
                holder.imageStatus.setBackgroundResource(R.drawable.ic_volume_high)
                holder.imageStatus.tag = true
            if (prayerItem?.prayerId == nextPrayerIs ) {
                holder.namePrayer.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.textSelectedPrayerColor
                    )
                )
                holder.timePrayer.setTextColor(
                    ContextCompat.getColor(
                        mContext,
                        R.color.textSelectedPrayerColor
                    )
                )
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
            } else {
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