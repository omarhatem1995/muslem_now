package com.myapplication.ui.entities

import com.myapplication.data.entities.model.AzkarModel

sealed class AzkarRoomViewState {
    data class  Data(val data : List<AzkarModel>) : AzkarRoomViewState()

}