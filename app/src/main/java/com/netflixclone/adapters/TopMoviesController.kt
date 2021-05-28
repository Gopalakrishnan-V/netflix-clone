package com.netflixclone.adapters

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.epoxy.*
import com.bumptech.glide.Glide
import com.netflixclone.R
import com.netflixclone.data_models.Movie
import com.netflixclone.extensions.getBackdropUrl

class TopMoviesController(private val onItemClick: ((Movie) -> Unit)) :
    TypedEpoxyController<List<Movie>>() {
    override fun buildModels(topMovies: List<Movie>) {
        topMovies.forEachIndexed { index, movie ->
            topMovie {
                id(index)
                movie(movie)
                onClick(onItemClick)
            }
        }
    }
}

@EpoxyModelClass
abstract class TopMovieModel : EpoxyModelWithHolder<TopMovieModel.TopMovieHolder>() {

    @EpoxyAttribute
    lateinit var movie: Movie

    @EpoxyAttribute
    lateinit var onClick: (Movie) -> Unit

    inner class TopMovieHolder : EpoxyHolder() {
        lateinit var content: LinearLayout
        lateinit var nameText: TextView
        lateinit var backdropImage: ImageView
        override fun bindView(itemView: View) {
            content = itemView.findViewById(R.id.content)
            backdropImage = itemView.findViewById(R.id.backdrop_image)
            nameText = itemView.findViewById(R.id.name_text)
        }
    }

    override fun bind(holder: TopMovieHolder) {
        Glide.with(holder.backdropImage).load(movie.getBackdropUrl()).into(holder.backdropImage)
        holder.nameText.text = movie.title

        holder.content.setOnClickListener { onClick(movie) }
    }

    override fun getDefaultLayout(): Int = R.layout.item_top_movie
}