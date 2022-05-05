package com.zaker.domain.usecases.ui

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.googleplaces.GooglePlacesResponse

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