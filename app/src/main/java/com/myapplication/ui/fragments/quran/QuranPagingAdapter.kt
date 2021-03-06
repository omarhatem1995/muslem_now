package com.myapplication.ui.fragments.quran

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.*
import com.myapplication.SuraNameUtil
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranLinesRecyclerBinding

class QuranPagingAdapter(
    val context: Context,
    var onLongClick : (String,String,String) -> Boolean
) :
    ListAdapter<QuranPage, QuranPagingAdapter.QuranAyaViewHolder>(DiffCallBack) {


    private var pageNumber = 1
    val getNewPages: MutableLiveData<Int> = MutableLiveData(0)


    inner class QuranAyaViewHolder(val binding: QuranLinesRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {



        var suraNumber: Int = 1


        @SuppressLint("SetTextI18n")
        fun onBind(position: Int) {
            val linesList: MutableList<List<QuranVersesEntity>> = mutableListOf()
            var emptyLines: ArrayList<Int> = ArrayList()
            var emptyLinesForChecking: ArrayList<Int> = ArrayList()

//           if (getNewPages.value!! <= position)
//           {
//               getNewPages.value = position
//           }

            getNewPages.value = position
            //var fullList = currentList


            var page: QuranPage? = currentList[position]

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


            if (page != null && page.versesList.isNotEmpty() && page.lines != null) {
                lineNum = page.lines!!

                binding.pageNumber.text = page.page.toString()
                if (page.versesList[0] != null && page.versesList.isNotEmpty()) {
                    binding.suraName.text = page.versesList[0]?.sura?.let {
                        SuraNameUtil.getSuraNameByUnicodeFromSuraNumber(
                            it
                        )
                    }
                    binding.suraJuz.text = page.versesList[0]?.juz.toString()
                } else if (page.versesList[1] != null) {
                    binding.suraName.text = page.versesList[1]?.sura?.let {
                        SuraNameUtil.getSuraNameByUnicodeFromSuraNumber(
                            it
                        )
                    }
                    binding.suraJuz.text = page.versesList[1]?.juz.toString()
                } else {
                    binding.suraName.text = page.versesList[2]?.sura?.let {
                        SuraNameUtil.getSuraNameByUnicodeFromSuraNumber(
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
                    var longClick = "0"
                    if (filteredByLine?.isNotEmpty()!!) {
                        val linesAdapter: QuranLinesAdapter = QuranLinesAdapter(context, emptyLines , longClick,{type ->
                            when (type) {
                                Constants.ONCLICK -> {
//                                    onLongClick.invoke("Y","Y")
                                    recursiveOnClick(filteredByLine, emptyLines, "0", linesList)
                                }
                            }
                        })
                        {
                            type , data , sura->
                                when (type) {
                                    Constants.LONGCLICK -> {
                                        longClick = data
                                        onLongClick.invoke(Constants.LONGCLICK,longClick,sura)
                                        recursiveOnClick(filteredByLine,emptyLines,longClick,linesList)
                                    }
                                }
                            return@QuranLinesAdapter true

                        }
                        binding.lineRecycler.adapter = linesAdapter
                        if (filteredByLine.first()?.line == 1) {
                            emptyLines.clear()
//                             linesList.clear()
                            // linesAdapter.submitList(listOf())
                        }
                        linesList.add(filteredByLine as List<QuranVersesEntity>)
                        linesAdapter.submitList(linesList)

                    }
                    binding.linesContainer.setOnClickListener {
                        recursiveOnClick(filteredByLine,emptyLines,"0",linesList)
                    }

                    binding.lineRecycler.setOnClickListener {
                        recursiveOnClick(filteredByLine,emptyLines,"0",linesList)
                    }


                }
            }


        }

        private fun recursiveOnClick(filteredByLine :List<QuranVersesEntity?>,emptyLines : ArrayList<Int>,
                                     longClick:String,linesList : MutableList<List<QuranVersesEntity>>) {
            if (filteredByLine?.isNotEmpty()!!) {
                val linesAdapter: QuranLinesAdapter = QuranLinesAdapter(context, emptyLines , longClick,{ type ->
                    when (type){
                        Constants.ONCLICK-> recursiveOnClick(filteredByLine,emptyLines,"0",linesList)
                    }
                    }){
                        type , data ,sura->
                    when (type) {
                        Constants.LONGCLICK -> {
                            recursiveOnClick(filteredByLine,emptyLines,data,linesList)
                            onLongClick.invoke(Constants.LONGCLICK,data,sura)
                        }

                    }
                    return@QuranLinesAdapter true


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
