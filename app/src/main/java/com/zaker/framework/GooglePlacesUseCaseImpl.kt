package com.zaker.framework

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.googleplaces.GooglePlacesResponse
import com.zaker.domain.usecases.data.repo.GooglePlacesRepo
import com.zaker.domain.usecases.ui.GooglePlacesUseCases

class GooglePlacesUseCaseImpl (
    val googlePlacesRepo: GooglePlacesRepo
) : GooglePlacesUseCases.GetPlaces {
    override suspend fun invoke(
        location:String
    ): ApiResponse<GooglePlacesResponse, Error> =googlePlacesRepo.getPlaces(location)

}