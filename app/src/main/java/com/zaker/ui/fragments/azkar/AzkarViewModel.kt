package com.zaker.ui.fragments.azkar

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.zaker.data.entities.model.AzkarModel
import com.zaker.data.gateways.dao.MuslemNowDataBase
import com.zaker.data.repositories.SharedPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AzkarViewModel (application: Application): AndroidViewModel(application) {

    fun saveAzkar(applicationContext: Context, azkarModelList: List<AzkarModel>){
        val db = MuslemNowDataBase.getDataBase(applicationContext)

        val saveAzkarDao = db.alAzkarDao()

        viewModelScope.launch(Dispatchers.IO){
            for(x in azkarModelList) {
                saveAzkarDao.addAzkar(x)
            }

        }

    }
    val viewStateAzkar: MutableLiveData< List<AzkarModel>> = MutableLiveData()

    fun update(userPressedCount:Int,id:Int,listItems : List<AzkarModel>){
        val db = MuslemNowDataBase.getDataBase(getApplication())

        val updateAzkarCountDao = db.alAzkarDao()

        viewModelScope.launch(Dispatchers.IO) {
            updateAzkarCountDao.updateAzkar(listItems)
        }
    }
    var preference = SharedPreferencesRepository(application)
    fun getSpecificDayAzkar(category: String){
        val db = MuslemNowDataBase.getDataBase(getApplication())

        val getAzkarFromCategory = db.alAzkarDao()
        viewModelScope.launch(Dispatchers.IO) {
            val getAzkar : List<AzkarModel> = getAzkarFromCategory.getSpecificDayAzkar2(category)
            viewStateAzkar.postValue(getAzkar)
        }

    }
}