package com.netflixclone.network.models

import com.netflixclone.data_models.Movie
import com.squareup.moshi.Json

data class MoviesResponse(
        @Json(name = "page") val page: Int,
        @Json(name = "results") val results: List<Movie>,
        @Json(name = "total_pages") val totalPages: Int,
        @Json(name = "total_results") val totalResults: Int
)