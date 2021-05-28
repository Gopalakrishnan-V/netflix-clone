package com.netflixclone.adapters

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.*
import com.netflixclone.R
import com.netflixclone.data_models.Video
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class VideosController(private val onItemClick: ((Video) -> Unit)) :
    TypedEpoxyController<List<Video>?>() {
    override fun buildModels(videos: List<Video>?) {
        videos?.forEachIndexed { index, it ->
            video {
                id(index)
                video(it)
                onClick(onItemClick)
            }
        }
    }
}

@EpoxyModelClass
abstract class VideoModel : EpoxyModelWithHolder<VideoModel.VideoHolder>() {

    @EpoxyAttribute
    lateinit var video: Video

    @EpoxyAttribute
    lateinit var onClick: (Video) -> Unit

    inner class VideoHolder : EpoxyHolder() {
        lateinit var container: ConstraintLayout
        lateinit var youtubePlayerView: YouTubePlayerView
        lateinit var titleText: TextView
        override fun bindView(itemView: View) {
            container = itemView.findViewById(R.id.container)
            youtubePlayerView = itemView.findViewById(R.id.youtube_player_view)
            titleText = itemView.findViewById(R.id.title_text)
        }
    }

    override fun bind(holder: VideoHolder) {
        holder.titleText.text = video.name
        holder.youtubePlayerView.getYouTubePlayerWhenReady(object: YouTubePlayerCallback{
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(video.key, 0f)
            }
        })
    }

    override fun getDefaultLayout(): Int = R.layout.item_video
}