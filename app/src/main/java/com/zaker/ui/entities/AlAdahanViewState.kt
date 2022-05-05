package com.zaker.ui.entities

import com.zaker.data.entities.model.Datum

sealed class AlAdahanViewState {
    data class  Loading(val show :Boolean) : AlAdahanViewState()
    object  NetworkFailure : AlAdahanViewState()
    data class  Data(val data : List<Datum>) : AlAdahanViewState()

}