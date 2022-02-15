package com.myapplication.ui.fragments.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.repositories.QuranPagingRepositoryImpl
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QuranViewModel(application: Application): AndroidViewModel(application)  {
    var preference = SharedPreferencesRepository(application)


    var quranFlow:MutableStateFlow<List<QuranVersesEntity>?> = MutableStateFlow(null)
    var quranVersesMutableLiveData:MutableLiveData<List<QuranVersesEntity>?> = MutableLiveData()

    private val pages:MutableList<QuranVersesEntity> = mutableListOf()

    private val quranRepository=QuranPagingRepositoryImpl()

    init {

            getPagingData(1)


    }


   fun getPagingData(page:Int)
    {
//       return quranRepository.getQuranPagingData(getApplication()).stateIn(viewModelScope,
//           SharingStarted.WhileSubscribed(5000), PagingData.empty())

        viewModelScope.launch(Dispatchers.IO) {
            val verses =quranRepository.getQuranPagingData(getApplication(),page)

            quranFlow.emit(verses)
            }
        }

        }

