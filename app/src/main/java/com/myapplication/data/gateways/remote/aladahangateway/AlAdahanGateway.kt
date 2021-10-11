package com.myapplication.data.gateways.remote.aladahangateway

import com.example.el_mared.data.core.NetworkResponseFactory.ApiResponseAdapterFactory
import com.example.el_mared.data.core.ServiceCore
import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.domain.core.Constants
import com.myapplication.data.entities.model.AlAdahanResponseModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    companion object {

        fun provideGateWay(): AlAdahanGateway {

            return Retrofit.Builder()
                .client(ServiceCore.provideOkHttpClient())
                .baseUrl(Constants.ADAHAN_BASEURL)
                .addCallAdapterFactory(ApiResponseAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AlAdahanGateway::class.java)
        }
    }
}