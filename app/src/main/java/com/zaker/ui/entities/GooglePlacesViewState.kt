package com.zaker.ui.entities

import com.zaker.data.entities.model.googleplaces.GooglePlacesResponse

sealed class GooglePlacesViewState {
    data class  Loading(val show :Boolean) : GooglePlacesViewState()
    object  NetworkFailure : GooglePlacesViewState()
    data class  Data(val data : GooglePlacesResponse) : GooglePlacesViewState()

}