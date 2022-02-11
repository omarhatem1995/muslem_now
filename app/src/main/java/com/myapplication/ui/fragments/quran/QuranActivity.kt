package com.myapplication.ui.fragments.quran

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.ActivityQuranBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuranActivity : AppCompatActivity() {

    lateinit var binding:ActivityQuranBinding
    lateinit var adapter: QuranPagingAdapter
    val viewModel:QuranViewModel by viewModels()

    var currentPageNum = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_quran)

        adapter = QuranPagingAdapter(this)

        binding.quranRecycler.adapter = adapter

        binding.pageNumber.text = currentPageNum.toString()

        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(binding.quranRecycler)


        binding.quranRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

               val layoutManager= recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount: Int = layoutManager.childCount
                val totalItemCount: Int = layoutManager.itemCount
                val firstVisibleItemPosition: Int = layoutManager.findFirstVisibleItemPosition()

                lifecycleScope.launch {

                        if (withContext(Dispatchers.IO) {
                                viewModel.getLastPage(
                                    currentPageNum
                                )
                            }) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= 1) {
                                currentPageNum++
                                viewModel.getPagingData(currentPageNum)
                                adapter.getPage(currentPageNum)
                            }
                        }

                }


            }
        })

        var arrayLines : ArrayList<QuranVersesEntity> = ArrayList()
        viewModel.quranVersesMutableLiveData.observe(this,{
            for(i in 0 until it?.size!!) {
                arrayLines.add(it[i])
            }
            binding.hizbNumber.text = it[0].hizb.toString()
            binding.juzNumber.text = it[0].juz.toString()
            binding.suraName.text = it[0].sura.toString()
            binding.suraNameEnglish.text = it[0].sura.toString()
        })
        Handler(Looper.getMainLooper()).postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            val adapter =
                QuranPageAdapter(arrayLines) { item, quranModelList ->

                }
            binding.quranRecycler.adapter = adapter
            adapter.submitList(arrayLines)

        }, 3000)
/*
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED)
            {
        *//*        viewModel.quranFlow.collect {
                    Log.d("getQuranList", it?.size.toString())
//                    Toast.makeText(applicationContext,it?.size.toString(),Toast.LENGTH_LONG).show()
                   // Log.e(null, "onCreate:$it ", )
                    if (it != null)
                    adapter.submitList(it)

                }*//*



            }
        }*/





    }
}