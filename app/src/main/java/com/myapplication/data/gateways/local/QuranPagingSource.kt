package com.myapplication.data.gateways.local

import android.content.Context
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room.Room
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.dao.qurangateway.QuranDao

//class QuranPagingSource  constructor(private val quranDao: QuranDao): PagingSource<Int, QuranVersesEntity>() {
//
//
//    override fun getRefreshKey(state: PagingState<Int, QuranVersesEntity>): Int? {
//        return  state.anchorPosition?.let { anchorPosition ->
//            val anchorPage = state.closestPageToPosition(anchorPosition)
//            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
//        }
//
//    }
//
//    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, QuranVersesEntity> {
//        val currentPage = params.key ?: 1
//
//        return try {
//
//            quranDao.getQuranPaged(currentPage).let {
//
////                val data = it
////                LoadResult.Page(data = data,prevKey = if (currentPage == 1) null else currentPage -1,
////                    nextKey = if (currentPage == quranDao.getLastPage()) null else currentPage+1)
////            }
//
//        }catch (e:Exception)
//        {
//
//            LoadResult.Error(e)
//        }
//    }








  //  }
