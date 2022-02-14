package com.myapplication.ui.fragments.quran

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranLinesRecyclerBinding

class QuranPagingAdapter(val context:Context):PagingDataAdapter<QuranVersesEntity,QuranPagingAdapter.QuranAyaViewHolder>(DiffCallBack) {


    private var pageNumber = 1

    val linesList:MutableList<List<QuranVersesEntity>> = mutableListOf()
   inner class QuranAyaViewHolder(val binding:QuranLinesRecyclerBinding):RecyclerView.ViewHolder(binding.root)
    {



        @SuppressLint("SetTextI18n")
        fun onBind(position: Int)

        {
            val linesAdapter:QuranLinesAdapter = QuranLinesAdapter(context)

            var page:QuranPage? =null

            Log.d("totalList", "onBind: ${snapshot().items}", )

            val list = snapshot().toList()

            binding.lineRecycler.adapter = linesAdapter

            var lineNum = 1
            var pageData:List<QuranVersesEntity?> = listOf()
            if (list.isNotEmpty())
            {
//                list.filter {
//                    Log.d("filtering", "onBind: ${it?.page},${position+1}")
//                       it?.page == position+1
//
//                   }
    list.forEach {
        Log.e("byWord", "onBind: ${it?.page}, ${list.size}")
    }
                pageData =  if (position == 0) {
                    list.filter {
                        Log.d("filtering1", "onBind: ${it?.page},${position+1}")
                        it?.page == 1
                    }
                }else  {
                    list.filter {
                        if (it?.page!! > 1)
                        {
                            Log.d("filtering", "onBind: ${it?.page},${position+1}")
                            it.page == position
                        }else
                        false
                    }
                }

                if (pageData.isNotEmpty())
                {

                    page = QuranPage(pageData,pageData.last()?.page,pageData.last()?.line)
                }

            }

            Log.e("page", "onBindViewHolder: $page ,position :$position ", )
            if (page != null)
            {
                lineNum = page.lines!!


                for (number in 1..lineNum)
                {
                   val filteredByLine = page.versesList?.filter {

                       it?.line == number

                   }

                    Log.e("QuranPagingAdapter", "onBind: $filteredByLine", )
                    if (filteredByLine?.isNotEmpty()!!)
                    {
                        if (filteredByLine.first()?.line == 1)
                        {
                            linesList.clear()
                            linesAdapter.submitList(listOf())
                        }
                        linesList.add(filteredByLine as List<QuranVersesEntity>)
                        linesAdapter.submitList(linesList)
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


    companion object DiffCallBack: DiffUtil.ItemCallback<QuranVersesEntity>() {
        override fun areItemsTheSame(
            oldItem: QuranVersesEntity,
            newItem:QuranVersesEntity
        ): Boolean {
         return   oldItem == newItem


        }

        override fun areContentsTheSame(
            oldItem: QuranVersesEntity,
            newItem: QuranVersesEntity
        ): Boolean {
            return   oldItem== newItem


        }


    }

    fun getPage(page:Int)
    {
        this.pageNumber = page
    }

}