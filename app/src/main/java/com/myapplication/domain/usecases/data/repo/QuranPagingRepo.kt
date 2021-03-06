package com.myapplication.domain.usecases.data.repo

import android.content.Context
import androidx.paging.PagingData
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import kotlinx.coroutines.flow.Flow

interface QuranPagingRepo {

   suspend fun getQuranPagingData(context: Context,page:Int,initPage:Int): List<QuranVersesEntity>
}