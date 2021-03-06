package com.myapplication.ui.fragments.quran

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.common.Constants
import com.myapplication.databinding.ActivityQuranBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QuranActivity : AppCompatActivity() {

    lateinit var binding: ActivityQuranBinding
    var adapter: QuranPagingAdapter? = null
    val viewModel:QuranViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_quran)

        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(binding.quranRecycler)

        var pageNumber = intent.getIntExtra("pageNumber",1)
        adapter = QuranPagingAdapter(this@QuranActivity) { type, data ,sura ->
            when (type) {
                Constants.LONGCLICK -> {
                    val quranBottomSheet = QuranSoundBottomSheet()
                    val bundle = Bundle()
                    bundle.putString("pageNumber",data)
                    bundle.putString("suraNumberAndAyaInSuraNumber",sura)
                    quranBottomSheet.arguments = bundle
                    quranBottomSheet.show(supportFragmentManager, "a")
                    return@QuranPagingAdapter true
                }

                else -> false
            }
        }

        binding.quranRecycler.adapter = adapter
        viewModel.getPagingData(pageNumber)
//        pageNumber -=1

        binding.quranRecycler.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (pageNumber  > 604)
                {
                    if (dx<0) {
                        this@QuranActivity.finish()
                    }
                }
            }
            })

        lifecycleScope.launch {
            viewModel.quranFlow.flowWithLifecycle(lifecycle,Lifecycle.State.STARTED).collectLatest {
                if (it != null) {
                    adapter!!.submitList(it)
                    binding.quranRecycler.scrollToPosition(pageNumber-1)
                }

            }
        }

        adapter?.let {
            it.getNewPages.observe(this){ position->
                if (position>0) {
                    pageNumber = position
                }

                if (position in 1..603 && position%5 ==0) {
                    viewModel.getPagingData(position-1)
                }

            }
        }


    }

}