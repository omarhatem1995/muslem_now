package com.zaker.domain.usecases.data.repo

import android.content.Context
import com.zaker.data.entities.model.QuranVersesEntity

interface QuranPagingRepo {

   suspend fun getQuranPagingData(context: Context,page:Int,initPage:Int): List<QuranVersesEntity>
}