package com.zaker.framework

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.AlAdahanResponseModel
import com.zaker.domain.usecases.data.repo.AlAdahanRepo
import com.zaker.domain.usecases.ui.AlAdahanUseCases

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