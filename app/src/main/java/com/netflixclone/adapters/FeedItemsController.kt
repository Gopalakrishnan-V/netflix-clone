package com.netflixclone.adapters

import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.*
import com.airbnb.epoxy.paging.PagedListEpoxyController
import com.bumptech.glide.Glide
import com.netflixclone.R
import com.netflixclone.constants.ImageSize
import com.netflixclone.data_models.FeedItem
import com.netflixclone.data_models.Media
import com.netflixclone.extensions.getPosterUrl
import com.netflixclone.helpers.getGenresText

class FeedItemsController(private val onMediaClick: (Media) -> Unit) :
    PagedListEpoxyController<FeedItem>() {

    override fun buildItemModel(currentPosition: Int, item: FeedItem?): EpoxyModel<*> {

        return when (item) {
            is FeedItem.Header -> {
                FeedHeaderModel_()
                    .id("header")
                    .data(item.data)
                    .onInfoClick{onMediaClick(item.data)}
            }
            is FeedItem.HorizontalList -> {
                FeedItemHorizontalList_()
                    .id(currentPosition)
                    .data(item)
                    .onItemClick(onMediaClick)
            }
            else -> {
                EmptyModel_()
                    .id("empty_$currentPosition")
            }
        }
    }
}

@EpoxyModelClass
abstract class FeedHeaderModel : EpoxyModelWithHolder<FeedHeaderModel.FeedHeaderHolder>() {
    @EpoxyAttribute
    lateinit var data: Media

    @EpoxyAttribute
    lateinit var onInfoClick: () -> Unit

    inner class FeedHeaderHolder : EpoxyHolder() {
        lateinit var backgroundImage: ImageView
        lateinit var genreText: TextView
        lateinit var infoButton: LinearLayout

        override fun bindView(itemView: View) {
            backgroundImage = itemView.findViewById(R.id.background_image)
            genreText = itemView.findViewById(R.id.genres_text)
            infoButton = itemView.findViewById(R.id.info_button)
        }
    }

    override fun bind(holder: FeedHeaderHolder) {
        var posterUrl: String? = null
        if (data is Media.Movie) {
            posterUrl = (data as Media.Movie).getPosterUrl(ImageSize.ORIGINAL)
            holder.genreText.text = getGenresText((data as Media.Movie).genreIds)
        } else if (data is Media.Tv) {
            posterUrl = (data as Media.Tv).getPosterUrl(ImageSize.ORIGINAL)
            holder.genreText.text = getGenresText((data as Media.Movie).genreIds)
        }
        Glide.with(holder.backgroundImage).load(posterUrl).into(holder.backgroundImage)


        val animZoomOut =
            AnimationUtils.loadAnimation(holder.backgroundImage.context, R.anim.zoom_out)
        holder.backgroundImage.startAnimation(animZoomOut)

        holder.infoButton.setOnClickListener { onInfoClick() }
    }

    override fun getDefaultLayout(): Int = R.layout.item_feed_header
}


@EpoxyModelClass
abstract class FeedItemHorizontalList :
    EpoxyModelWithHolder<FeedItemHorizontalList.FeedItemHorizontalListHolder>() {
    @EpoxyAttribute
    lateinit var data: FeedItem.HorizontalList

    @EpoxyAttribute
    lateinit var onItemClick: (Media) -> Unit

    inner class FeedItemHorizontalListHolder : EpoxyHolder() {
        lateinit var titleText: TextView
        lateinit var postersList: RecyclerView

        override fun bindView(itemView: View) {
            titleText = itemView.findViewById(R.id.title_text)
            postersList = itemView.findViewById(R.id.posters_list)
        }
    }

    override fun bind(holder: FeedItemHorizontalListHolder) {
        holder.titleText.text = data.title
        val controller = MediaItemsController(onItemClick)
        controller.setData(data.data)
        holder.postersList.isNestedScrollingEnabled = false
        holder.postersList.adapter = controller.adapter
    }

    override fun getDefaultLayout(): Int = R.layout.item_feed_horizontal_list
}


class MediaItemsController(val onMediaClick: (Media) -> Unit) :
    TypedEpoxyController<List<Media>>() {
    override fun buildModels(items: List<Media>) {
        items.mapIndexed { index, item ->
            media {
                id(index)
                data(item)
                onClick { onMediaClick(item) }
            }
        }
    }
}

@EpoxyModelClass
abstract class MediaModel : EpoxyModelWithHolder<MediaModel.MediaHolder>() {
    @EpoxyAttribute
    lateinit var data: Media

    @EpoxyAttribute
    lateinit var onClick: () -> Unit

    inner class MediaHolder : EpoxyHolder() {
        lateinit var posterImage: ImageView

        override fun bindView(itemView: View) {
            posterImage = itemView.findViewById(R.id.poster_image)
        }
    }

    override fun bind(holder: MediaHolder) {
        var posterUrl: String? = null
        if (data is Media.Movie) {
            posterUrl = (data as Media.Movie).getPosterUrl()
        } else if (data is Media.Tv) {
            posterUrl = (data as Media.Tv).getPosterUrl()
        }
        holder.posterImage.setOnClickListener { onClick() }
        Glide.with(holder.posterImage).load(posterUrl).into(holder.posterImage)
        holder.posterImage.clipToOutline = true
    }

    override fun getDefaultLayout(): Int = R.layout.item_poster
}

@EpoxyModelClass
abstract class EmptyModel : EpoxyModelWithHolder<EmptyModel.EmptyHolder>() {
    inner class EmptyHolder : EpoxyHolder() {
        override fun bindView(itemView: View) {}
    }

    override fun bind(holder: EmptyHolder) {}

    override fun getDefaultLayout(): Int = R.layout.item_empty
}