package com.myapplication.ui.fragments.quran

import android.app.Application
import android.util.Log
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


    var quranFlow:MutableStateFlow<MutableList<QuranPage?>?> = MutableStateFlow(null)
    var quranVersesMutableLiveData:MutableLiveData<List<QuranVersesEntity>?> = MutableLiveData()

   // private var pages:ArrayList<QuranPage?> = ArrayList()

    private val quranRepository=QuranPagingRepositoryImpl()

    init {

//        for (num in 0..603)
//        {
//            pages.add(QuranPage())
//        }


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
            var verses:List<QuranVersesEntity>? = null
            verses =quranRepository.getQuranPagingData(getApplication(),page+10,initialPage)
            if (page in 595..604)
            {
                verses =quranRepository.getQuranPagingData(getApplication(),(604-page)+page,initialPage)

            }






            val pages = pagesReset()


            Log.e("verses", "getPagingData: ${pages.size}", )

            val quranPages = preparePages(verses!!,if (page == 604)page else page+10,initialPage)
            if (quranPages.isNotEmpty())
            {
                quranPages.forEach {

                    pages.add(index = it.page?.minus(1)!!,it)
                }
            }


            Log.e("pages", "getPagingData: $pages", )

            quranFlow.emit(pages)
            }
        }




   private fun preparePages(list:List<QuranVersesEntity>,start:Int,end:Int):List<QuranPage>
   {
       var pages:MutableList<QuranPage> = mutableListOf()
       if (list.isNotEmpty()) {
           Log.e("compare", "preparePages: $start , $end", )
           var pageData: List<QuranVersesEntity> = listOf()

           var quranPage:QuranPage? = null

           for (page in end..start)
           {
               Log.e("pageNumber", "preparePages: $page ", )
               pageData=  list.filter {


                   it.page == page




               }
               if (pageData.isNotEmpty())
               {

                   quranPage = QuranPage(pageData,pageData.last()?.page,pageData.last()?.line)
                   Log.e("perPage", "preparePages: $quranPage", )
                   //pageData = listOf()
               }

               if (quranPage != null) {
                   pages.add(quranPage)
               }

           }


       }
       Log.e("toPages", "preparePages: $pages", )
       return pages.toList()
   }

  private fun pagesReset():ArrayList<QuranPage?>
   {
       val pages:ArrayList<QuranPage?> = ArrayList()
       for (num in 0..603)
       {
           pages.add(QuranPage())
       }

       return pages
   }
}