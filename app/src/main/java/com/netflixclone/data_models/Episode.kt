package com.netflixclone.data_models

import com.squareup.moshi.Json

data class Episode(
    val id: Int,
    val name: String,
    @Json(name = "episode_number") val episodeNumber: Int,
    @Json(name = "still_path") val stillPath: String?,
    val overview: String,
    @Json(name = "air_date") val airDate: String,
    @Json(name = "vote_average") val voteAverage: Double,
)