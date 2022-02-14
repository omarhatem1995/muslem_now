package com.myapplication.ui.fragments.quran

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.insertSeparators
import androidx.paging.map
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

    var currentPageNum = 603

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_quran)

        adapter = QuranPagingAdapter(this)

        binding.quranRecycler.adapter = adapter


        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(binding.quranRecycler)

        lifecycleScope.launch {
            viewModel.getPagingData().flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collect {
                adapter = QuranPagingAdapter(this@QuranActivity)

                binding.quranRecycler.adapter = adapter
                adapter.submitData(it)
            }
        }




//        binding.quranRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//               val layoutManager= recyclerView.layoutManager as LinearLayoutManager
//                val visibleItemCount: Int = layoutManager.childCount
//                val totalItemCount: Int = layoutManager.itemCount
//                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()
//
//                lifecycleScope.launch {
//
//                        if (withContext(Dispatchers.IO) {
//                                viewModel.getLastPage(
//                                    currentPageNum
//                                )
//                            }) {
//                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
//                                && firstVisibleItemPosition >= 0
//                                && totalItemCount >= 1) {
//                                currentPageNum++
//                                viewModel.getPagingData()
//                                adapter.getPage(currentPageNum)
//                            }
//                        }
//
//                }
//
//
//            }
//        })

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
//
//                viewModel.getPagingData().flowWithLifecycle()collect {
//                    //Log.d("getQuranList", it?.size.toString())
//                    //Toast.makeText(applicationContext,it?.size.toString(),Toast.LENGTH_LONG).show()
//                   // Log.e(null, "onCreate:$it ", )
//                    if (it != null)
//                    adapter.submitData(it)
//
//                }




        }





    }
