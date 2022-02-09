package com.myapplication.data.repositories

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import androidx.paging.PagingData
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.local.QuranPagingSource
import com.myapplication.domain.usecases.data.repo.QuranPagingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class QuranPagingRepositoryImpl constructor(private val quranPagingSource: QuranPagingSource):QuranPagingRepo {
    override fun getQuranPagingData(): Flow<PagingData<QuranVersesEntity>> {
        return Pager(defaultPageConfig(),pagingSourceFactory = {quranPagingSource}).flow.
        flowOn(Dispatchers.IO)
    }


    private fun defaultPageConfig():PagingConfig
    {
        return PagingConfig(pageSize = 15,prefetchDistance = 30,
            enablePlaceholders = false,initialLoadSize = 30,maxSize = MAX_SIZE_UNBOUNDED)
    }
}