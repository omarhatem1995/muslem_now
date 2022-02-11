package com.myapplication.data.repositories

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import androidx.paging.PagingData
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.domain.usecases.data.repo.QuranPagingRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class QuranPagingRepositoryImpl :QuranPagingRepo {
    override fun getQuranPagingData(context: Context,page:Int): Flow<List<QuranVersesEntity>> {
//        return Pager(defaultPageConfig(),pagingSourceFactory = {MuslemNowDataBase.getDataBase(context).alQuranDao().getQuranPaged()}).flow.
//        flowOn(Dispatchers.IO)


           return  MuslemNowDataBase.getDataBase(context).alQuranDao().getQuranPaged(page)




    }


    private fun defaultPageConfig():PagingConfig
    {
        return PagingConfig(pageSize = 1,
             prefetchDistance = 2,
            enablePlaceholders = false, //initialLoadSize = 200, maxSize = MAX_SIZE_UNBOUNDED
     )
    }
}