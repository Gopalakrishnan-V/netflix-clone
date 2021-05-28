package com.netflixclone.adapters

import android.graphics.Typeface
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.data_models.PickerItem
import com.netflixclone.databinding.ItemPickerOptionBinding
import com.netflixclone.R

class PickerItemsAdapter(private val selectedIndex: Int, private val onItemClick: ((PickerItem, Int) -> Unit)) :
        ListAdapter<PickerItem, PickerItemViewHolder>(PickerItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PickerItemViewHolder {
        val binding = ItemPickerOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PickerItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: PickerItemViewHolder, position: Int) {
        val pickerItem = getItem(position)
        holder.bind(pickerItem, position, selectedIndex)
    }
}

class PickerItemViewHolder(
        private val binding: ItemPickerOptionBinding,
        private val onItemClick: ((PickerItem, Int) -> Unit)
) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(pickerItem: PickerItem, position: Int, selectedIndex: Int) {
        binding.optionText.text = pickerItem.text
        val selected = position == selectedIndex
        binding.optionText.setTextColor(ContextCompat.getColor(binding.root.context, if (selected) R.color.text_primary else R.color.text_secondary))
        binding.optionText.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (selected) 20.0f else 18.0f)
        binding.optionText.setTypeface(binding.optionText.typeface, if (selected) Typeface.BOLD else Typeface.NORMAL)
        binding.root.setOnClickListener { onItemClick(pickerItem, position) }
    }
}

class PickerItemDiffCallback : DiffUtil.ItemCallback<PickerItem>() {
    override fun areItemsTheSame(oldItem: PickerItem, newItem: PickerItem): Boolean {
        return oldItem.text == newItem.text
    }

    override fun areContentsTheSame(oldItem: PickerItem, newItem: PickerItem): Boolean {
        return oldItem == newItem
    }
}