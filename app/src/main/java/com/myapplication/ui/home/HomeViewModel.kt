package com.myapplication.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.el_mared.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.aladahangateway.AlAdahanDatabase
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGateway
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGatewayProvider
import com.myapplication.data.repositories.AlAdahanRepositoryImpl
import com.myapplication.domain.usecases.ui.AlAdahanUseCases
import com.myapplication.framework.AlAdahanUseCaseImpl
import com.myapplication.ui.entities.AlAdahanViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher

class HomeViewModel : ViewModel() {
    val viewStateAlAdahan: MutableLiveData<AlAdahanViewState> = MutableLiveData()
    val alAdahanUseCase = createAlAdahanUseCase()



    fun savePrayerTimes(applicationContext: Context,prayerTimesList: List<PrayerTimeModel>){
        val db = AlAdahanDatabase.getDataBase(applicationContext)

        val savePrayerDao = db.alAdahanDao()
        viewModelScope.launch(Dispatchers.IO){

            val alAdahan = savePrayerDao.addPrayerTimes(prayerTimesList)
            Log.d("PrayerTimes","alAdahan : "+ alAdahan)
           /* val getAlAdahan : LiveData<List<PrayerTimeModel>> = savePrayerDao.getCurrentPrayerTimes()
            Log.d("getPrayerTimes","data " +  getAlAdahan.value)*/
        }


    }
    fun getPrayerTimes(applicationContext: Context) : LiveData<List<PrayerTimeModel>>{
        val db = AlAdahanDatabase.getDataBase(applicationContext)

        val savePrayerDao = db.alAdahanDao()

            /*val alAdahan = savePrayerDao.addPrayerTimes(prayerTimesList)
            Log.d("PrayerTimes","alAdahan : "+ alAdahan)*/

            val getAlAdahan : LiveData<List<PrayerTimeModel>> = savePrayerDao.getCurrentPrayerTimes()
            Log.d("getPrayerTimes","data " +  getAlAdahan.value)

        return getAlAdahan

    }


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

/*    private fun saveAlAdahanUseCase(context: Context): AlAdahanUseCases.SaveAdahanTiming {
        val alAdahanDao : AlAdahanDatabase.AppDatabase = AlAdahanDaoProvider.provideGateway(context)
        return SavePrayerTimesUseCaseImpl(SavePrayerTimesRepositoryImpl(alAdahanDao))
    }*/
}