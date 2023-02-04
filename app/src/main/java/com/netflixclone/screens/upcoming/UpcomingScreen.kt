package com.netflixclone.screens.upcoming

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.map
import com.netflixclone.R
import com.netflixclone.data.MediaViewModel
import com.netflixclone.data_models.Movie
import com.netflixclone.extensions.toMediaBsData
import com.netflixclone.screens.MediaDetailsBottomSheet

@Composable
fun UpcomingScreen(viewModel: MediaViewModel = viewModel()) {
    val upcomingItems = viewModel.getUpcomingMovies().collectAsLazyPagingItems()
    val activity = LocalContext.current as FragmentActivity

    val handleItemClick = remember {
        fun(movie: Movie) {
            MediaDetailsBottomSheet.newInstance(movie.toMediaBsData())
                .show(activity.supportFragmentManager, movie.id.toString())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coming soon") },
                modifier = Modifier.statusBarsPadding(),
                backgroundColor = Color.Black,
                contentColor = colorResource(R.color.text_primary)
            )
        }
    ) { innerPadding ->
        LazyColumn(Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            items(upcomingItems, key = { it.id }) {
                if (it != null) {
                    UpcomingItem(it, handleItemClick)
                }
            }
        }
    }
}