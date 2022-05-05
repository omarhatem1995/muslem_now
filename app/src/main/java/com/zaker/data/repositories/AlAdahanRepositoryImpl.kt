package com.zaker.data.repositories

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.AlAdahanResponseModel
import com.zaker.data.gateways.remote.aladahangateway.AlAdahanGateway
import com.zaker.domain.usecases.data.repo.AlAdahanRepo

class AlAdahanRepositoryImpl (
    val alAdahanGateway: AlAdahanGateway
): AlAdahanRepo{
    override suspend fun getAladahanTime(
        latitude: String,
        longitude: String,
        method: String,
        month: String,
        year: String
    ): ApiResponse<AlAdahanResponseModel, Error> {
        return alAdahanGateway.getAladahan(latitude, longitude, method, month, year)
    }

}