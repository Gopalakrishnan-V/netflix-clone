package com.netflixclone.screens

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.netflixclone.adapters.EpisodeItemsAdapter
import com.netflixclone.adapters.TvShowsAdapter
import com.netflixclone.adapters.VideosController
import com.netflixclone.data.Injection
import com.netflixclone.data.TvShowDetailsViewModel
import com.netflixclone.data_models.TvShow
import com.netflixclone.databinding.ActivityTvDetailsScreenBinding
import com.netflixclone.extensions.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TvDetailsActivity : BaseActivity() {
    lateinit var binding: ActivityTvDetailsScreenBinding
    private lateinit var tvShowDetailsViewModel: TvShowDetailsViewModel
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
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
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
        val details = tvShowDetailsViewModel.tvDetails.value
        if (details != null) {
            val seasonNames =
                details.seasons.mapIndexed { _, season -> season.name } as ArrayList<String>

            val newFragment: ItemPickerFragment =
                ItemPickerFragment.newInstance(seasonNames,
                    tvShowDetailsViewModel.selectedSeasonIndex.value!!)
            newFragment.showsDialog = true
            newFragment.show(supportFragmentManager, "pickerDialog")
            newFragment.setItemClickListener { newSelectedPosition ->
                val selectedSeason = details.seasons[newSelectedPosition]
                binding.selectedSeasonText.text = selectedSeason.name
                tvShowDetailsViewModel.selectedSeasonIndex.value = newSelectedPosition
                lifecycleScope.launch {
                    tvShowDetailsViewModel.fetchSeasonDetails(tvId!!, selectedSeason.seasonNumber)
                }
            }
        }
    }

    private fun setupViewModel() {
        tvShowDetailsViewModel = ViewModelProvider(this,
            Injection.provideTvShowDetailsViewModelFactory()).get(TvShowDetailsViewModel::class.java)

        tvShowDetailsViewModel.tvDetailsLoading.observe(this) { checkAndShowLoader() }
        tvShowDetailsViewModel.tvDetails.observe(this) {
            if (it != null) {
                onDetailsLoad()
                updateDetails()
            }
        }

        tvShowDetailsViewModel.selectedSeasonDetails.observe(this) {
            episodeItemsAdapter.submitList(it.episodes)
        }

        tvShowDetailsViewModel.similarTvShowsLoading.observe(this) {
            if (it) {
                binding.tabContentLoader.show()
            } else {
                binding.tabContentLoader.hide()
            }
        }
        tvShowDetailsViewModel.similarTvShows.observe(this) {
            similarTvItemsAdapter.submitList(it)
            similarTvItemsAdapter.notifyDataSetChanged()
        }

        tvShowDetailsViewModel.tvShowVideos.observe(this) {
            checkAndLoadVideo()
            videosController.setData(it)
        }
    }

    private fun checkAndShowLoader() {
        val detailsLoading = tvShowDetailsViewModel.tvDetailsLoading.value!!
        val details = tvShowDetailsViewModel.tvDetails.value
        val selectedSeasonDetailsLoading =
            tvShowDetailsViewModel.selectedSeasonDetailsLoading.value!!
        val selectedSeasonDetails = tvShowDetailsViewModel.selectedSeasonDetails.value
        val videosLoading = tvShowDetailsViewModel.tvShowVideosLoading.value!!
        val videos = tvShowDetailsViewModel.tvShowVideos.value

        val loading =
            (detailsLoading && details == null) || (selectedSeasonDetailsLoading && selectedSeasonDetails == null) || (videosLoading && videos == null)
        if (loading) {
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
        if (tvId == null) {
            return
        }
        tvShowDetailsViewModel.fetchTvShowDetails(tvId!!)
        tvShowDetailsViewModel.fetchTvShowVideos(tvId!!)
    }

    private fun onDetailsLoad() {
        val details = tvShowDetailsViewModel.tvDetails.value!!
        if (details.seasons.isNotEmpty()) {
            var initialSeasonIndex = details.seasons.indexOfFirst { it.seasonNumber > 0 }
            if (initialSeasonIndex == -1) {
                initialSeasonIndex = 0
            }
            val initialSeason = details.seasons[initialSeasonIndex]
            binding.selectedSeasonText.text = initialSeason.name
            tvShowDetailsViewModel.selectedSeasonIndex.value = initialSeasonIndex
            tvShowDetailsViewModel.fetchSeasonDetails(tvId!!, initialSeason.seasonNumber)
        }
    }

    private fun updateDetails() {
        val details = tvShowDetailsViewModel.tvDetails.value!!
        Glide.with(this).load(details.getBackdropUrl()).transform(CenterCrop())
            .into(binding.thumbnail.backdropImage)
        binding.header.titleText.text = details.name
        binding.header.overviewText.text = details.overview
        binding.header.yearText.text = details.getFirstAirDate()
        binding.header.runtimeText.visibility = View.GONE
        binding.header.ratingText.text = details.voteAverage.toString()
    }

    private fun checkAndLoadVideo() {
        val videos = tvShowDetailsViewModel.tvShowVideos.value
        val firstVideo = videos?.firstOrNull()
        if (firstVideo?.site == "YouTube") {
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
                    if (tvShowDetailsViewModel.similarTvShows.value == null) {
                        binding.tabContentLoader.show()
                        tvShowDetailsViewModel.fetchSimilarTvShows(tvId!!)
                    }
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