package com.netflixclone.screens

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnScrollListener
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.netflixclone.adapters.UpcomingMovieViewHolder
import com.netflixclone.adapters.UpcomingMoviesAdapter
import com.netflixclone.data.Injection
import com.netflixclone.data.MediaViewModel
import com.netflixclone.databinding.FragmentComingSoonBinding
import com.netflixclone.extensions.hide
import com.netflixclone.extensions.show
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ComingSoonFragment : BottomNavFragment() {
    private lateinit var binding: FragmentComingSoonBinding
    private lateinit var viewModel: MediaViewModel
    private lateinit var upcomingMoviesAdapter: UpcomingMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentComingSoonBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupViewModel()
    }

    override fun onFirstDisplay() {
        fetchData()
    }

    private fun setupUI() {
        upcomingMoviesAdapter = UpcomingMoviesAdapter()
        binding.upcomingMoviesList.adapter = upcomingMoviesAdapter


        val snapHelper = GravitySnapHelper(Gravity.CENTER)
        snapHelper.scrollMsPerInch = 40.0f
        snapHelper.maxFlingSizeFraction = 0.2f
        snapHelper.attachToRecyclerView(binding.upcomingMoviesList)

        addListScrollListener()
    }

    private fun addListScrollListener() {
        binding.upcomingMoviesList.addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                synchronized(this) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        val newPosition =
                            (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                        if (newPosition == -1) {
                            return
                        }
                        val oldPosition = upcomingMoviesAdapter.firstVisibleItemPosition
                        Log.v(
                            "__SCROLL_STATE_IDLE",
                            "oldPosition: $oldPosition, newPosition: $newPosition"
                        )

                        if (newPosition != oldPosition) {
                            val oldPositionItem =
                                (recyclerView.findViewHolderForAdapterPosition(oldPosition) as UpcomingMovieViewHolder?)
                            val newPositionItem =
                                (recyclerView.findViewHolderForAdapterPosition(newPosition) as UpcomingMovieViewHolder?)

                            oldPositionItem?.binding?.overlay?.show()
                            newPositionItem?.binding?.overlay?.hide()
                            upcomingMoviesAdapter.firstVisibleItemPosition = newPosition
                        }
                    }
                }
            }
        })

        lifecycleScope.launch {

        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            this,
            Injection.provideMediaViewModelFactory()
        ).get(MediaViewModel::class.java)
    }

    private fun fetchData() {
        lifecycleScope.launchWhenCreated {
            try {
                viewModel.getUpcomingMovies().collectLatest {
                    upcomingMoviesAdapter.submitData(it)
                }
            } catch (e: Exception) {
            }
        }
    }
}