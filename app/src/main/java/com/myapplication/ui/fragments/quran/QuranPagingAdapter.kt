package com.myapplication.ui.fragments.quran

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranItemBinding
import com.myapplication.databinding.QuranLinesRecyclerBinding

class QuranPagingAdapter(val context:Context):ListAdapter<QuranPage,QuranPagingAdapter.QuranAyaViewHolder>(DiffCallBack) {


    private var pageNumber = 1
    private val linesAdapter:QuranLinesAdapter = QuranLinesAdapter(context)
   inner class QuranAyaViewHolder(val binding:QuranLinesRecyclerBinding):RecyclerView.ViewHolder(binding.root)
    {



        @SuppressLint("SetTextI18n")
        fun onBind(position: Int)

        {



            val list = currentList
            binding.lineRecycler.adapter = linesAdapter

            var lineNum = 1
            var quranList : QuranPage
          //  Log.e(null, "onBindViewHolder: $list ,position :$position ", )
            if (list.isNotEmpty())
            {
                lineNum = list[position].lines!!


                for (number in 2..lineNum)
                {
                   val filteredByLine = list[position].versesList?.filter {

                       it.line == number

                   }

                    Log.e("QuranPagingAdapter", "onBind: $filteredByLine", )
                    if (filteredByLine?.isNotEmpty()!!)
                    {
                        linesAdapter.submitList(filteredByLine)
                    }





                }
            }



                }





           //binding.quranLine.addView()

           // val  typeface:Typeface = ResourcesCompat.getFont(context, R.font.p1)!!
           // binding.aya.typeface = typeface

           // Log.e(null, "onBind: $kelma", )
               // Html.fromHtml("\\u$kelma")




    }

    override fun onBindViewHolder(holder: QuranAyaViewHolder, position: Int) {


            holder.onBind(position)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuranAyaViewHolder {
        val binding = QuranLinesRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return QuranAyaViewHolder(binding)
    }


    companion object DiffCallBack: DiffUtil.ItemCallback<QuranPage>() {
        override fun areItemsTheSame(
            oldItem: QuranPage,
            newItem:QuranPage
        ): Boolean {
         return   if (!oldItem.versesList.isNullOrEmpty()&&!newItem.versesList.isNullOrEmpty())
          return oldItem.page == newItem.page
            else
                true
        }

        override fun areContentsTheSame(
            oldItem: QuranPage,
            newItem: QuranPage
        ): Boolean {
            return   if (!oldItem.versesList.isNullOrEmpty()&&!newItem.versesList.isNullOrEmpty())
                return oldItem== newItem
            else
                true
        }


    }

    fun getPage(page:Int)
    {
        this.pageNumber = page
    }

}