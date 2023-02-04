package com.netflixclone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ButtonDefaults.buttonColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.netflixclone.R

const val BANNER_IMAGE = "https://i.ibb.co/12fHwfg/netflix-downloads.png"

@Composable
fun DownloadsScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Downloads") },
                modifier = Modifier.statusBarsPadding(),
                backgroundColor = Color.Black,
                contentColor = colorResource(R.color.text_primary)
            )
        }
    ) { innerPadding ->
        Column(Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colors.background)
        ) {

            Column(Modifier
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                .verticalScroll(rememberScrollState())) {
                Row {
                    Icon(
                        Icons.Outlined.Settings,
                        "",
                        modifier = Modifier
                            .width(20.dp)
                            .height(20.dp),
                        tint = colorResource(R.color.text_secondary)
                    )

                    Text(
                        stringResource(R.string.smart_downloads),
                        Modifier.padding(start = 4.dp),
                        colorResource(R.color.text_secondary),
                        14.sp
                    )
                }

                Text(
                    stringResource(R.string.introducing_downloads_for_you),
                    Modifier.padding(top = 40.dp),
                    color = colorResource(R.color.text_primary),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    stringResource(R.string.we_ll_download_a_personalized),
                    Modifier.padding(top = 8.dp),
                    color = colorResource(R.color.text_secondary),
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )

                AsyncImage(
                    model = BANNER_IMAGE,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                        .height(250.dp),
                    contentScale = ContentScale.Crop,
                )

                Button(
                    onClick = { },
                    Modifier
                        .padding(top = 32.dp)
                        .fillMaxWidth(),
                    colors = buttonColors(
                        backgroundColor = colorResource(R.color.blue),
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = stringResource(R.string.set_up))
                }

                Button(
                    onClick = { },
                    Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(32.dp),
                    colors = buttonColors(
                        backgroundColor = colorResource(R.color.dark_gray),
                        contentColor = Color.White
                    ),
                ) {
                    Text(text = stringResource(R.string.find_something_to_download))
                }
            }
        }
    }
}