package com.netflixclone.screens

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.netflixclone.adapters.EpisodeItemsAdapter
import com.netflixclone.adapters.TvShowsAdapter
import com.netflixclone.adapters.VideosController
import com.netflixclone.data.TvShowDetailsViewModel
import com.netflixclone.data_models.TvShow
import com.netflixclone.data_models.Video
import com.netflixclone.databinding.ActivityTvDetailsScreenBinding
import com.netflixclone.extensions.*
import com.netflixclone.network.models.TvDetailsResponse
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TvDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityTvDetailsScreenBinding
    private val tvShowDetailsViewModel: TvShowDetailsViewModel by viewModels()
    private lateinit var episodeItemsAdapter: EpisodeItemsAdapter
    private lateinit var similarTvItemsAdapter: TvShowsAdapter
    private lateinit var videosController: VideosController
    private val tvId: Int?
        get() = intent.extras?.getInt("id")


    var isVideoRestarted = false
    var player: YouTubePlayer? = null
    var bannerVideoLoaded = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvDetailsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchInitialData()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.youtubePlayerView.removeYouTubePlayerListener(youTubePlayerListener)
    }

    private fun handleTvClick(tvShow: TvShow) {
        MediaDetailsBottomSheet.newInstance(tvShow.toMediaBsData())
            .show(supportFragmentManager, tvShow.id.toString())
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        title = ""
        showBackIcon()

        binding.youtubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
        binding.header.overviewText.setOnClickListener {
            binding.header.overviewText.maxLines = 10
            binding.header.overviewText.isClickable = false
        }
        binding.menusTabLayout.addOnTabSelectedListener(tabSelectedListener)

        binding.seasonPicker.setOnClickListener { handleSeasonPickerSelectClick() }

        episodeItemsAdapter = EpisodeItemsAdapter {}
        binding.episodesList.adapter = episodeItemsAdapter
        binding.episodesList.isNestedScrollingEnabled = false

        similarTvItemsAdapter = TvShowsAdapter(this::handleTvClick)
        binding.similarTvsList.adapter = similarTvItemsAdapter
        binding.similarTvsList.isNestedScrollingEnabled = false

        videosController = VideosController {}
        binding.videosList.adapter = videosController.adapter
        binding.videosList.isNestedScrollingEnabled = false
    }

    private fun handleSeasonPickerSelectClick() {
        val details = tvShowDetailsViewModel.details.value?.data
        if (details != null) {
            val seasonNames =
                details.seasons.mapIndexed { _, season -> season.name } as ArrayList<String>

            val itemPickerFragment: ItemPickerFragment =
                ItemPickerFragment.newInstance(seasonNames,
                    tvShowDetailsViewModel.selectedSeasonNameIndexPair.value?.second!!)
            itemPickerFragment.showsDialog = true
            itemPickerFragment.show(supportFragmentManager, "pickerDialog")
            itemPickerFragment.setItemClickListener { newSelectedPosition ->
                val selectedSeason = details.seasons[newSelectedPosition]
                tvShowDetailsViewModel.selectedSeasonNameIndexPair.value =
                    Pair(selectedSeason.name, newSelectedPosition)
                lifecycleScope.launch {
                    tvShowDetailsViewModel.fetchSeasonDetails(tvId!!, selectedSeason.seasonNumber)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupViewModel() {
        tvShowDetailsViewModel.details.observe(this) {
            val loading = (it!!.isLoading && it.data == null)
            if (loading) {
                setLoading(true)
            } else if (it.data != null) {
                setLoading(false)
                updateDetails(it.data)

                // Similar TV Shows
                similarTvItemsAdapter.submitList(it.data.similar.results)
                similarTvItemsAdapter.notifyDataSetChanged()

                // Videos
                checkAndLoadVideo(it.data.videos.results)
                videosController.setData(it.data.videos.results)
            }
        }

        tvShowDetailsViewModel.selectedSeasonNameIndexPair.observe(this) {
            if (it != null) {
                binding.selectedSeasonText.text = it.first
            }
        }

        tvShowDetailsViewModel.selectedSeasonDetails.observe(this) {
            if (it.data != null) {
                episodeItemsAdapter.submitList(it.data.episodes)
            }
        }
    }

    private fun setLoading(flag: Boolean) {
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

    private fun fetchInitialData() {
        if (tvId != null) {
            tvShowDetailsViewModel.fetchTvShowDetails(tvId!!)
        }
    }

    private fun updateDetails(details: TvDetailsResponse) {
        Glide.with(this).load(details.getBackdropUrl()).transform(CenterCrop())
            .into(binding.thumbnail.backdropImage)
        binding.header.titleText.text = details.name
        binding.header.overviewText.text = details.overview
        binding.header.yearText.text = details.getFirstAirDate()
        binding.header.runtimeText.visibility = View.GONE
        binding.header.ratingText.text = details.voteAverage.toString()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
            if (!isVideoRestarted) {
                youTubePlayer.mute()
            }

            if (state == PlayerConstants.PlayerState.ENDED) {
                binding.youtubePlayerView.hide()
                binding.thumbnail.container.show()
                binding.thumbnail.videoLoader.hide()
            }
        }
    }

    private val tabSelectedListener = object : OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> {
                    binding.seasonPicker.show()
                    binding.similarTvsList.hide()
                    binding.episodesList.show()
                    binding.videosList.hide()
                    binding.tabContentLoader.hide()
                }
                1 -> {
                    binding.seasonPicker.hide()
                    binding.episodesList.hide()
                    binding.similarTvsList.show()
                    binding.videosList.hide()
                }
                2 -> {
                    binding.seasonPicker.hide()
                    binding.similarTvsList.hide()
                    binding.episodesList.hide()
                    binding.videosList.show()
                    binding.tabContentLoader.hide()
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }
}