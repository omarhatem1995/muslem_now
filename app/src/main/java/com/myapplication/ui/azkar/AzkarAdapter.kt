package com.myapplication.ui.azkar

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.AlAzkarListModel

class AzkarAdapter(mContext: Context, dataItem: List<AlAzkarListModel>) : RecyclerView.Adapter<AzkarAdapter.AzkarViewHolder>() {

    var mContext: Context? = mContext
    var mData: List<AlAzkarListModel> = dataItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzkarViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.azkar_item, parent, false)
        Log.d("languageList" , " view Holder")
        return AzkarViewHolder(v)
    }

    override fun onBindViewHolder(holder: AzkarViewHolder, position: Int) {
        val zekrItem = mData!![position]

        holder.imageAzkar.setBackgroundResource(zekrItem.image)

        holder.imageAzkar.setOnClickListener {
            val intent = Intent(mContext, AzkarActivity::class.java)
            mContext?.startActivity(intent)
        }
    }


    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    class AzkarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageAzkar: ImageView

        init {
            imageAzkar = itemView.findViewById(R.id.azkar_image_item)
            Log.d("languageList" , " view Holder")

        }
    }
}