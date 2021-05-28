package com.netflixclone.network.models

import com.netflixclone.data_models.Video

data class VideosResponse(
        val id: Int,
        val results: List<Video>,
)