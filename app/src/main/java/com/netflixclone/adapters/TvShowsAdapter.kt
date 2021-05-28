package com.netflixclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.data_models.TvShow
import com.netflixclone.databinding.ItemMediaBinding
import com.netflixclone.extensions.getPosterUrl

class TvShowsAdapter(private val onItemClick: ((TvShow) -> Unit)) :
        ListAdapter<TvShow, TvShowViewHolder>(TvShowDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TvShowViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TvShowViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: TvShowViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class TvShowViewHolder(
        var binding: ItemMediaBinding,
        private val onItemClick: ((TvShow) -> Unit),
) :
        RecyclerView.ViewHolder(binding.root) {

    fun bind(tvShow: TvShow?) {
        if (tvShow == null) {
            return
        }

        val posterUrl = tvShow.getPosterUrl()
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
                .into(binding.posterImage)
        binding.posterImage.clipToOutline = true
        itemView.setOnClickListener { onItemClick(tvShow) }
    }
}

class TvShowDiffCallback : DiffUtil.ItemCallback<TvShow>() {
    override fun areItemsTheSame(oldItem: TvShow, newItem: TvShow): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: TvShow, newItem: TvShow): Boolean {
        return oldItem == newItem
    }
}