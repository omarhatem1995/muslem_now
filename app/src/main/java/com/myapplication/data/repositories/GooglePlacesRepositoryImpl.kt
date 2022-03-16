package com.myapplication.data.repositories

import com.myapplication.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGateway
import com.myapplication.data.gateways.remote.aladahangateway.GooglePlacesGateway
import com.myapplication.domain.usecases.data.repo.AlAdahanRepo
import com.myapplication.domain.usecases.data.repo.GooglePlacesRepo

class GooglePlacesRepositoryImpl (
    val googlePlacesGateway: GooglePlacesGateway
): GooglePlacesRepo{
    override suspend fun getPlaces(location:String): ApiResponse<GooglePlacesResponse, Error> {
        return googlePlacesGateway.getPlaces("json?sensor=false&key=AIzaSyB_mbG3vnY5ABJA4Ay9L_yKbtFy5D5gltg&location=$location&radius=500000&types=mosque")
    }

}