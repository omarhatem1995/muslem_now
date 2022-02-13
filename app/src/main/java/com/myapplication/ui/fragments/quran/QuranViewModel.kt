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

    var quranFlow:MutableLiveData<MutableList<QuranPage>?> = MutableLiveData(null)
    var quranVersesMutableLiveData:MutableLiveData<List<QuranVersesEntity>?> = MutableLiveData()
    var currentPage:Int = 1

    val isLastPageState:MutableLiveData<Boolean> = MutableLiveData(false)
    val pageSize:MutableLiveData<Int> = MutableLiveData(1)

    private var pages:MutableList<QuranPage> = mutableListOf()

    private val quranRepository=QuranPagingRepositoryImpl()

    init {
        viewModelScope.launch {
            getPagingData()
        }

    }


     fun getPagingData()
    {
        viewModelScope.launch {
            quranRepository.getQuranPagingData(getApplication(),currentPage)
                .collect {
                    Log.e("pageNum", "getPagingData: $currentPage ", )

                    //quranVersesMutableLiveData.postValue(it)

                    if (it != null && it.isNotEmpty())
                    {
                        val quranPage = QuranPage(it,currentPage,it.last().line)
                        val oldList = pages
                        pages.clear()
                        oldList.add(quranPage)
                        pages.addAll(oldList)


                        quranFlow.value = pages

                        Log.d("pages", "getPagingData: $oldList ")
                        currentPage += 1

                        getLastPage()
                        pageSize.value = it.size
                    }


                   // Log.d("pages", "getPagingData: $pages", )

                }
        }



    }

    suspend fun getLastPage()
    {
           val lastOfPaging = quranDao.getLastPage()
            isLastPageState.value = lastOfPaging==currentPage
        Log.e("lastPage", "getLastPage: $lastOfPaging , ${isLastPageState.value} ", )

    }

}