package com.zaker.ui.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.zaker.R
import com.zaker.data.entities.model.googleplaces.Result

class MosquesAdapter(mContext: Context, dataItem: List<Result>) :
    RecyclerView.Adapter<MosquesAdapter.MosquesViewHolder>() {
    var mContext: Context? = mContext
    private var mData: List<Result> = dataItem
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MosquesViewHolder {
        val v: View = LayoutInflater.from(mContext).inflate(R.layout.mosque_item, parent, false)
        return MosquesViewHolder(v)
    }

    override fun onBindViewHolder(holder: MosquesViewHolder, position: Int) {
        val mosqueItem = mData[position]
        holder.textNameOfMosque.text = mosqueItem.name
        holder.textAddressOfMosque.text = mosqueItem.vicinity

        holder.imageMosqueDirection.setOnClickListener {
            val gmmIntentUri = Uri.parse("google.navigation:q=${mosqueItem.geometry?.location?.lat},${mosqueItem.geometry?.location?.lng}")

            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            mContext?.startActivity(mapIntent)
        }
    }


    override fun getItemCount(): Int {
        return mData.size
    }

    class MosquesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textNameOfMosque: TextView = itemView.findViewById(R.id.mosqueName)
        var textAddressOfMosque: TextView = itemView.findViewById(R.id.mosqueAddress)
        var imageMosqueDirection: ImageView = itemView.findViewById(R.id.mosqueDirection)

    }
}