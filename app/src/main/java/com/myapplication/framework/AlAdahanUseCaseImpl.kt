package com.myapplication.framework

import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.domain.usecases.data.repo.AlAdahanRepo
import com.myapplication.domain.usecases.ui.AlAdahanUseCases

class AlAdahanUseCaseImpl (
    val alAdahanRepo: AlAdahanRepo
) : AlAdahanUseCases.AlAdahanTiming {
    override suspend fun invoke(
        latitude: String,
        longitude: String,
        method: String,
        month: String,
        year: String
    ): ApiResponse<AlAdahanResponseModel, Error> =alAdahanRepo.getAladahanTime(latitude, longitude, method, month, year)

}