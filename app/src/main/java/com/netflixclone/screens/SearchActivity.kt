package com.netflixclone.screens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.netflixclone.adapters.MediaItemsAdapter
import com.netflixclone.adapters.TopMoviesController
import com.netflixclone.data.SearchResultsViewModel
import com.netflixclone.data_models.Media
import com.netflixclone.data_models.Movie
import com.netflixclone.databinding.ActivitySearchBinding
import com.netflixclone.extensions.hide
import com.netflixclone.extensions.hideKeyboard
import com.netflixclone.extensions.show
import com.netflixclone.extensions.toMediaBsData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val searchResultsViewModel: SearchResultsViewModel by viewModels()
    private lateinit var topSearchesController: TopMoviesController
    private lateinit var searchResultsAdapter: MediaItemsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupViewModel()
        fetchData()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.clearSearchIcon.setOnClickListener {
            binding.searchTextInput.setText("")
        }
        binding.searchTextInput.addTextChangedListener {
            val query = it.toString().trim()
            if (query.isNotEmpty()) {
                searchResultsViewModel.fetchSearchResults(query)
            }
            updateUI()
        }

        topSearchesController = TopMoviesController(this::handleMovieClick)
        binding.topSearchesList.adapter = topSearchesController.adapter
        binding.topSearchesList.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.searchTextInput.clearFocus()
            false
        }

        binding.resultsList.setOnTouchListener { _, _ ->
            hideKeyboard()
            binding.searchTextInput.clearFocus()
            false
        }

        searchResultsAdapter = MediaItemsAdapter(this::handleMediaClick)
        binding.resultsList.adapter = searchResultsAdapter
    }

    private fun handleMovieClick(movie: Movie) {
        hideKeyboard()
        binding.searchTextInput.clearFocus()
        MediaDetailsBottomSheet.newInstance(movie.toMediaBsData())
            .show(supportFragmentManager, movie.id.toString())
    }

    private fun handleMediaClick(media: Media) {
        hideKeyboard()
        binding.searchTextInput.clearFocus()
        if (media is Media.Movie) {
            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                .show(supportFragmentManager, media.id.toString())
        } else if (media is Media.Tv) {
            MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                .show(supportFragmentManager, media.id.toString())
        }
    }

    private fun setupViewModel() {
        searchResultsViewModel.popularMoviesLoading.observe(this) { }
        searchResultsViewModel.popularMovies.observe(this) {
            if (it != null) {
                topSearchesController.setData(it)
            }
        }
        searchResultsViewModel.searchResultsLoading.observe(this) { loading ->
            val searchResults = searchResultsViewModel.searchResults.value
            if (loading && searchResults == null) {
                binding.searchResultsLoader.show()
            } else {
                binding.searchResultsLoader.hide()
            }
        }
        searchResultsViewModel.searchResults.observe(this) {
            searchResultsAdapter.submitList(it)
        }
    }

    private fun updateUI() {
        val query = binding.searchTextInput.text.trim().toString()
        if (query.isEmpty()) {
            binding.emptySearchContent.show()
            binding.searchResultsContent.hide()
        } else {
            val searchResultsLoading = searchResultsViewModel.searchResultsLoading.value!!
            val searchResults = searchResultsViewModel.searchResults.value
            binding.emptySearchContent.hide()
            binding.searchResultsContent.show()

            if (searchResultsLoading && searchResults == null) {
                binding.searchResultsLoader.show()
            } else {
                binding.searchResultsLoader.hide()
            }
        }
    }


    private fun fetchData() {
        searchResultsViewModel.fetchPopularMovies()
    }
}