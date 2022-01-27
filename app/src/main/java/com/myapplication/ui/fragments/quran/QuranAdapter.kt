package com.myapplication.ui.fragments.quran

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.myapplication.LocaleUtil.Companion.getMakkiyahOrMaddaniyah
import com.myapplication.R
import com.myapplication.data.entities.model.QuranIndexModel
import com.myapplication.databinding.IndexSouraItemBinding
import com.myapplication.databinding.QuranJuzItemBinding
import com.myapplication.utils.common.DataBoundListAdapter


class QuranAdapter(
    var language: String,
    var onClick: (String, QuranIndexModel) -> Unit
) : DataBoundListAdapter<QuranIndexModel, IndexSouraItemBinding>
    (diffCallback = object : DiffUtil.ItemCallback<QuranIndexModel>() {
    override fun areItemsTheSame(oldItem: QuranIndexModel, newItem: QuranIndexModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: QuranIndexModel, newItem: QuranIndexModel): Boolean {
        return oldItem == newItem
    }
}) {

    lateinit var context: Context
    override fun createBinding(parent: ViewGroup): IndexSouraItemBinding {
        context = parent.context
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.index_soura_item,
            parent,
            false
        )
    }

    override fun bind(
        binding: IndexSouraItemBinding,
        item: QuranIndexModel,
        position: Int,
        adapterPosition: Int
    ) {
        if (language == "ar")
            binding.title.text = item.titleAr
        else
            binding.title.text = item.title

        if(itemCount==1)
            binding.viewLine.visibility = View.GONE

        if(itemCount-1 == position)
            binding.viewLine.visibility = View.GONE

        binding.suraNumber.text = item.index?.toInt().toString()

        binding.metadata.text =
            "${item.type?.let { getMakkiyahOrMaddaniyah(context, it) }} - ${item.count.toString()} " + context.getString(R.string.aya)

        binding.pageNumber.text = item.page + " " +context.getString(R.string.page)
    }
}