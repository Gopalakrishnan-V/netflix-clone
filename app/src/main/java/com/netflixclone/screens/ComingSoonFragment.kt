package com.netflixclone.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.netflixclone.adapters.UpcomingMoviesAdapter
import com.netflixclone.data.MediaViewModel
import com.netflixclone.screens.upcoming.UpcomingScreen
import kotlinx.coroutines.flow.collectLatest

class ComingSoonFragment : BottomNavFragment() {
    private val viewModel by viewModels<MediaViewModel>()
    private lateinit var upcomingMoviesAdapter: UpcomingMoviesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(colors = darkColors(background = Color.Black)) {
                    UpcomingScreen()
                }
            }
        }
    }

    override fun onFirstDisplay() {
        fetchData()
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