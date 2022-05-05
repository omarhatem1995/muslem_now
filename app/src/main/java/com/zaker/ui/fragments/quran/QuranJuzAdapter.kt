package com.zaker.ui.fragments.quran

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.zaker.LocaleUtil.Companion.getJuzNameFromNumber
import com.zaker.R
import com.zaker.data.entities.model.QuranIndexModel
import com.zaker.databinding.QuranJuzItemBinding
import com.zaker.common.DataBoundListAdapter


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
        binding.quranJuzNumberText.text = item.indexOut?.let { getJuzNameFromNumber(it,context) }
        val quranAdapter = QuranAdapter(language) { item, data ->
        }
        var x : MutableList<QuranIndexModel> = ArrayList()
        for(i in list.indices) {
            if (list[i].indexOut == item.indexOut) {
                x.add(list[i])
            }
        }
        binding.quranSouraRecyclerView.adapter = quranAdapter
        quranAdapter.submitList(x)
    }
}