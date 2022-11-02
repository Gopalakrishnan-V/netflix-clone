package com.netflixclone.data_models

import com.squareup.moshi.Json

data class Movie(
    @Json(name = "id") override val id: Int,
    @Json(name = "title") override val title: String,
    @Json(name = "poster_path") override val posterPath: String?,
    @Json(name = "backdrop_path") override val backdropPath: String?,
    @Json(name = "overview") override val overview: String,
    @Json(name = "release_date") override val releaseDate: String?,
    @Json(name = "vote_average") override val voteAverage: Double,
    @Json(name = "genre_ids") val genreIds: List<Int>,
) : IMovie

fun Movie.toMediaMovie() =
    Media.Movie(id, title, posterPath, backdropPath, overview, releaseDate, voteAverage, genreIds)
