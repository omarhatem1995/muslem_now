package com.myapplication.domain.usecases.data.repo

import android.content.Context
import com.myapplication.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.QuranVersesEntity
import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse

interface GooglePlacesRepo {
    suspend fun getPlaces(location:String): ApiResponse<GooglePlacesResponse,Error>

}