package com.netflixclone.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayout
import com.netflixclone.adapters.MoviesAdapter
import com.netflixclone.adapters.VideosController
import com.netflixclone.data.MovieDetailsViewModel
import com.netflixclone.data_models.Movie
import com.netflixclone.data_models.Video
import com.netflixclone.databinding.ActivityMovieDetailsBinding
import com.netflixclone.extensions.*
import com.netflixclone.network.models.MovieDetailsResponse
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MovieDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityMovieDetailsBinding
    private val movieDetailsViewModel: MovieDetailsViewModel by viewModels()
    private lateinit var similarMoviesItemsAdapter: MoviesAdapter
    private lateinit var videosController: VideosController
    private val movieId: Int?
        get() = intent.extras?.getInt("id")

    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchData()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
        binding.tabLayout.removeOnTabSelectedListener(tabSelectedListener)
    }

    private fun handleMovieClick(item: Movie) {
        MediaDetailsBottomSheet.newInstance(item.toMediaBsData())
            .show(supportFragmentManager, item.id.toString())
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.loader.root.show()
        binding.content.hide()
        binding.youtubePlayerView.hide()
        binding.thumbnail.container.hide()
        binding.thumbnail.playContainer.setOnClickListener { replayVideo() }

        binding.header.overviewText.setOnClickListener {
            binding.header.overviewText.maxLines = 10
            binding.header.overviewText.isClickable = false
        }

        binding.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        binding.tabLayout.addOnTabSelectedListener(tabSelectedListener)

        similarMoviesItemsAdapter = MoviesAdapter(this::handleMovieClick)
        binding.similarMoviesList.adapter = similarMoviesItemsAdapter
        binding.similarMoviesList.isNestedScrollingEnabled = false

        videosController = VideosController {}
        binding.videosList.adapter = videosController.adapter
        binding.videosList.isNestedScrollingEnabled = false
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViewModel() {
        movieDetailsViewModel.details.observe(this) {
            if (it.isLoading && it.data == null) {
                showLoader(true)
            } else if (it.data != null) {
                showLoader(false)
                updateDetails(it.data)
            }
        }
    }

    private fun fetchData() {
        if (movieId != null) {
            movieDetailsViewModel.fetchMovieDetails(movieId!!)
        }
    }

    private fun showLoader(flag: Boolean) {
        if (flag) {
            binding.loader.root.show()
            binding.content.hide()
            binding.youtubePlayerView.hide()
            binding.thumbnail.container.hide()
        } else {
            binding.loader.root.hide()
            binding.content.show()
            binding.thumbnail.container.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateDetails(details: MovieDetailsResponse) {
        // Basic details
        Glide.with(this).load(details.getBackdropUrl()).transform(CenterCrop())
            .into(binding.thumbnail.backdropImage)
        binding.header.titleText.text = details.title
        binding.header.overviewText.text = details.overview
        binding.header.yearText.text = details.getReleaseYear()
        binding.header.runtimeText.text = details.getRunTime()
        binding.header.ratingText.text = details.voteAverage.toString()

        // Similar movies
        similarMoviesItemsAdapter.submitList(details.similar.results)
        similarMoviesItemsAdapter.notifyDataSetChanged()

        // Videos
        checkAndLoadVideo(details.videos.results)
        videosController.setData(details.videos.results)
    }

    private fun checkAndLoadVideo(videos: List<Video>) {
        val firstVideo =
            videos.firstOrNull { video -> (video.type == "Trailer") && video.site == "YouTube" }
        if (firstVideo != null) {
            if (!bannerVideoLoaded) {
                binding.youtubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        player = youTubePlayer
                        youTubePlayer.loadVideo(firstVideo.key, 0f)
                        bannerVideoLoaded = true
                    }
                })
            }
        } else {
            binding.thumbnail.playContainer.hide()
        }
    }

    private fun replayVideo() {
        if (player != null) {
            player!!.seekTo(0f)
            lifecycleScope.launch {
                delay(500)
                binding.youtubePlayerView.show()
                binding.thumbnail.container.hide()
            }
        }
    }

    private val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
            if (!isVideoRestarted && second > 3.2) {
                isVideoRestarted = true
                lifecycleScope.launch {
                    youTubePlayer.seekTo(0f)
                    youTubePlayer.unMute()
                    binding.youtubePlayerView.getPlayerUiController().showUi(false)
                    delay(50)
                    binding.thumbnail.container.hide()
                    binding.thumbnail.videoLoader.hide()
                    binding.youtubePlayerView.show()
                    delay(1000)
                    binding.youtubePlayerView.getPlayerUiController().showUi(true)
                }
            }
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState,
        ) {
            if ((state == PlayerConstants.PlayerState.UNSTARTED) && !isVideoRestarted) {
                youTubePlayer.mute()
            }

            if (state == PlayerConstants.PlayerState.ENDED) {
                binding.youtubePlayerView.hide()
                binding.thumbnail.container.show()
                binding.thumbnail.videoLoader.hide()
            }
        }
    }

    private val tabSelectedListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            if (tab?.position == 0) {
                binding.similarMoviesList.show()
                binding.videosList.hide()
            } else {
                binding.similarMoviesList.hide()
                binding.videosList.show()
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }
}