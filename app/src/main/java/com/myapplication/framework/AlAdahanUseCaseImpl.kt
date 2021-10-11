package com.myapplication.framework

import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.domain.usecases.data.repo.AladahanRepo
import com.myapplication.domain.usecases.ui.AlAdahanUseCases

class AlAdahanUseCaseImpl (
    val aladahanRepo: AladahanRepo
) : AlAdahanUseCases.AlAdahanTiming {
    override suspend fun invoke(
        latitude: String,
        longitude: String,
        method: String,
        month: String,
        year: String
    ): ApiResponse<AlAdahanResponseModel, Error> =aladahanRepo.getAladahanTime(latitude, longitude, method, month, year)

}