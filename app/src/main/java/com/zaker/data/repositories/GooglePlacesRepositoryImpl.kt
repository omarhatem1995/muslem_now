package com.zaker.data.repositories

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.googleplaces.GooglePlacesResponse
import com.zaker.data.gateways.remote.aladahangateway.GooglePlacesGateway
import com.zaker.domain.usecases.data.repo.GooglePlacesRepo

class GooglePlacesRepositoryImpl (
    val googlePlacesGateway: GooglePlacesGateway
): GooglePlacesRepo{
    override suspend fun getPlaces(location:String): ApiResponse<GooglePlacesResponse, Error> {
        return googlePlacesGateway.getPlaces("json?sensor=false&key=AIzaSyB_mbG3vnY5ABJA4Ay9L_yKbtFy5D5gltg&location=$location&radius=500000&types=mosque")
    }

}