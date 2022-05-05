package com.zaker.domain.usecases.ui

import com.zaker.data.entities.base.ApiResponse
import com.zaker.data.entities.model.AlAdahanResponseModel
import com.zaker.data.entities.model.Datum
import com.zaker.data.entities.model.PrayerTimeModel

interface AlAdahanUseCases {
    interface View {
        fun renderParentTimings(data : List<Datum>)
        fun renderLoading(show: Boolean)
        fun renderNetworkFailure()
    }
    interface AlAdahanTiming {
        suspend fun invoke(latitude:String,longitude:String,
        method:String,month:String,year:String): ApiResponse<AlAdahanResponseModel, Error>
    }
    interface SaveAdahanTiming{
        suspend fun invoke(adahanList : PrayerTimeModel)
    }
}