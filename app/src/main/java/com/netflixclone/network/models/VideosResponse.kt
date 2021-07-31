package com.netflixclone.network.models

import com.netflixclone.data_models.Video

data class VideosResponse(
        val results: List<Video>,
)