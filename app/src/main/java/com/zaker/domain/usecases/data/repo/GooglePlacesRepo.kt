package com.zaker.domain.usecases.data.repo

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.googleplaces.GooglePlacesResponse

interface GooglePlacesRepo {
    suspend fun getPlaces(location:String): ApiResponse<GooglePlacesResponse,Error>

}