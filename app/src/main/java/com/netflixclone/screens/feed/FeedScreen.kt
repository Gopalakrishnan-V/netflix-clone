package com.netflixclone.screens.feed

import android.content.Intent
import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.FragmentActivity
import com.netflixclone.data.FeedViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.netflixclone.R
import com.netflixclone.data.FeedItem
import com.netflixclone.data_models.Media
import com.netflixclone.extensions.toMediaBsData
import com.netflixclone.screens.MediaDetailsBottomSheet
import com.netflixclone.screens.PopularMoviesActivity
import com.netflixclone.screens.PopularTvActivity
import com.netflixclone.screens.SearchActivity

object Tab {
    const val TV_SHOWS = "TV Shows"
    const val MOVIES = "Movies"
    const val CATEGORIES = "Categories"
}

val TABS = listOf(Tab.TV_SHOWS, Tab.MOVIES, Tab.CATEGORIES)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(feedViewModel: FeedViewModel = viewModel()) {
    val feedList = feedViewModel.feedList.value

    LaunchedEffect(Unit) {
        feedViewModel.fetchData()
    }
    val activity = LocalContext.current as FragmentActivity
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()

    val handleSearchClick = remember {
        fun() {
            activity.startActivity(Intent(activity, SearchActivity::class.java))
        }
    }

    val handleHeaderTabClick = remember {
        fun(tab: String) {
            when (tab) {
                Tab.TV_SHOWS -> {
                    activity.startActivity(Intent(activity, PopularTvActivity::class.java))
                }
                Tab.MOVIES -> {
                    activity.startActivity(Intent(activity, PopularMoviesActivity::class.java))
                }
            }
        }
    }

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

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        content = { innerPadding ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(colorResource(R.color.black)),
            ) {
                LazyColumn(state = listState) {
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
                                    FeedHorizontalList(it.title,
                                        it.data,
                                        it.isLarge,
                                        handleItemClick)
                                }
                            }
                        }
                    }
                }

                MediumTopAppBar(
                    title = {},
                    navigationIcon = {
                        Image(
                            painter = painterResource(R.drawable.ic_netflix_short_logo),
                            "",
                            modifier = Modifier
                                .width(32.dp)
                                .height(32.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = handleSearchClick) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "",
                                tint = colorResource(R.color.text_primary)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = colorResource(R.color.black_transparent),
                        titleContentColor = colorResource(R.color.text_primary)
                    ),
                    scrollBehavior = scrollBehavior
                )

                Row(Modifier
                    .fillMaxWidth()
                    .padding(top = 96.0.dp)
                    .height(((1 - scrollBehavior.state.collapsedFraction) * 48).dp)
                ) {
                    TABS.forEach {
                        Box(Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = rememberRipple(bounded = false),
                                onClick = { handleHeaderTabClick(it) }
                            ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = it,
                                modifier = Modifier,
                                color = colorResource(R.color.text_primary),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    )
}

