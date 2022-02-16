package com.myapplication.ui.fragments.quran

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.repositories.QuranPagingRepositoryImpl
import com.myapplication.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class QuranViewModel(application: Application): AndroidViewModel(application)  {
    var preference = SharedPreferencesRepository(application)


    var quranFlow:MutableStateFlow<List<QuranPage?>?> = MutableStateFlow(null)
    var quranVersesMutableLiveData:MutableLiveData<List<QuranVersesEntity>?> = MutableLiveData()

    private val pages:MutableList<QuranPage?> = mutableListOf()

    private val quranRepository=QuranPagingRepositoryImpl()

    init {

            //getPagingData(1)


    }


   fun getPagingData(page:Int)
    {
//       return quranRepository.getQuranPagingData(getApplication()).stateIn(viewModelScope,
//           SharingStarted.WhileSubscribed(5000), PagingData.empty())

        viewModelScope.launch(Dispatchers.IO) {
            var initialPage = page-10
            if (initialPage <= 0)
            {
                initialPage = 1
            }
            val verses =quranRepository.getQuranPagingData(getApplication(),page,initialPage)

            val quranPages = preparePages(verses,page+10,initialPage)
            if (quranPages.isNotEmpty())
            {
                quranPages.forEach {

                    pages.add(index = it.page?.minus(1)!!,it)
                }
            }


            quranFlow.emit(pages.toList())
            }
        }

        }


   private fun preparePages(list:List<QuranVersesEntity>,start:Int,end:Int):List<QuranPage>
   {
       var pageData: List<QuranVersesEntity> = listOf()
       var pages:MutableList<QuranPage> = mutableListOf()
       var quranPage:QuranPage? = null
       if (list.isNotEmpty()) {

           for (page in start..end)
           {
               pageData=  list.filter {

                   it?.page == page


               }
               if (pageData.isNotEmpty())
               {

                   quranPage = QuranPage(pageData,pageData.last()?.page,pageData.last()?.line)
                   pageData = listOf()
               }

               if (quranPage != null) {
                   pages.add(quranPage)
               }

           }


       }
       return pages.toList()
   }

