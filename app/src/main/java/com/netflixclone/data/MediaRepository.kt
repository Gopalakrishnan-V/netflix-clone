package com.netflixclone.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.netflixclone.data_models.Movie
import com.netflixclone.data_models.TvShow
import com.netflixclone.network.services.ApiClient
import kotlinx.coroutines.flow.Flow
import retrofit2.http.Path
import retrofit2.http.Query

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
        upcomingMovies = Pager(config = defaultPagingConfig,
            pagingSourceFactory = { UpcomingMoviesPagingSource() }).flow
        return upcomingMovies!!
    }

    fun getPopularMoviesStream(): Flow<PagingData<Movie>> {
        if (popularMovies != null) {
            return popularMovies!!
        }
        popularMovies = Pager(config = defaultPagingConfig,
            pagingSourceFactory = { PopularMoviesPagingSource() }).flow
        return popularMovies!!
    }

    fun getPopularTvShowsStream(): Flow<PagingData<TvShow>> {
        if (popularTvShows != null) {
            return popularTvShows!!
        }
        popularTvShows = Pager(config = defaultPagingConfig,
            pagingSourceFactory = { PopularTvShowsPagingSource() }).flow
        return popularTvShows!!
    }

    suspend fun fetchTrending(timeWindow: String = "week", page: Int = 1) =
        ApiClient.TMDB.fetchTrending(timeWindow, page)

    suspend fun fetchMovieDetails(id: Int) = ApiClient.TMDB.fetchMovieDetails(id)

    suspend fun fetchTvShowDetails(id: Int) = ApiClient.TMDB.fetchTvDetails(id)

    suspend fun fetchSimilarMovies(id: Int) = ApiClient.TMDB.fetchSimilarMovies(id)

    suspend fun fetchSimilarTvShows(id: Int) = ApiClient.TMDB.fetchSimilarTvs(id)

    suspend fun fetchMovieVideos(id: Int) = ApiClient.TMDB.fetchMovieVideos(id)

    suspend fun fetchTvShowVideos(id: Int) = ApiClient.TMDB.fetchTvVideos(id)

    suspend fun fetchTvShowSeasonDetails(id: Int, seasonNumber: Int) =
        ApiClient.TMDB.fetchTvSeasonDetails(id, seasonNumber)

    suspend fun fetchPopularMovies(page: Int) = ApiClient.TMDB.fetchPopularMovies(page)

    suspend fun fetchPopularTvShows(page: Int) = ApiClient.TMDB.fetchPopularTvShows(page)

    suspend fun fetchSearchResults(query: String, page: Int) =
        ApiClient.TMDB.fetchSearchResults(query, page)

    suspend fun discoverMovies(
        withGenres: String? = null,
        sortBy: String? = null,
        voteCountGreater: Int? = null,
        withWatchProviders: Int? = null,
        watchRegion: String? = null,
    ) = ApiClient.TMDB.discoverMovies(withGenres, sortBy, voteCountGreater, withWatchProviders, watchRegion)

    suspend fun discoverTvShows(
        withGenres: String? = null,
        sortBy: String? = null,
        voteCountGreater: Int? = null,
        withWatchProviders: Int? = null,
        watchRegion: String? = null,
    ) = ApiClient.TMDB.discoverTvShows(withGenres, sortBy, voteCountGreater, withWatchProviders, watchRegion)

    const val NETWORK_PAGE_SIZE = 20
}