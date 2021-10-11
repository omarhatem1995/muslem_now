package com.myapplication.domain.usecases.data.repo

import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel

interface AladahanRepo {
    suspend fun getAladahanTime(latitude:String,
    longitude:String,method:String,month:String,year:String) : ApiResponse<AlAdahanResponseModel, Error>

}