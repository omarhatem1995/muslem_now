package com.myapplication.domain.usecases.data.repo

import androidx.paging.PagingData
import com.myapplication.data.entities.model.QuranVersesEntity
import kotlinx.coroutines.flow.Flow

interface QuranPagingRepo {

    fun getQuranPagingData(): Flow<PagingData<QuranVersesEntity>>
}