package com.myapplication.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.AlAdahanResponseModel
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGateway
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGatewayProvider
import com.myapplication.data.repositories.AlAdahanRepositoryImpl
import com.myapplication.domain.usecases.ui.AlAdahanUseCases
import com.myapplication.framework.AlAdahanUseCaseImpl
import com.myapplication.ui.entities.AlAdahanViewState
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    val viewStateAlAdahan: MutableLiveData<AlAdahanViewState> = MutableLiveData()
    val alAdahanUseCase = createAlAdahanUseCase()


    fun getAlAdahanAPI(
        latitude: String, longitude: String, method: String, month: String,
        year: String
    ) = viewModelScope.launch {
        viewStateAlAdahan.postValue(AlAdahanViewState.Loading(true))
        val response = alAdahanUseCase.invoke(latitude, longitude, method, month, year)
        when (response) {
            is ApiResponse.Success -> {
                val aladahan = response.body.data
                viewStateAlAdahan.postValue(AlAdahanViewState.Loading(false))
                viewStateAlAdahan.postValue(AlAdahanViewState.Data(aladahan))
            }
            is ApiResponse.NetworkError -> viewStateAlAdahan.postValue(
                AlAdahanViewState.NetworkFailure
            )
            else -> viewStateAlAdahan.postValue(AlAdahanViewState.Loading(false))
        }
    }

    private fun createAlAdahanUseCase(): AlAdahanUseCases.AlAdahanTiming {
        val alAdahanGateway: AlAdahanGateway = AlAdahanGatewayProvider.provideGateWay()
        return AlAdahanUseCaseImpl(AlAdahanRepositoryImpl(alAdahanGateway))
    }
}