package com.myapplication.domain.usecases.ui

import com.myapplication.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.entities.model.Datum
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.entities.model.Timings
import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse

interface GooglePlacesUseCases {
    interface View {
        fun renderMosques(data : GooglePlacesResponse)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface GetPlaces {
        suspend fun invoke(location:String): ApiResponse<GooglePlacesResponse, Error>
    }

}