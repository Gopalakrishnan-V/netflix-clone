package com.netflixclone.screens.feed

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.netflixclone.data_models.Media
import com.netflixclone.extensions.getId
import com.netflixclone.extensions.getPosterUrl

@Composable
fun FeedHorizontalList(
    title: String,
    data: List<Media>,
    isLarge: Boolean,
    onItemClick: (Media) -> Unit,
) {
    val listState = rememberLazyListState()
    Column(Modifier
        .fillMaxWidth()
        .padding(top = 24.dp)) {
        Text(
            text = title,
            modifier = Modifier
                .padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
                .alpha(0.8f),
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }

    LazyRow(
        modifier = Modifier.padding(end = 4.dp),
        state = listState
    ) {
        items(data, { it.getId() }) { media ->
            val posterUrl = when (media) {
                is Media.Movie -> media.getPosterUrl()
                is Media.Tv -> media.getPosterUrl()
                else -> null
            }
            Poster(url = posterUrl, isLarge) { onItemClick(media) }
        }
    }
}