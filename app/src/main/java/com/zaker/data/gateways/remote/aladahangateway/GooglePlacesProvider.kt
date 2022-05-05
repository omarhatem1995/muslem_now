package com.zaker.data.gateways.remote.aladahangateway

import com.zaker.data.core.ServiceCore
import com.zaker.data.core.networkresponsefactory.ApiResponseAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GooglePlacesProvider {
    fun provideGateWay(): GooglePlacesGateway {

        return Retrofit.Builder()
            .client(ServiceCore.provideOkHttpClient())
            .baseUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/")
            .addCallAdapterFactory(ApiResponseAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GooglePlacesGateway::class.java)
    }
}