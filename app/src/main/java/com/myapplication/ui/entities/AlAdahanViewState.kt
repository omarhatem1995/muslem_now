package com.myapplication.ui.entities

import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.entities.model.Datum

sealed class AlAdahanViewState {
    data class  Loading(val show :Boolean) : AlAdahanViewState()
    object  NetworkFailure : AlAdahanViewState()
    data class  Data(val data : List<Datum>) : AlAdahanViewState()

}