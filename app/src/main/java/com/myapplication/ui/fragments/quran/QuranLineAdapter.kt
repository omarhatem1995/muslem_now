package com.myapplication.ui.fragments.quran

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.myapplication.R
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranItemBinding
import com.myapplication.utils.common.DataBoundListAdapter


class QuranLineAdapter(
    var language: String,
    var listOfEmpty : List<Int>
//    var onClick: (String, QuranVersesEntity) -> Unit
) : DataBoundListAdapter<QuranVersesEntity, QuranItemBinding>
    (diffCallback = object : DiffUtil.ItemCallback<QuranVersesEntity>() {
    override fun areItemsTheSame(oldItem: QuranVersesEntity, newItem: QuranVersesEntity): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: QuranVersesEntity, newItem: QuranVersesEntity): Boolean {
        return oldItem == newItem
    }
}) {

    lateinit var context: Context
    override fun createBinding(parent: ViewGroup): QuranItemBinding {
        context = parent.context
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.quran_item,
            parent,
            false
        )
    }

    override fun bind(
        binding: QuranItemBinding,
        item: QuranVersesEntity,
        position: Int,
        adapterPosition: Int
    ) {
        var lineNumber = language

        val typeface: Typeface = ResourcesCompat.getFont(context, R.font.p603)!!

        if(item.mobileCode!=null && item.quranAya!=null) {
        Log.d("getCurrentItem" , "${item.line} , ${position}")
            binding.quranLine.text = item.mobileCode
            binding.quranLine.typeface = typeface

            binding.quranLine.setOnClickListener {
                Toast.makeText(context, "aya : ${item.quranAya}", Toast.LENGTH_LONG).show()
            }
        }

    }
}