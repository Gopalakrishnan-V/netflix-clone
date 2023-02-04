package com.netflixclone.data_models

import com.squareup.moshi.Json

data class TvShow(
    @Json(name = "id") override val id: Int,
    @Json(name = "name") override val name: String,
    @Json(name = "poster_path") override val posterPath: String?,
    @Json(name = "backdrop_path") override val backdropPath: String?,
    @Json(name = "overview") override val overview: String,
    @Json(name = "first_air_date") override val firstAirDate: String?,
    @Json(name = "vote_average") override val voteAverage: Double,
    @Json(name = "genre_ids") val genreIds: List<Int>,
) : ITvShow

fun TvShow.toMediaTvShow() =
    Media.Tv(id, name, posterPath, backdropPath, overview, firstAirDate, voteAverage, genreIds)