package com.myapplication.framework

import com.myapplication.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse
import com.myapplication.domain.usecases.data.repo.AlAdahanRepo
import com.myapplication.domain.usecases.data.repo.GooglePlacesRepo
import com.myapplication.domain.usecases.ui.AlAdahanUseCases
import com.myapplication.domain.usecases.ui.GooglePlacesUseCases

class GooglePlacesUseCaseImpl (
    val googlePlacesRepo: GooglePlacesRepo
) : GooglePlacesUseCases.GetPlaces {
    override suspend fun invoke(
        location:String
    ): ApiResponse<GooglePlacesResponse, Error> =googlePlacesRepo.getPlaces(location)

}