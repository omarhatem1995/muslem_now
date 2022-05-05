package com.zaker.data.gateways.remote.aladahangateway

import com.zaker.data.core.ServiceCore
import com.zaker.data.core.networkresponsefactory.ApiResponseAdapterFactory
import com.zaker.domain.core.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AlAdahanGatewayProvider {
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