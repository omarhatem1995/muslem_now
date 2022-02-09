package com.myapplication.ui.fragments.quran

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.filter
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.myapplication.R
import com.myapplication.databinding.ActivityQuranBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QuranActivity : AppCompatActivity() {

    lateinit var binding:ActivityQuranBinding
    lateinit var adapter: QuranPagingAdapter
    val viewModel:QuranViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this,R.layout.activity_quran)

        adapter = QuranPagingAdapter(this)

        binding.quranRecycler.adapter = adapter

        val pager = PagerSnapHelper()
        pager.attachToRecyclerView(binding.quranRecycler)


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED)
            {
                viewModel.getPagingData().collect {

                    it.filter { entity->
                        entity.page==1
                    }
                    adapter.submitData(it)
                }
            }
        }





    }
}