package com.zaker.ui.entities

import com.zaker.data.entities.model.AzkarModel

sealed class AlAzkarViewState {
    data class  Loading(val show :Boolean) : AlAzkarViewState()
    object  NetworkFailure : AlAzkarViewState()
    data class  Data(val data : List<AzkarModel>) : AlAzkarViewState()

}