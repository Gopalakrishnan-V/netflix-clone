package com.netflixclone.screens.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.netflixclone.data.FeedViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.netflixclone.data.FeedItem
import com.netflixclone.data_models.Media
import com.netflixclone.extensions.toMediaBsData
import com.netflixclone.screens.MediaDetailsBottomSheet

@Composable
fun FeedScreen(feedViewModel: FeedViewModel = viewModel()) {
    val feedList = feedViewModel.feedList.value

    LaunchedEffect(Unit) {
        feedViewModel.fetchData()
    }
    val activity = LocalContext.current as FragmentActivity
    val listState = rememberLazyListState()

    val handleItemClick = remember {
        fun(media: Media) {
            if (media is Media.Movie) {
                MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                    .show(activity.supportFragmentManager, media.id.toString())
            } else if (media is Media.Tv) {
                MediaDetailsBottomSheet.newInstance(media.toMediaBsData())
                    .show(activity.supportFragmentManager, media.id.toString())
            }
        }
    }

    MaterialTheme(colors = darkColors(background = Color.Black)) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
                .padding(bottom = 24.dp),
            state = listState,
        ) {
            if (feedList.data != null) {
                items(feedList.data, key = {
                    when (it) {
                        is FeedItem.Header -> "header"
                        is FeedItem.HorizontalList -> it.title
                    }
                }, contentType = {
                    when (it) {
                        is FeedItem.Header -> 1
                        is FeedItem.HorizontalList -> 2
                    }
                }) {
                    when (it) {
                        is FeedItem.Header -> FeedHeader(it.data, handleItemClick)
                        is FeedItem.HorizontalList -> {
                            FeedHorizontalList(it.title, it.data, it.isLarge, handleItemClick)
                        }
                    }
                }
            }
        }
    }
}

