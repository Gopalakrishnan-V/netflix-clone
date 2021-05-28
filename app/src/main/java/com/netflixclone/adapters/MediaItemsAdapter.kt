package com.netflixclone.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.data_models.Media
import com.netflixclone.databinding.ItemMediaBinding
import com.netflixclone.extensions.getPosterUrl
import com.netflixclone.extensions.hide
import com.netflixclone.extensions.show

class MediaItemsAdapter(private val onItemClick: ((Media) -> Unit)) :
    ListAdapter<Media, MediaItemViewHolder>(MediaItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaItemViewHolder {
        val binding = ItemMediaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MediaItemViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: MediaItemViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media)
    }
}

class MediaItemViewHolder(
    var binding: ItemMediaBinding,
    private val onItemClick: ((Media) -> Unit)
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(media: Media?) {
        if (media == null) {
            return
        }

        var posterUrl: String? = null
        if (media is Media.Movie) {
            posterUrl = media.getPosterUrl()
        } else if (media is Media.Tv) {
            posterUrl = media.getPosterUrl()
        }
        Glide.with(binding.posterImage).load(posterUrl).transform(CenterCrop())
            .into(binding.posterImage)
        binding.posterImage.clipToOutline = true

        itemView.setOnClickListener { onItemClick(media) }
    }
}

class MediaItemDiffCallback : DiffUtil.ItemCallback<Media>() {
    override fun areItemsTheSame(oldItem: Media, newItem: Media): Boolean {
        return (oldItem is Media.Movie && newItem is Media.Movie && oldItem.id == newItem.id) || (oldItem is Media.Tv && newItem is Media.Tv && oldItem.id == newItem.id)
    }

    override fun areContentsTheSame(oldItem: Media, newItem: Media): Boolean {
        return oldItem == newItem
    }
}