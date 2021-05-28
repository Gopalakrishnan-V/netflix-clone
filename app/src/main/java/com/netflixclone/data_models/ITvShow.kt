package com.netflixclone.data_models

interface ITvShow {
    val id: Int
    val name: String
    val posterPath: String?
    val backdropPath: String?
    val overview: String
    val firstAirDate: String?
    val voteAverage: Double
}
