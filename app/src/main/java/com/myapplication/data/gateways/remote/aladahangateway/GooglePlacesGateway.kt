package com.myapplication.data.gateways.remote.aladahangateway

import com.myapplication.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url
import java.lang.Error

interface GooglePlacesGateway {
//=29.9734075,30.9484463
    @GET
    suspend fun getPlaces(
    @Url url : String
        ): ApiResponse<GooglePlacesResponse, Error>

}