package com.zaker.domain.usecases.data.repo

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.AlAdahanResponseModel

interface AlAdahanRepo {
    suspend fun getAladahanTime(latitude:String,
    longitude:String,method:String,month:String,year:String) : ApiResponse<AlAdahanResponseModel, Error>

}