package com.netflixclone.data_models

import com.squareup.moshi.Json

data class Season(
    val id: Int,
    val name: String,
    @Json(name = "season_number") val seasonNumber: Int,
    @Json(name = "poster_path") val posterPath: String?,
    val overview: String,
    @Json(name = "air_date") val airDate: String?,
)