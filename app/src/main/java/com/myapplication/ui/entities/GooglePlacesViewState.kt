package com.myapplication.ui.entities

import com.myapplication.data.entities.model.googleplaces.GooglePlacesResponse

sealed class GooglePlacesViewState {
    data class  Loading(val show :Boolean) : GooglePlacesViewState()
    object  NetworkFailure : GooglePlacesViewState()
    data class  Data(val data : GooglePlacesResponse) : GooglePlacesViewState()

}