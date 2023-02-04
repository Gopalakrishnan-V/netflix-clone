package com.netflixclone.screens.feed

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.netflixclone.R
import com.netflixclone.constants.ImageSize
import com.netflixclone.data_models.Media
import com.netflixclone.extensions.getPosterUrl
import com.netflixclone.helpers.getGenresText

@Composable
fun FeedHeader(data: Media, onInfoClick: (Media) -> Unit) {
    val (posterUrl, genresText) = remember(data) {
        when (data) {
            is Media.Movie -> Pair(data.getPosterUrl(ImageSize.ORIGINAL),
                getGenresText(data.genreIds))
            is Media.Tv -> Pair(data.getPosterUrl(ImageSize.ORIGINAL), getGenresText(data.genreIds))
            else -> Pair(null, null)
        }
    }

    Box(Modifier
        .fillMaxWidth()
        .aspectRatio(0.7f)
    ) {
        AsyncImage(
            model = posterUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.5f)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colorResource(R.color.black_transparent),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = genresText.orEmpty(),
                modifier = Modifier.padding(top = 120.dp),
                color = colorResource(R.color.text_primary),
                fontSize = 12.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_add),
                        "",
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        colorFilter = ColorFilter.tint(colorResource(R.color.white))
                    )

                    Text(
                        "My List",
                        Modifier.padding(top = 2.dp),
                        colorResource(R.color.text_secondary),
                        12.sp
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.White)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = rememberRipple(bounded = false, color = Color.DarkGray),
                            onClick = {}
                        )
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_play),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(Color.Black)
                    )

                    Text(
                        text = "Play",
                        modifier = Modifier.padding(start = 4.dp),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Column(
                    modifier = Modifier
                        .width(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = rememberRipple(),
                            onClick = { onInfoClick(data) }
                        )
                        .padding(vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_info),
                        contentDescription = null,
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        colorFilter = ColorFilter.tint(colorResource(R.color.white))
                    )

                    Text(
                        "Info",
                        Modifier.padding(top = 2.dp),
                        colorResource(R.color.text_secondary),
                        12.sp
                    )
                }
            }
        }
    }
}

val sampleMovie = Media.Movie(
    475557,
    "Joker",
    "/mZuAPY4ETMQPHhCXIcJ90kd6RaS.jpg",
    "",
    "",
    "",
    8.2,
    listOf(80, 53, 18)
)

@Composable
@Preview
fun FeedHeaderPreview() {
    Box(Modifier
        .width(411.dp)
        .height(731.dp)) {
        FeedHeader(sampleMovie) {}
    }
}