package com.myapplication.ui.fragments.quran

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.AbsListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.room.PrimaryKey
import com.myapplication.R
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.ActivityQuranBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuranActivity : AppCompatActivity() {

    lateinit var binding:ActivityQuranBinding
    lateinit var adapter: QuranPagingAdapter
    val viewModel:QuranViewModel by viewModels()

    var flag = false

    var currentPageNum = 1
    var isLastPage = false
    var isScrolling = true
    var pageSize = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_quran)



        viewModel.isLastPageState.observe(this){
            isLastPage = it
        }

        viewModel.pageSize.observe(this){
            pageSize = it
        }

        viewModel.quranFlow.observe(this){
            if (it != null && it.isNotEmpty())
                   {
                     Log.e("PageData", "onCreate:num${it} ", )
                       adapter = QuranPagingAdapter(this@QuranActivity)
                        binding.quranRecycler.adapter = adapter
                        adapter.submitList(it)
                    }
        }



        //binding.pageNumber.text = currentPageNum.toString()

        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(binding.quranRecycler)


        binding.quranRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)


                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount


                val isNotLastPage =  !isLastPage
                val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
                val isNotAtBeginning = firstVisibleItemPosition >= 0
                val isTotalMoreThanVisible = totalItemCount >= pageSize
                Log.e("onScrolled", "onScrolled: $totalItemCount , $pageSize, $isNotAtBeginning, $isNotLastPage,$isAtLastItem,$isScrolling", )
                val shouldPaginate = isNotLastPage  && isNotAtBeginning
                        && isScrolling
                Log.e("shouldPaginate", "onScrolled: $shouldPaginate", )
                if(shouldPaginate) {
                    viewModel.getPagingData()
//                    lifecycleScope.launch {
//                        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED)
//                        {
//
//                                viewModel.quranFlow.collect {
//
//                                    if (it != null && it.isNotEmpty())
//                                    {
//                                        flag = true
//                                        Log.e("PageData2", "onCreate:num${it} ", )
//                                        adapter = QuranPagingAdapter(this@QuranActivity)
//                                        binding.quranRecycler.adapter = adapter
//                                        adapter.submitList(it)
//                                    }
//
//
//
//                                }
//
//
//                        }
//                    }

                    isScrolling = false
                } else {
                    Log.e("else", "onScrolled: failed ", )
                    binding.quranRecycler.setPadding(0, 0, 0, 0)
                }




            }


            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                    flag = false
                }
            }
        })

//        var arrayLines : ArrayList<QuranVersesEntity> = ArrayList()
//        var arrayOfAllLines : ArrayList<Int> = ArrayList()
//        var emptyLines : ArrayList<Int> = ArrayList()
//        var emptyLines2 : ArrayList<Int> = ArrayList()
////        var emptyElement : QuranVersesEntity = QuranVersesEntity(0,0,"0",0,0,0,"0","0",0,0,0,"0","0",0,0,"0",0)
//        viewModel.quranVersesMutableLiveData.observe(this,{
//            for(i in 0 until it?.size!!) {
//                arrayLines.add(it[i])
//                it[i].line?.let { it1 -> arrayOfAllLines.add(it1) }
//            }
//            binding.hizbNumber.text = it[0].hizb.toString()
//            binding.juzNumber.text = it[0].juz.toString()
//            binding.suraName.text = it[0].sura.toString()
//            binding.suraNameEnglish.text = it[0].sura.toString()
//        })
//        Handler(Looper.getMainLooper()).postDelayed({
//            Log.d("arrayOfLines" , "${arrayOfAllLines.distinct()}")
//            for(i in 1 until 15) {
//                if (!arrayOfAllLines.contains(i)) {
//                    if (i%2 == 0)
//                    emptyLines.add(i)
//                }
//            }
//
//            /*for(i in emptyLines) {
//                if (emptyLines+1 == emptyLines)
//                    emptyLines2 = emptyLines
//            }*/
//            Log.d("arrayOfLines" , "${emptyLines}")
//
//            val adapter =
//                QuranPageAdapter(arrayLines , emptyLines) { item, quranModelList ->
//
//                }
//            binding.quranRecycler.adapter = adapter
//            adapter.submitList(arrayLines)
//
//        }, 3000)

//        lifecycleScope.launch {
//            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED)
//            {
//                viewModel.quranFlow.collect {
//
//
//                    if (it != null && it.isNotEmpty())
//                    {
//                        Log.e("PageData", "onCreate:num${it} ", )
//                        adapter = QuranPagingAdapter(this@QuranActivity)
//                        binding.quranRecycler.adapter = adapter
//                        adapter.submitList(it)
//                    }
//
//
//                }
//            }
//        }





    }
}