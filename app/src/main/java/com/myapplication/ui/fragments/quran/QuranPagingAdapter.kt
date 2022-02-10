package com.myapplication.ui.fragments.quran

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranItemBinding

class QuranPagingAdapter(val context:Context):PagingDataAdapter<QuranVersesEntity,QuranPagingAdapter.QuranAyaViewHolder>(DiffCallBack) {


   inner class QuranAyaViewHolder(val binding:QuranItemBinding):RecyclerView.ViewHolder(binding.root)
    {

        @SuppressLint("SetTextI18n")
        fun onBind(quranVersesEntity: QuranVersesEntity)

        {

            val kelma = quranVersesEntity.unicode
            binding.aya.setText(Html.fromHtml("$kelma"))


           // val  typeface:Typeface = ResourcesCompat.getFont(context, R.font.p1)!!
           // binding.aya.typeface = typeface

            Log.e("LogcatApplicaiton", "onBind: $kelma", )

        }
    }

    override fun onBindViewHolder(holder: QuranAyaViewHolder, position: Int) {
        val aya =getItem(position)

        if(aya?.line != 1)

        if (aya != null) {
            holder.onBind(aya)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuranAyaViewHolder {
        val binding = QuranItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return QuranAyaViewHolder(binding)
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
          return  oldItem == newItem
        }


    }
}