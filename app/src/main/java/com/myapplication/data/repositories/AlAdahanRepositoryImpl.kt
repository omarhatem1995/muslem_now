package com.myapplication.data.repositories

import android.util.Log
import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGateway
import com.myapplication.domain.usecases.data.repo.AlAdahanRepo

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