package com.myapplication.ui.fragments.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.local.QuranPagingSource
import com.myapplication.data.repositories.QuranPagingRepositoryImpl
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class QuranViewModel(application: Application): AndroidViewModel(application)  {
    var preference = SharedPreferencesRepository(application)

    private val quranDao = MuslemNowDataBase.getDataBase(application).alQuranDao()

    private val quranPagingSource = QuranPagingSource(quranDao)

    private val quranRepository=QuranPagingRepositoryImpl(quranPagingSource)

    val viewStateAzkar: MutableLiveData<List<QuranVersesEntity>> = MutableLiveData()

    fun getSpecificTest(page: Int) {
        val db = MuslemNowDataBase.getDataBase(getApplication())

        val getAzkarFromCategory = db.alQuranDao()
        viewModelScope.launch(Dispatchers.IO) {
            val getAzkar: List<QuranVersesEntity> = getAzkarFromCategory.getQuranData(page)
            viewStateAzkar.postValue(getAzkar)
        }
    }

    fun getPagingData(): Flow<PagingData<QuranVersesEntity>>
    {
        return quranRepository.getQuranPagingData()
            .cachedIn(viewModelScope)

    }

}