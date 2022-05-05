package com.zaker.ui.entities

import com.zaker.data.entities.model.AzkarModel

sealed class AzkarRoomViewState {
    data class  Data(val data : List<AzkarModel>) : AzkarRoomViewState()

}