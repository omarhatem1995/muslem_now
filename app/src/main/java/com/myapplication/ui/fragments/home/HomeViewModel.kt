package com.myapplication.ui.fragments.home

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.myapplication.data.entities.base.ApiResponse
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGateway
import com.myapplication.data.gateways.remote.aladahangateway.AlAdahanGatewayProvider
import com.myapplication.data.repositories.AlAdahanRepositoryImpl
import com.myapplication.data.repositories.SharedPreferencesRepository
import com.myapplication.domain.usecases.ui.AlAdahanUseCases
import com.myapplication.framework.AlAdahanUseCaseImpl
import com.myapplication.ui.entities.AlAdahanViewState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel (application: Application):AndroidViewModel(application) {
    val viewStateAlAdahan: MutableLiveData<AlAdahanViewState> = MutableLiveData()
    val alAdahanUseCase = createAlAdahanUseCase()



    fun savePrayerTimes(applicationContext: Context,prayerTimesList: List<PrayerTimeModel>){
        val db = MuslemNowDataBase.getDataBase(applicationContext)

        val savePrayerDao = db.alAdahanDao()

        viewModelScope.launch(Dispatchers.IO){
        for(x in prayerTimesList) {

            savePrayerDao.addPrayerTimes(x)
        }

        }

    }
    fun getPrayerTimes(applicationContext: Context) : LiveData<List<PrayerTimeModel>>{
        val db = MuslemNowDataBase.getDataBase(applicationContext)

        val savePrayerDao = db.alAdahanDao()


            val getAlAdahan : LiveData<List<PrayerTimeModel>> = savePrayerDao.getCurrentPrayerTimes()

        return getAlAdahan

    }
    fun getPrayerTimesForSpecificDate(date:String,applicationContext: Context) : LiveData<List<PrayerTimeModel>>{
        val db = MuslemNowDataBase.getDataBase(applicationContext)

        val getPrayerDao = db.alAdahanDao()


            val getAlAdahan : LiveData<List<PrayerTimeModel>> = getPrayerDao.getSpecificDayPrayerTimes(date).asLiveData(context = Dispatchers.IO)

        return getAlAdahan

    }
    var preference = SharedPreferencesRepository(application)

    fun getCityFromPreferences():String?{
        return if(preference.getCity()!=null)
            preference.getCity()!!
        else
            null

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