package com.netflixclone.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.netflixclone.data_models.Movie
import com.netflixclone.data_models.TvShow
import com.netflixclone.network.services.ApiClient
import kotlinx.coroutines.flow.Flow

val defaultPagingConfig = PagingConfig(
        pageSize = MediaRepository.NETWORK_PAGE_SIZE,
        enablePlaceholders = false
)

object MediaRepository {
    private var upcomingMovies: Flow<PagingData<Movie>>? = null
    private var popularMovies: Flow<PagingData<Movie>>? = null
    private var popularTvShows: Flow<PagingData<TvShow>>? = null

    fun getUpcomingMovies(): Flow<PagingData<Movie>> {
        if (upcomingMovies != null) {
            return upcomingMovies!!
        }
        upcomingMovies = Pager(config = defaultPagingConfig, pagingSourceFactory = { UpcomingMoviesPagingSource() }).flow
        return upcomingMovies!!
    }

    fun getPopularMoviesStream(): Flow<PagingData<Movie>> {
        if (popularMovies != null) {
            return popularMovies!!
        }
        popularMovies = Pager(config = defaultPagingConfig, pagingSourceFactory = { PopularMoviesPagingSource() }).flow
        return popularMovies!!
    }

    fun getPopularTvShowsStream(): Flow<PagingData<TvShow>> {
        if (popularTvShows != null) {
            return popularTvShows!!
        }
        popularTvShows = Pager(config = defaultPagingConfig, pagingSourceFactory = { PopularTvShowsPagingSource() }).flow
        return popularTvShows!!
    }

    suspend fun fetchMovieDetails(id: Int) = ApiClient.TMDB.fetchMovieDetails(id)

    suspend fun fetchTvShowDetails(id: Int) = ApiClient.TMDB.fetchTvDetails(id)

    suspend fun fetchSimilarMovies(id: Int) = ApiClient.TMDB.fetchSimilarMovies(id)

    suspend fun fetchSimilarTvShows(id: Int) = ApiClient.TMDB.fetchSimilarTvs(id)

    suspend fun fetchMovieVideos(id: Int) = ApiClient.TMDB.fetchMovieVideos(id)

    suspend fun fetchTvShowVideos(id: Int) = ApiClient.TMDB.fetchTvVideos(id)

    suspend fun fetchTvShowSeasonDetails(id: Int, seasonNumber: Int) = ApiClient.TMDB.fetchTvSeasonDetails(id, seasonNumber)

    suspend fun fetchPopularMovies(page: Int) = ApiClient.TMDB.fetchPopularMovies(page)

    suspend fun fetchSearchResults(query: String, page: Int) = ApiClient.TMDB.fetchSearchResults(query, page)

    const val NETWORK_PAGE_SIZE = 20
}