package com.myapplication.ui.fragments.quran

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranItemBinding
import com.myapplication.databinding.WordItemBinding
import java.util.*


class QuranLinesAdapter(val context: Context):ListAdapter<QuranVersesEntity,QuranLinesAdapter.QuranLinesViewHolder>(DiffCallBack) {




    inner class QuranLinesViewHolder(private val binding:QuranItemBinding): RecyclerView.ViewHolder(binding.root)
    {
        fun onBind(entity:QuranVersesEntity)
        {
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



            val kelma = entity.mobileCode
                val text = TextView(context)
                val  typeface: Typeface = ResourcesCompat.getFont(context, R.font.p1)!!
                text.text= kelma
                // Log.e(null, "onBind: $kelma", )
                text.typeface = typeface
            binding.quranLine.addView(text)
//           list.forEach {
//               val view = itemInflator()
//               wordItemBinding.quranWord.text = it.mobileCode
//               val  typeface: Typeface = ResourcesCompat.getFont(context, R.font.p1)!!
//               wordItemBinding.quranWord.typeface = typeface
//               binding.quranLine.addView(wordItemBinding.root)
//
//           }
           // val view = itemInflator()


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuranLinesViewHolder {
        val binding = QuranItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)

        return QuranLinesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuranLinesViewHolder, position: Int) {

        val line = currentList[position]

        Log.d("lines", "onBindViewHolder: $line")

        holder.onBind(line)
    }



    companion object DiffCallBack: DiffUtil.ItemCallback<QuranVersesEntity>() {
        override fun areItemsTheSame(
            oldItem: QuranVersesEntity,
            newItem: QuranVersesEntity
        ): Boolean {

                return oldItem.id == newItem.id



        }

        override fun areContentsTheSame(
            oldItem: QuranVersesEntity,
            newItem: QuranVersesEntity
        ): Boolean {

                return oldItem == newItem


        }


    }

//    fun itemInflator():View
//    {
//        val view = View.inflate(this.context, R.layout.word_item, null)
//        wordItemBinding = DataBindingUtil.bind(view)!!
//        return view
//    }
}