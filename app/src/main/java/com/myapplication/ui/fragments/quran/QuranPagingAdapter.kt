package com.myapplication.ui.fragments.quran

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranLinesRecyclerBinding

class QuranPagingAdapter(val context:Context):ListAdapter<QuranPage,QuranPagingAdapter.QuranAyaViewHolder>(DiffCallBack) {


    private var pageNumber = 1
    private val linesAdapter:QuranLinesAdapter = QuranLinesAdapter(context)
    val linesList:MutableList<List<QuranVersesEntity>> = mutableListOf()
    var pages:MutableList<QuranPage> = mutableListOf()

   inner class QuranAyaViewHolder(val binding:QuranLinesRecyclerBinding):RecyclerView.ViewHolder(binding.root)
    {



        @SuppressLint("SetTextI18n")
        fun onBind(position: Int)

        {





            val list = currentList
            binding.lineRecycler.adapter = linesAdapter

            Log.e("Position", "onBind: $position", )
            Log.e("adapterPage", "onBind: ${currentList[position]} ", )


            var lineNum = 1
            var quranList : QuranPage
          //  Log.e(null, "onBindViewHolder: $list ,position :$position ", )
            if (currentList[position].versesList?.isNotEmpty()!!)
            {

                lineNum = currentList[position].lines!!

                Log.e("CurrentPages", "onBind: $currentList ", )
                for (number in 1..lineNum)
                {
                   val filteredByLine = currentList[position].versesList?.filter {

                       it.line == number

                   }

                    //Log.e("QuranPagingAdapter", "onBind: $filteredByLine", )
                    if (filteredByLine?.isNotEmpty()!!)
                    {
                        if (filteredByLine.first().line == 1)
                        {
                            linesList.clear()
                            linesAdapter.submitList(listOf())
                        }
                        linesList.add(filteredByLine)
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


    companion object DiffCallBack: DiffUtil.ItemCallback<QuranPage>() {
        override fun areItemsTheSame(
            oldItem: QuranPage,
            newItem:QuranPage
        ): Boolean {
         return oldItem == newItem

           // if (!oldItem.versesList.isNullOrEmpty()&&!newItem.versesList.isNullOrEmpty())
        }

        override fun areContentsTheSame(
            oldItem: QuranPage,
            newItem: QuranPage
        ): Boolean {
            return   false


        }


    }

    fun getPage(page:Int)
    {
        this.pageNumber = page
    }

}