package com.netflixclone.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.netflixclone.data_models.FeedItem
import kotlinx.coroutines.flow.Flow

object FeedRepository {
    private const val NETWORK_PAGE_SIZE = 20
    private val DEFAULT_PAGING_CONFIG = PagingConfig(
            pageSize = FeedRepository.NETWORK_PAGE_SIZE,
            enablePlaceholders = false
    )
}