package com.netflixclone.screens.upcoming

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.map
import com.netflixclone.R
import com.netflixclone.data.MediaViewModel

@Composable
fun UpcomingScreen(viewModel: MediaViewModel = viewModel()) {
    val upcomingItems = viewModel.getUpcomingMovies().collectAsLazyPagingItems()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Coming soon") },
                backgroundColor = Color.Black,
                contentColor = colorResource(R.color.text_primary)
            )
        }
    ) { innerPadding ->
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
        ) {
            items(upcomingItems, key = { it.id }) {
                if (it != null) {
                    UpcomingItem(it)
                }
            }
        }
    }
}