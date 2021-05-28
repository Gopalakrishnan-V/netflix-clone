package com.netflixclone.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.netflixclone.constants.ImageSize
import com.netflixclone.data_models.Movie
import com.netflixclone.databinding.ItemUpcomingMovieBinding
import com.netflixclone.extensions.getBackdropUrl
import com.netflixclone.extensions.getGenresText
import com.netflixclone.extensions.getReleaseDayMonth

class UpcomingMoviesAdapter :
    PagingDataAdapter<Movie, UpcomingMovieViewHolder>(UpcomingMovieDiffCallback()) {

    var firstVisibleItemPosition: Int = 0
        set(value) {
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingMovieViewHolder {
        val binding =
            ItemUpcomingMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UpcomingMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingMovieViewHolder, position: Int) {
        val media = getItem(position)
        holder.bind(media, position, firstVisibleItemPosition)
    }
}

class UpcomingMovieViewHolder(
    var binding: ItemUpcomingMovieBinding
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(movie: Movie?, position: Int, firstVisibleItemPosition: Int) {
        if (movie == null) {
            return
        }
        Log.v(
            "___bind",
            "position: $position, firstVisibleItemPosition: $firstVisibleItemPosition, condition: ${(position == firstVisibleItemPosition)}"
        )

        binding.overlay.visibility = if(position == firstVisibleItemPosition) View.GONE else View.VISIBLE

        val posterUrl = movie.getBackdropUrl(ImageSize.ORIGINAL)
        val releaseDayMonth = movie.getReleaseDayMonth()
        Glide.with(binding.backdropImage).load(posterUrl).transform(CenterCrop())
            .into(binding.backdropImage)
        binding.backdropImage.clipToOutline = true
        binding.titleText.text = movie.title
        binding.arrivalDateText.text =
            if (releaseDayMonth != null) "Coming on $releaseDayMonth" else null
        binding.overviewText.text = movie.overview
        binding.genresText.text = movie.getGenresText()
    }

}

class UpcomingMovieDiffCallback : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}