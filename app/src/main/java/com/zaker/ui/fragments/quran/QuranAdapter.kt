package com.zaker.ui.fragments.quran

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.zaker.LocaleUtil.Companion.getSuraMakkiyahOrMaddaniyah
import com.zaker.R
import com.zaker.data.entities.model.QuranIndexModel
import com.zaker.databinding.IndexSouraItemBinding
import com.zaker.common.DataBoundListAdapter


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
            "${item.type?.let { getSuraMakkiyahOrMaddaniyah(context, it) }} - ${item.count.toString()} " + context.getString(R.string.aya)

        binding.pageNumber.text = item.page + " " +context.getString(R.string.page)

        binding.itemSoura.setOnClickListener {
            val intentQuranActivity = Intent(context,QuranActivity::class.java)
            intentQuranActivity.putExtra("pageNumber",item.page?.toInt())
            context.startActivity(intentQuranActivity)
        }
    }
}