package com.myapplication.ui.azkar

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.myapplication.LocaleUtil
import com.myapplication.LocaleUtil.Companion.writeToFile
import com.myapplication.R
import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.data.gateways.dao.alazkargateway.AlAzkarDao
import org.json.JSONObject

class AzkarListAdapter(mContext: Context, dataItem: List<AzkarModel> , userPressed: UserPressed) : RecyclerView.Adapter<AzkarListAdapter.AzkarViewHolder>() {

    var mContext: Context? = mContext
    var mData: List<AzkarModel> = dataItem
    var userPressed : UserPressed = userPressed

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AzkarViewHolder {
        val v: View
        v = LayoutInflater.from(mContext).inflate(R.layout.azkar_list_item, parent, false)
        return AzkarViewHolder(v)
    }

    override fun onBindViewHolder(holder: AzkarViewHolder, position: Int) {
        val zekrItem = mData!![position]
        Log.d("languageList" , " view Holder" + zekrItem)

        holder.azkarName.text = zekrItem.zekr
        holder.azkarDescription.text = zekrItem.description
        holder.count.text = zekrItem.count.toString()
        var counterPressed = zekrItem.userPressedCount
        var actualCount: Int
        var counter : Int
        var zekrItemId = zekrItem.id
        if(zekrItem.count!!.equals("")){
            counter = 0
            actualCount = 0
        }else {
            actualCount = zekrItem.count.toInt() - counterPressed
            if (actualCount < 1) {
                holder.countImageView.setImageResource(R.drawable.finished_zekr_count)
                holder.count.visibility = View.GONE
            }
            counter = zekrItem.count.toInt()
            holder.count.text = actualCount.toString()
        }
        holder.countImageView.setOnClickListener {
            if(counter ==0)

            else {
                counter -= 1
                counterPressed += 1
                holder.count.text = counter.toString()
                if (counter < 1) {
                    holder.countImageView.setImageResource(R.drawable.finished_zekr_count)
                    holder.count.visibility = View.GONE
                }
            }
            Log.d("userPressed ", "Item ID : " + zekrItemId + " ," + counterPressed)
//            userPressed.userPressed(zekrItemId,counterPressed)
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