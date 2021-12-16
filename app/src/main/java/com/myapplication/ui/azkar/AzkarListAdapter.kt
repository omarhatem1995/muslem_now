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
import com.myapplication.data.entities.model.AzkarModel

class AzkarListAdapter(mContext: Context, dataItem: List<AzkarModel>) : RecyclerView.Adapter<AzkarListAdapter.AzkarViewHolder>() {

    var mContext: Context? = mContext
    var mData: List<AzkarModel> = dataItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzkarViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.azkar_list_item, parent, false)
        return AzkarViewHolder(v)
    }

    override fun onBindViewHolder(holder: AzkarViewHolder, position: Int) {
        val zekrItem = mData!![position]
        Log.d("languageList" , " view Holder" + zekrItem)

        holder.azkarName.text = zekrItem.category
        holder.azkarDescription.text = zekrItem.desciption
        holder.count.text = zekrItem.count
        var actualCount: Int
        var counter : Int
        if(zekrItem.count.equals("")){
            counter = 0
            actualCount = 0
        }else {
            actualCount = zekrItem.count.toInt()
            counter = zekrItem.count.toInt()
        }
        holder.countImageView.setOnClickListener {
            counter -= 1
            holder.count.text = counter.toString()
            if (counter < 1) {
                holder.countImageView.setImageResource(R.drawable.finished_zekr_count)
                holder.count.visibility = View.GONE
            }
        }
        holder.reloadImageView.setOnClickListener {
            counter = actualCount
            holder.countImageView.setImageResource(R.drawable.azkar_circle)
            holder.count.visibility = View.VISIBLE
            holder.count.text = actualCount.toString()
        }
    }


    override fun getItemCount(): Int {
        return if (mData != null) mData!!.size else 0
    }

    class AzkarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var azkarName: TextView
        var azkarDescription : TextView
        var count : TextView
        var countImageView : ImageView
        var reloadImageView : ImageView
        init {
            azkarName = itemView.findViewById(R.id.azkar_name)
            azkarDescription = itemView.findViewById(R.id.azkarDescription)
            count = itemView.findViewById(R.id.azkarCount)
            countImageView = itemView.findViewById(R.id.azkarCircle)
            reloadImageView = itemView.findViewById(R.id.azkarReloadImageView)

        }
    }
}