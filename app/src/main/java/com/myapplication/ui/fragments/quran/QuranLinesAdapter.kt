package com.myapplication.ui.fragments.quran

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranItemBinding


class QuranLinesAdapter(val context: Context) :
    ListAdapter<List<QuranVersesEntity>, QuranLinesAdapter.QuranLinesViewHolder>(DiffCallBack) {


    inner class QuranLinesViewHolder(private val binding: QuranItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(entities: List<QuranVersesEntity>) {
            //Log.e("QuranLinesAdapter", "onBind:$list ")
            Log.e("QuranLinesAdapter", "currentList:$currentList ")
//           val newList = list.map {
//
//                val kelma = it.mobileCode
//                val text = TextView(context)
//                val  typeface: Typeface = ResourcesCompat.getFont(context, R.font.p1)!!
//                text.text= kelma
//                // Log.e(null, "onBind: $kelma", )
//                text.typeface = typeface
//               text.id = View.generateViewId()
//                return
//
//            }
            var typeface: Typeface?
            entities.forEach {
                val kelma = it.mobileCode
                val text = TextView(context)
                var pageNumber = it.page
//                if (it.page == 1)
//                {
                    typeface = Typeface.createFromAsset(context.assets,"p$pageNumber.ttf")!!
                /*}else if (it.page == 2)
                {
                    typeface = ResourcesCompat.getFont(context, R.font.p2)!!

                }else if (it.page == 3)
                {
                    typeface = ResourcesCompat.getFont(context, R.font.p3)!!
                }else if (it.page == 4)
                {
                    typeface = ResourcesCompat.getFont(context, R.font.p4)!!
                }else
                {
                    typeface = ResourcesCompat.getFont(context, R.font.p5)!!
                }*/

                text.text = kelma
                // Log.e(null, "onBind: $kelma", )
                text.typeface = typeface
                //text.gravity = Gravity.CENTER_HORIZONTAL
                text.setTextSize(TypedValue.COMPLEX_UNIT_SP,22f)
                binding.quranLine.addView(text)
            }




        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuranLinesViewHolder {
        val binding = QuranItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return QuranLinesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuranLinesViewHolder, position: Int) {

        val line = currentList[position]

        Log.d("lines", "onBindViewHolder: $line")
//        Toast.makeText(context, line.text, Toast.LENGTH_LONG).show()
        holder.onBind(line)
    }


    companion object DiffCallBack : DiffUtil.ItemCallback<List<QuranVersesEntity>>() {
        override fun areItemsTheSame(
            oldItem: List<QuranVersesEntity>,
            newItem: List<QuranVersesEntity>
        ): Boolean {

            return oldItem.last().id == newItem.last().id


        }

        override fun areContentsTheSame(
            oldItem: List<QuranVersesEntity>,
            newItem: List<QuranVersesEntity>
        ): Boolean {

            return oldItem == newItem


        }


    }


}