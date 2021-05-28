package com.netflixclone.network.services

import com.netflixclone.network.models.FeedResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface FirebaseService {
    @GET("p%2Fnetflix%2Fapi%2Ffeed%2F{page}.json?alt=media")
    suspend fun fetchFeedItems(@Path("page") page: Int): FeedResponse
}