package com.myapplication.data.gateways.remote.aladahangateway

import com.myapplication.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.Error

interface AlAdahanGateway {

    @GET("v1/calendar")
    suspend fun getAladahan(
        @Query("latitude") latitude: String,
        @Query("longitude") longitude: String,
        @Query("method") method: String,
        @Query("month") month: String,
        @Query("year") year: String,

        ): ApiResponse<AlAdahanResponseModel, Error>

}