package com.myapplication.ui.fragments.quran

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.repositories.QuranPagingRepositoryImpl
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class QuranViewModel(application: Application): AndroidViewModel(application)  {
    var preference = SharedPreferencesRepository(application)

    private val quranDao = MuslemNowDataBase.getDataBase(application).alQuranDao()

   // private val quranPagingSource = QuranPagingSource(quranDao)

    var quranFlow:MutableStateFlow<List<QuranPage>?> = MutableStateFlow(null)
    var quranVersesMutableLiveData:MutableLiveData<List<QuranVersesEntity>?> = MutableLiveData()

    private val pages:MutableList<QuranPage> = mutableListOf()

    private val quranRepository=QuranPagingRepositoryImpl()

    init {
        viewModelScope.launch {
            getPagingData(603)
        }

    }


    suspend fun getPagingData(page:Int)
    {
        return quranRepository.getQuranPagingData(getApplication(),page)
            .collect {
                val quranPage = QuranPage(it,page,it.last().line)
                pages.add(quranPage)
                quranVersesMutableLiveData.postValue(it)
                quranFlow.value = pages
                Log.d("pages", "getPagingData: $pages", )
            }

    }

    suspend fun getLastPage(currentPage:Int):Boolean
    {
        return quranDao.getLastPage()==currentPage
    }

}