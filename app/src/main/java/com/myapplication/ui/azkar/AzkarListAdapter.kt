package com.myapplication.ui.azkar

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.myapplication.R
import com.myapplication.common.Constants
import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.databinding.ActivityZekrBinding
import com.myapplication.databinding.AzkarListItemBinding
import com.myapplication.utils.common.DataBoundListAdapter

class AzkarListAdapter (
    var onClick: (String, AzkarModel)-> Unit
): DataBoundListAdapter<AzkarModel,AzkarListItemBinding>
    (diffCallback = object : DiffUtil.ItemCallback<AzkarModel>(){
    override fun areItemsTheSame(oldItem: AzkarModel, newItem: AzkarModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: AzkarModel, newItem: AzkarModel): Boolean {
        return oldItem == newItem
    }

}) {
    lateinit var context: Context
    override fun createBinding(parent: ViewGroup): AzkarListItemBinding {
        context = parent.context
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.azkar_list_item,
            parent,
            false
        )
    }

    override fun bind(
        binding: AzkarListItemBinding,
        item: AzkarModel,
        position: Int,
        adapterPosition: Int
    ) {
        binding.azkarName.text = item.zekr
        binding.azkarDescription.text = item.description
        binding.azkarCount.text = item.count.toString()
        var counterPressed = item.userPressedCount
        var actualCount: Int
        var counter : Int
        var zekrItemId = item.id
        Log.d("getItemCount" , " "  + item.count)
        if(item.count!! == 0){
            counter = 0
            actualCount = 0
        }else {
            actualCount = item.count.toInt() - counterPressed
            if (actualCount < 1) {
                Log.d("azkarCircle" , " is not called $actualCount")
                binding.azkarCircle.setImageResource(R.drawable.finished_zekr_count)
                binding.azkarCount.visibility = View.GONE
            }
            counter = (binding.azkarCount.text as String).toInt()
            binding.azkarCount.text = actualCount.toString()
        }
        binding.azkarCount.setOnClickListener {
            if(counter ==0)

            else {
                counter -= 1
                counterPressed += 1
                Log.d("azkarCircle" , " is pressed $counterPressed")
                binding.azkarCount.text = counter.toString()
                if (item.count <= counterPressed) {
                    Log.d("azkarCircle" , "if item.count is pressed $counterPressed")
                    binding.azkarCircle.setImageResource(R.drawable.finished_zekr_count)
                    binding.azkarCount.visibility = View.GONE
                }
            }
            var newItem = item
            newItem.userPressedCount += 1
            onClick.invoke(Constants.INCREASEADAPTER,newItem)
            notifyDataSetChanged()//            userPressed.userPressed(zekrItemId,counterPressed)
        }
        binding.azkarCircle.setOnClickListener {
            if(counter ==0)

            else {
                counter -= 1
                counterPressed += 1
            Log.d("azkarCircle" , " is pressed $counterPressed")
                binding.azkarCount.text = counter.toString()
                if (item.count <= counterPressed) {
                    Log.d("azkarCircle" , "if item.count is pressed $counterPressed")
                    binding.azkarCircle.setImageResource(R.drawable.finished_zekr_count)
                    binding.azkarCount.visibility = View.GONE
                }
            }
            var newItem = item
            newItem.userPressedCount += 1
            onClick.invoke(Constants.INCREASEADAPTER,newItem)
            notifyDataSetChanged()
        }
        binding.azkarReloadImageView.setOnClickListener {
            var newItem = item
            newItem.userPressedCount = 0
            counter = item.count
            binding.azkarCircle.setImageResource(R.drawable.azkar_circle)
            binding.azkarCount.visibility = View.VISIBLE
            binding.azkarCount.text = item.count.toString()
            counterPressed = 0
            onClick.invoke(Constants.RESETADAPTER,newItem)
        }
        binding.azkarShareImageView.setOnClickListener {
            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, item.zekr)
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            context.startActivity(shareIntent)
        }
    }
}