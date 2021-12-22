package com.myapplication.ui.entities

import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.data.entities.model.Datum

sealed class AlAzkarViewState {
    data class  Loading(val show :Boolean) : AlAzkarViewState()
    object  NetworkFailure : AlAzkarViewState()
    data class  Data(val data : List<AzkarModel>) : AlAzkarViewState()

}