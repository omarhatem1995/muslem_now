package com.myapplication.ui.fragments.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.local.QuranPagingSource
import com.myapplication.data.repositories.QuranPagingRepositoryImpl
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow

class QuranViewModel(application: Application): AndroidViewModel(application)  {
    var preference = SharedPreferencesRepository(application)

    private val quranDao = MuslemNowDataBase.getDataBase(application).alQuranDao()

    private val quranPagingSource = QuranPagingSource(quranDao)

    private val quranRepository=QuranPagingRepositoryImpl(quranPagingSource)

    fun getPagingData(): Flow<PagingData<QuranVersesEntity>>
    {
        return quranRepository.getQuranPagingData()
            .cachedIn(viewModelScope)

    }

}