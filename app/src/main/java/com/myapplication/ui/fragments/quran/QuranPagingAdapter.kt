package com.myapplication.ui.fragments.quran

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranLinesRecyclerBinding

class QuranPagingAdapter(val context:Context):ListAdapter<QuranVersesEntity,QuranPagingAdapter.QuranAyaViewHolder>(DiffCallBack) {


    private var pageNumber = 1
    val getNewPages:MutableLiveData<Int> = MutableLiveData(0)



   inner class QuranAyaViewHolder(val binding:QuranLinesRecyclerBinding):RecyclerView.ViewHolder(binding.root) {


       var emptyLines : ArrayList<Int> = ArrayList()


       @SuppressLint("SetTextI18n")
       fun onBind(position: Int) {
           val linesList:MutableList<List<QuranVersesEntity>> = mutableListOf()

           if (getNewPages.value!! <= position)
           {
               getNewPages.value = position
           }


           var page: QuranPage? = null

           Log.d("totalList", "onBind: $currentList")

           val list = currentList



           var lineNum = 1
           var pageData: List<QuranVersesEntity?> = listOf()
           if (list.isNotEmpty()) {
               pageData=  list.filter {

                    it?.page == position+1


                   }


                if (pageData.isNotEmpty())
                {

                    page = QuranPage(pageData,pageData.last()?.page,pageData.last()?.line)
                }

            }

               Log.e("page", "onBindViewHolder:position :$position , $page ,")
               if (page != null) {
                   lineNum = page.lines!!



                   for (number in 1..lineNum) {
                       val filteredByLine = page.versesList?.filter {

                           it?.line == number

                       }
                       if(filteredByLine.isEmpty() && isOdd(number)) {
                           emptyLines.add(number - 1)
                       }
                       Log.e("QuranPagingAdapter", "onBind: $filteredByLine")
                       Log.e("emptyAdapterList", "${position} onBind: $emptyLines")

                       if (filteredByLine?.isNotEmpty()!!) {
                           val linesAdapter: QuranLinesAdapter = QuranLinesAdapter(context , emptyLines)
                           binding.lineRecycler.adapter = linesAdapter
                           if (filteredByLine.first()?.line == 1) {
                               emptyLines.clear()
                               // linesList.clear()
                              // linesAdapter.submitList(listOf())
                           }
                           linesList.add(filteredByLine as List<QuranVersesEntity>)
                           linesAdapter.submitList(linesList)

                       }


                   }
               }


           }



       }

    fun isOdd(`val`: Int): Boolean {
        return `val` and 0x01 != 0
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
         return   oldItem.id == newItem.id


        }

        override fun areContentsTheSame(
            oldItem: QuranVersesEntity,
            newItem: QuranVersesEntity
        ): Boolean {
            return   oldItem == newItem


        }


    }

    fun getPage(page:Int)
    {
        this.pageNumber = page
    }


}
