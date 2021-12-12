package com.myapplication.domain.usecases.ui

import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.entities.model.Datum
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.entities.model.Timings

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