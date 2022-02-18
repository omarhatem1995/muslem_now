package com.myapplication.ui.fragments.quran

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.*
import com.myapplication.SuraNameUtil
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranLinesRecyclerBinding

class QuranPagingAdapter(val context: Context) :
    ListAdapter<QuranPage, QuranPagingAdapter.QuranAyaViewHolder>(DiffCallBack) {


    private var pageNumber = 1
    val getNewPages: MutableLiveData<Int> = MutableLiveData(0)


    inner class QuranAyaViewHolder(val binding: QuranLinesRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {


        var emptyLines: ArrayList<Int> = ArrayList()
        var emptyLinesForChecking: ArrayList<Int> = ArrayList()
        var suraNumber: Int = 1


        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val linesList: MutableList<List<QuranVersesEntity>> = mutableListOf()

//           if (getNewPages.value!! <= position)
//           {
//               getNewPages.value = position
//           }

            getNewPages.value = position


            var page: QuranPage? = currentList[position]

            Log.d("totalList", "onBind: $currentList")

            //val list = currentList


            var lineNum = 1
//           var pageData: List<QuranVersesEntity?> = listOf()
//           if (list.isNotEmpty()) {
//               pageData=  list.filter {
//
//                    it?.page == position+1
//
//
//                   }
//
//
//                if (pageData.isNotEmpty())
//                {
//
//                    page = QuranPage(pageData,pageData.last()?.page,pageData.last()?.line)
//                }
//
//            }

            Log.e("page", "onBindViewHolder:position :$position , $page ,")
            if (page != null && page.versesList.isNotEmpty()) {
                lineNum = page.lines!!

                binding.pageNumber.text = page.page.toString()
                if (page.versesList[0] != null && page.versesList.isNotEmpty()) {
                    binding.suraName.text = page.versesList[0]?.sura?.let {
                        SuraNameUtil.getSuraName(
                            it
                        )
                    }
                    binding.suraJuz.text = page.versesList[0]?.juz.toString()
                } else if (page.versesList[1] != null) {
                    binding.suraName.text = page.versesList[1]?.sura?.let {
                        SuraNameUtil.getSuraName(
                            it
                        )
                    }
                    binding.suraJuz.text = page.versesList[1]?.juz.toString()
                } else {
                    binding.suraName.text = page.versesList[2]?.sura?.let {
                        SuraNameUtil.getSuraName(
                            it
                        )
                    }
                    binding.suraJuz.text = page.versesList[2]?.juz.toString()
                }
                for (number in 1..lineNum) {
                    val filteredByLine = page.versesList?.filter {

                        it?.line == number

                    }
                    if (filteredByLine.isEmpty()) {
                        emptyLinesForChecking.add(number - 1)
                        if (isOdd(number)) {
                            if (number > 0 && number > 1) {
                                emptyLines.add(number - emptyLinesForChecking.size)
                            } else {
                                emptyLines.add(number - 1)
                            }
                        }
                        /*         if(filteredByLine[number-1]!=null)
                                 suraNumber = filteredByLine[number-1]?.sura!!*/
                    }
                    Log.e("QuranPagingAdapter", "onBind: $filteredByLine")
                    Log.e("emptyAdapterList", "$emptyLinesForChecking onBind: $emptyLines")
                    var click = "0"
                    if (filteredByLine?.isNotEmpty()!!) {
                        val linesAdapter: QuranLinesAdapter = QuranLinesAdapter(context, emptyLines , click){
                            type , data ->
                                when (type) {
                                    Constants.INCREASEADAPTER -> {
                                        click = data
//                                        Log.d("ClickListener", " $click")
                                        recursiveOnClick(filteredByLine,emptyLines,click,linesList)
                                    }
                                }

                        }
//                        Log.d("getLinesList", " $linesList")
                        binding.lineRecycler.adapter = linesAdapter
                        if (filteredByLine.first()?.line == 1) {
                            emptyLines.clear()
//                             linesList.clear()
                            // linesAdapter.submitList(listOf())
                        }
                        linesList.add(filteredByLine as List<QuranVersesEntity>)
                        linesAdapter.submitList(linesList)

                    }


                }
            }


        }

        private fun recursiveOnClick(filteredByLine :List<QuranVersesEntity?>,emptyLines : ArrayList<Int>,
                                     click:String,linesList : MutableList<List<QuranVersesEntity>>) {
            if (filteredByLine?.isNotEmpty()!!) {
                val linesAdapter: QuranLinesAdapter = QuranLinesAdapter(context, emptyLines , click){
                        type , data ->
                    when (type) {
                        Constants.INCREASEADAPTER -> {
//                        click = data
                            Log.d("ClickListener", " $click")
                            recursiveOnClick(filteredByLine,emptyLines,data,linesList)
                        }
                    }

                }

                binding.lineRecycler.adapter = linesAdapter
                if (filteredByLine.first()?.line == 1) {
                    emptyLines.clear()
//                     linesList.clear()
                    // linesAdapter.submitList(listOf())
                }
//                linesList.add(filteredByLine as List<QuranVersesEntity>)
                linesAdapter.submitList(linesList)

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
        val binding =
            QuranLinesRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QuranAyaViewHolder(binding)
    }


    companion object DiffCallBack : DiffUtil.ItemCallback<QuranPage>() {
        override fun areItemsTheSame(
            oldItem: QuranPage,
            newItem: QuranPage
        ): Boolean {
            return oldItem.page == newItem.page


        }

        override fun areContentsTheSame(
            oldItem: QuranPage,
            newItem: QuranPage
        ): Boolean {
            return oldItem == newItem


        }


    }


}
