package com.myapplication.ui.fragments.quran

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.LocaleUtil.Companion.getJuzName
import com.myapplication.R
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.QuranIndexModel
import com.myapplication.databinding.IndexSouraItemBinding
import com.myapplication.databinding.QuranJuzItemBinding
import com.myapplication.utils.common.DataBoundListAdapter


class QuranJuzAdapter(
    var language :String,
    var list : List<QuranIndexModel>,
    var onClick: (String, QuranIndexModel) -> Unit
) : DataBoundListAdapter<QuranIndexModel, QuranJuzItemBinding>
    (diffCallback = object : DiffUtil.ItemCallback<QuranIndexModel>() {
    override fun areItemsTheSame(oldItem: QuranIndexModel, newItem: QuranIndexModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: QuranIndexModel, newItem: QuranIndexModel): Boolean {
        return oldItem == newItem
    }
}) {

    lateinit var context: Context
    override fun createBinding(parent: ViewGroup): QuranJuzItemBinding {
        context = parent.context
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.quran_juz_item,
            parent,
            false
        )
    }

    override fun bind(
        binding: QuranJuzItemBinding,
        item: QuranIndexModel,
        position: Int,
        adapterPosition: Int
    ) {
        binding.quranJuzNumberText.text = item.indexOut?.let { getJuzName(it,context) }
        val quranAdapter = QuranAdapter(language) { item, data ->
        }
        var x : MutableList<QuranIndexModel> = ArrayList()
        for(i in 0..list.size-1) {
            if (list[i].indexOut == item.indexOut) {
                x.add(list[i])
            }
        }
        binding.quranSouraRecyclerView.adapter = quranAdapter
        quranAdapter.submitList(x)
    }
}