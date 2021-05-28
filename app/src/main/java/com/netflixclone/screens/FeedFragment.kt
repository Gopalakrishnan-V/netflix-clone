package com.netflixclone.screens

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.netflixclone.R
import com.netflixclone.data.FeedViewModel
import com.netflixclone.data.Injection
import com.netflixclone.data.MediaViewModel
import com.netflixclone.data_models.Media
import com.netflixclone.databinding.FragmentFeedBinding
import com.netflixclone.adapters.FeedItemsController
import com.netflixclone.extensions.toMediaBsData
import kotlin.math.min


class FeedFragment : BottomNavFragment() {
    private lateinit var binding: FragmentFeedBinding
    private lateinit var viewModel: MediaViewModel
    private lateinit var feedViewModel: FeedViewModel
    private lateinit var feedItemsController: FeedItemsController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModel()
        (requireActivity() as BottomNavActivity).onFeedFragmentViewCreated()
    }

    override fun onFirstDisplay() {
        feedViewModel.getFeedPagedList()
            .observe(viewLifecycleOwner) { feedItemsController.submitList(it) }
    }

    private fun handleSearchClick() {
        val intent = Intent(requireActivity(), SearchActivity::class.java)
        startActivity(intent)
    }

    private fun handleMediaClick(media: Media) {
        var id: Int? = null
        if (media is Media.Movie) {
            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                .show(requireActivity().supportFragmentManager, id.toString())
        } else if (media is Media.Tv) {
            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                .show(requireActivity().supportFragmentManager, id.toString())
        }
    }

    private fun setupUI() {
        calculateAndSetListTopPadding()
        binding.searchIcon.setOnClickListener { handleSearchClick() }

        binding.feedItemsList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val scrollY: Int = binding.feedItemsList.computeVerticalScrollOffset()
                val color = changeAlpha(
                    ContextCompat.getColor(requireActivity(), R.color.black_transparent),
                    (min(255, scrollY).toFloat() / 255.0f).toDouble()
                )
                binding.appBarLayout.setBackgroundColor(color)
            }
        })


        feedItemsController = FeedItemsController(this::handleMediaClick)
        binding.feedItemsList.adapter = feedItemsController.adapter

        binding.tvShowsTv.setOnClickListener {
            val intent = Intent(requireActivity(), PopularTvActivity::class.java)
            startActivity(intent)
        }

        binding.moviesTv.setOnClickListener {
            val intent = Intent(requireActivity(), PopularMoviesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            Injection.provideMediaViewModelFactory()
        ).get(MediaViewModel::class.java)
        feedViewModel = ViewModelProvider(
            this,
            Injection.provideFeedViewModelFactory()
        ).get(FeedViewModel::class.java)
    }

    private fun calculateAndSetListTopPadding() {
        binding.appBarLayout.viewTreeObserver.addOnGlobalLayoutListener(object :
            OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val availableHeight: Int = binding.appBarLayout.measuredHeight
                if (availableHeight > 0) {
                    binding.appBarLayout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    //save height here and do whatever you want with it

                    val params = binding.feedItemsList.layoutParams as ViewGroup.MarginLayoutParams
                    params.setMargins(0, -availableHeight, 0, 0)
                    binding.feedItemsList.layoutParams = params
                }
            }
        })
    }

    private fun changeAlpha(color: Int, fraction: Double): Int {
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        val alpha: Int = (Color.alpha(color) * (fraction)).toInt()
        return Color.argb(alpha, red, green, blue)
    }
}

