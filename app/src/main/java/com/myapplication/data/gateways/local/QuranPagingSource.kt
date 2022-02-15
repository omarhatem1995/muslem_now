package com.myapplication.data.gateways.local

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Room
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.dao.qurangateway.QuranDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.last

//class QuranPagingSource  constructor(private val quranDao: QuranDao): PagingSource<Int, QuranVersesEntity>() {
//
//    val pages:MutableList<List<QuranVersesEntity>> = mutableListOf()
//
//
//    override fun getRefreshKey(state: PagingState<Int, QuranVersesEntity>): Int? {
//
//        Log.d("anchor", "getRefreshKey: ${state.anchorPosition}")
//        return state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//
//
//
//
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuranVersesEntity> {
//        val currentPage =  params.key?:1
//        pages.add(0, listOf())
//
//
//
//         try {
//
//
//          val result = quranDao.getQuranPaged(currentPage).last().let {
//               Log.d("pagingSource", "load: $currentPage")
//               val data = QuranPage(it,currentPage,it.last().line)
//
//               // val nextPage = data.page?.plus(1)
//
//               pages.add(it)
//               Log.e("pages", "load: $pages", )
//
//             return@let LoadResult.Page(
//                   data = it, prevKey = if (currentPage == 1) null else currentPage-1,
//                   nextKey = if (currentPage == 604) null else currentPage+1
//               )
//
//              return res
//           }
//
//        } catch (e: Exception) {
//
//            LoadResult.Error(e)
//        }
//
//    }
//
//
//
//}
//
//
//
//
//  //  }
