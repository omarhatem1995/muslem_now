package com.myapplication.ui.fragments.quran

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import com.myapplication.R
import com.myapplication.databinding.ActivityQuranBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class QuranActivity : AppCompatActivity() {

    lateinit var binding:ActivityQuranBinding
    var adapter: QuranPagingAdapter? = null
    val viewModel:QuranViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_quran)

        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(binding.quranRecycler)

        val pageNumber = intent.getIntExtra("pageNumber",1)
        Log.d("pageNumber", pageNumber.toString())
        adapter = QuranPagingAdapter(this@QuranActivity)

        binding.quranRecycler.adapter = adapter
        viewModel.getPagingData(pageNumber)

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
                Log.e("swiped", "onSwiped: right :$position", )
                if (position!=0 &&position%5 ==0)
                    viewModel.getPagingData(position+1)
                binding.quranRecycler.scrollToPosition(position)
            }
        }


        }

    }
