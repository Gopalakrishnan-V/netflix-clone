package com.netflixclone.network.models

import com.netflixclone.data_models.ITvShow
import com.netflixclone.data_models.Season
import com.squareup.moshi.Json

data class TvDetailsResponse(
        @Json(name = "id") override val id: Int,
        @Json(name = "name") override val name: String,
        @Json(name = "poster_path") override val posterPath: String?,
        @Json(name = "backdrop_path") override val backdropPath: String?,
        @Json(name = "overview") override val overview: String,
        @Json(name = "first_air_date") override val firstAirDate: String?,
        @Json(name = "vote_average") override val voteAverage: Double,
        val seasons: List<Season>,
) : ITvShow