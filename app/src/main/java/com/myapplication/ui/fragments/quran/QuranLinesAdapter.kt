package com.myapplication.ui.fragments.quran

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.myapplication.R
import com.myapplication.SuraNameUtil
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.databinding.QuranItemBinding


class QuranLinesAdapter(val context: Context,val emptyList:ArrayList<Int>, val click:String,
                        var onClick: (String) -> Unit,
                        var onLongClick : (String,String,String) -> Boolean
) :
    ListAdapter<List<QuranVersesEntity>, QuranLinesAdapter.QuranLinesViewHolder>(DiffCallBack) {


    inner class QuranLinesViewHolder(private val binding: QuranItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(entities: List<QuranVersesEntity>) {
            //Log.e("QuranLinesAdapter", "onBind:$list ")
            Log.e("QuranLinesAdapter", "currentList:$currentList ")
//           val newList = list.map {
//
//                val kelma = it.mobileCode
//                val text = TextView(context)
//                val  typeface: Typeface = ResourcesCompat.getFont(context, R.font.p1)!!
//                text.text= kelma
//                // Log.e(null, "onBind: $kelma", )
//                text.typeface = typeface
//               text.id = View.generateViewId()
//                return
//
//            }
            var typeface: Typeface?
            entities.forEach {
                val kelma = it.mobileCode
                val kelmaText = it.text
                val kelmaPosition = it.position
                val text = TextView(context)
                var pageNumber = it.page
                var suraNumber = it.sura
                var ayahNumberInSura = it.aya
                text.contentDescription = "${it.quranAya}"

                text.text = kelma
                if(emptyList.contains(position)){
                    binding.headerQuran.text = "\u00F2"
                    binding.headerQuran.visibility = View.VISIBLE
                    binding.headerNameQuran.visibility = View.VISIBLE
                    binding.headerNameQuranTextView.visibility = View.VISIBLE
                    binding.linearLayoutHeader.visibility = View.VISIBLE
                    binding.headerNameQuran.text = "${suraNumber?.let { it1 ->
                        SuraNameUtil.getSuraNameByUnicodeFromSuraNumber(
                            it1
                        )
                    }}"
                    typeface = Typeface.createFromAsset(context.assets,"bsml.ttf")!!
                    binding.headerNameQuran.typeface = typeface
                    binding.headerQuran.typeface = typeface
                }
                if(text.contentDescription == click) {
                    text.background = context.getDrawable(R.color.backgroundGreen)
                }
                text.setOnLongClickListener {
                    onLongClick.invoke(com.myapplication.common.Constants.LONGCLICK,text.contentDescription.toString(),
                        "${suraNumber.toString()}+${ayahNumberInSura.toString()}-${pageNumber.toString()}")

                }
                text.setOnClickListener {
                    onClick.invoke(com.myapplication.common.Constants.ONCLICK)
                }
                binding.constraintQuranItem.setOnClickListener {
                    onClick.invoke(com.myapplication.common.Constants.ONCLICK)
                }

                if(kelmaPosition != 27) {
                    typeface = Typeface.createFromAsset(context.assets, "p$pageNumber.ttf")!!
                    text.typeface = typeface
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP,22f)
                }else{
                    text.text = kelmaText
                    text.setTextSize(TypedValue.COMPLEX_UNIT_SP,18f)
                }
                //text.gravity = Gravity.CENTER_HORIZONTAL
                binding.quranLine.addView(text)
            }




        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuranLinesViewHolder {
        val binding = QuranItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return QuranLinesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: QuranLinesViewHolder, position: Int) {

        val line = currentList[position]

        Log.d("lines", "onBindViewHolder: $line")
//        Toast.makeText(context, line.text, Toast.LENGTH_LONG).show()
        holder.onBind(line)
    }


    companion object DiffCallBack : DiffUtil.ItemCallback<List<QuranVersesEntity>>() {
        override fun areItemsTheSame(
            oldItem: List<QuranVersesEntity>,
            newItem: List<QuranVersesEntity>
        ): Boolean {

            return oldItem.last().id == newItem.last().id


        }

        override fun areContentsTheSame(
            oldItem: List<QuranVersesEntity>,
            newItem: List<QuranVersesEntity>
        ): Boolean {

            return oldItem == newItem


        }


    }


}