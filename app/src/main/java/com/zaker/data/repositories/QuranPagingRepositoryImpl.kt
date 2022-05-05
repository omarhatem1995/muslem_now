package com.zaker.data.repositories

import android.content.Context
import androidx.paging.PagingConfig
import androidx.paging.PagingConfig.Companion.MAX_SIZE_UNBOUNDED
import com.zaker.data.entities.model.QuranVersesEntity
import com.zaker.data.gateways.dao.MuslemNowDataBase
import com.zaker.domain.usecases.data.repo.QuranPagingRepo

class QuranPagingRepositoryImpl :QuranPagingRepo {



    override suspend fun getQuranPagingData(context: Context,page:Int,initPage:Int): List<QuranVersesEntity>{
        val dao = MuslemNowDataBase.getDataBase(context).alQuranDao()
//        val source = QuranPagingSource(dao)
//
//        return Pager(defaultPageConfig() , pagingSourceFactory = {source}).flow.
//        flowOn(Dispatchers.IO)



         return  MuslemNowDataBase.getDataBase(context).alQuranDao().getQuranPaged(page,initPage)

    }


    private fun defaultPageConfig():PagingConfig
    {
        return PagingConfig(pageSize = 500,
             prefetchDistance = 20,
            enablePlaceholders = false, initialLoadSize = 800, maxSize = MAX_SIZE_UNBOUNDED
     )
    }
}