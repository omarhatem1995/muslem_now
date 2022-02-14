package com.myapplication.ui.fragments.quran

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.repositories.QuranPagingRepositoryImpl
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuranViewModel(application: Application): AndroidViewModel(application)  {
    var preference = SharedPreferencesRepository(application)

    private val quranDao = MuslemNowDataBase.getDataBase(application).alQuranDao()

   // private val quranPagingSource = QuranPagingSource(quranDao)

    var quranFlow:MutableStateFlow<PagingData<QuranVersesEntity>?> = MutableStateFlow(null)
    var quranVersesMutableLiveData:MutableLiveData<List<QuranVersesEntity>?> = MutableLiveData()

    private val pages:MutableList<QuranPage> = mutableListOf()

    private val quranRepository=QuranPagingRepositoryImpl()

    init {
        viewModelScope.launch {
            getPagingData()
        }

    }


    suspend fun getPagingData(): Flow<PagingData<QuranVersesEntity>>
    {
       return quranRepository.getQuranPagingData(getApplication()).cachedIn(viewModelScope).stateIn(viewModelScope,
           SharingStarted.WhileSubscribed(5000), PagingData.empty())

        }













//    .collect {
//        //val quranPage = QuranPage(it,page,it.last().line)
//        // pages.add(quranPage)
//        // quranVersesMutableLiveData.postValue(it)
//        quranFlow.value = it
//        Log.d("pages", "getPagingData: $pages", )
//    }

    suspend fun getLastPage(currentPage:Int):Boolean
    {
        return quranDao.getLastPage()==currentPage
    }

}