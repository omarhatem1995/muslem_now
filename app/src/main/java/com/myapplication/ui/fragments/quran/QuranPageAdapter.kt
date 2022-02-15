package com.myapplication.ui.fragments.quran

//import android.content.Context
//import android.content.Intent
//import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
//import android.graphics.Paint
//import android.graphics.Typeface
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import android.widget.Toast
//import androidx.core.content.res.ResourcesCompat
//import androidx.databinding.DataBindingUtil
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.myapplication.LocaleUtil.Companion.getMakkiyahOrMaddaniyah
//import com.myapplication.R
//import com.myapplication.data.entities.model.QuranIndexModel
//import com.myapplication.data.entities.model.QuranVersesEntity
//import com.myapplication.databinding.QuranLinesRecyclerBinding
//import com.myapplication.utils.common.DataBoundListAdapter
//
//
//class QuranPageAdapter(
////    var language: String,
//    var list : List<QuranVersesEntity>,
//    var listOfEmpty : List<Int>,
//    var onClick: (String, QuranVersesEntity) -> Unit
//) : DataBoundListAdapter<QuranVersesEntity, QuranLinesRecyclerBinding>
//    (diffCallback = object : DiffUtil.ItemCallback<QuranVersesEntity>() {
//    override fun areItemsTheSame(oldItem: QuranVersesEntity, newItem: QuranVersesEntity): Boolean {
//        return oldItem == newItem
//    }
//
//    override fun areContentsTheSame(oldItem: QuranVersesEntity, newItem: QuranVersesEntity): Boolean {
//        return oldItem == newItem
//    }
//}) {
//
//    lateinit var context: Context
//    override fun createBinding(parent: ViewGroup): QuranLinesRecyclerBinding {
//        context = parent.context
//        return DataBindingUtil.inflate(
//            LayoutInflater.from(parent.context),
//            R.layout.quran_lines_recycler,
//            parent,
//            false
//        )
//    }
//
//    override fun bind(
//        binding: QuranLinesRecyclerBinding,
//        item: QuranVersesEntity,
//        position: Int,
//        adapterPosition: Int
//    ) {
////        Toast.makeText(context,list[position].text,Toast.LENGTH_LONG).show()
//        binding.line1.text = list[position].text
////        Toast.makeText(context," ${list.size}",Toast.LENGTH_LONG).show()
//        /*val quranLineAdapter = QuranLineAdapter(list) { item, data ->
//        }*/
//        val quranLineAdapter = QuranLineAdapter("en" , listOfEmpty)
//        var x : MutableList<QuranVersesEntity> = ArrayList()
////        for(i in list.indices) {
////            if (list[i].indexOut == item.indexOut) {
////                x.add(list[i])
////            }
////        }
//            Log.d("getPage", "  , $position")
//        binding.lineRecycler.adapter = quranLineAdapter
////        if(list.size-1 == position)
//        quranLineAdapter.submitList(list.filter { it.line == position })
//        if(listOfEmpty.contains(position))
//        {
//
//            binding.line1.visibility = View.VISIBLE
//            binding.lineWord.visibility = View.VISIBLE
//            binding.lineRecycler.visibility = View.GONE
//            binding.line1.text = "\u00F2"
//            binding.lineWord.text = "\u00F1"
//            val typeface2: Typeface = ResourcesCompat.getFont(context, R.font.bsml)!!
//            binding.line1.typeface = typeface2
//            binding.lineWord.typeface = typeface2
//
//        }

//    }
//
//}
            Log.d("getPage", "  , $position")
        binding.lineRecycler.adapter = quranLineAdapter
//        if(list.size-1 == position)
        quranLineAdapter.submitList(list.filter { it.line == position })
        if(listOfEmpty.contains(position))
        {

            binding.line1.visibility = View.VISIBLE
            binding.lineWord.visibility = View.VISIBLE
            binding.lineRecycler.visibility = View.GONE
            binding.constraintQuran.visibility = View.VISIBLE
            binding.line1.text = "\u00F2"
            binding.lineWord.text = "\u00F1 \u005C"
            val typeface2: Typeface = ResourcesCompat.getFont(context, R.font.bsml)!!
            binding.line1.typeface = typeface2
            binding.lineWord.typeface = typeface2

        }
    }

}
