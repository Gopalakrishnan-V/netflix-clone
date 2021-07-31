package com.netflixclone.network.models

import com.netflixclone.data_models.IMovie
import com.netflixclone.data_models.Movie
import com.squareup.moshi.Json

data class MovieDetailsResponse(
        @Json(name = "id") override val id: Int,
        @Json(name = "title") override val title: String,
        @Json(name = "poster_path") override val posterPath: String?,
        @Json(name = "backdrop_path") override val backdropPath: String?,
        @Json(name = "overview") override val overview: String,
        @Json(name = "release_date") override val releaseDate: String?,
        @Json(name = "vote_average") override val voteAverage: Double,
        @Json(name = "runtime") val runtime: Int?,
        @Json(name = "similar") val similar: PageResponse<Movie>,
        @Json(name = "videos") val videos: VideosResponse,
): IMovie