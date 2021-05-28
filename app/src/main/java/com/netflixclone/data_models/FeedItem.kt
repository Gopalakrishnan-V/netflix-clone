package com.netflixclone.data_models

import com.squareup.moshi.Json

sealed class FeedItem(
        @Json(name = "type") val type: FeedItemType,
) {
    data class Header(
            @Json(name = "data") val data: Media,
    ) : FeedItem(FeedItemType.HEADER)

    data class HorizontalList(
            @Json(name = "title") val title: String,
            @Json(name = "data") val data: List<Media>,
    ) : FeedItem(FeedItemType.HORIZONTAL_LIST)
}

enum class FeedItemType {
    @Json(name = "header")
    HEADER,

    @Json(name = "horizontal_list")
    HORIZONTAL_LIST,
}