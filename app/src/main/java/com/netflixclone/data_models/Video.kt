package com.netflixclone.data_models

import com.squareup.moshi.Json

data class Video(
    @Json(name = "id") val id: String,
    @Json(name = "key") val key: String,
    @Json(name = "name") val name: String,
    @Json(name = "site") val site: String,
    @Json(name = "type") val type: String,
)