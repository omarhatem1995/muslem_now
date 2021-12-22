package com.myapplication.ui.azkar

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myapplication.data.entities.model.AzkarModel
import com.myapplication.data.entities.model.PrayerTimeModel
import com.myapplication.data.gateways.dao.MuslemNowDataBase
import com.myapplication.data.repositories.SharedPreferencesRepository
import com.myapplication.ui.entities.AlAdahanViewState
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
    fun update(userPressedCount:Int,id:Int,listItems : List<AzkarModel>){
        val db = MuslemNowDataBase.getDataBase(getApplication())

        val updateAzkarCountDao = db.alAzkarDao()
        Log.d("asldksalad" , userPressedCount.toString() + " , " + id.toString())

        viewModelScope.launch(Dispatchers.IO) {
//            updateAzkarCountDao.updateAzkarCount(userPressedCount,id)
            updateAzkarCountDao.updateAzkar(listItems)
        }
    }
    var preference = SharedPreferencesRepository(application)

    fun getSpecificDayAzkar(category: String): LiveData<List<AzkarModel>>{
        val db = MuslemNowDataBase.getDataBase(getApplication())

        val getAzkarFromCategory = db.alAzkarDao()
        viewModelScope.launch(Dispatchers.IO) {
            getAzkarFromCategory.getSpecificDayAzkar(category)

        }
        val getAzkar : LiveData<List<AzkarModel>> = getAzkarFromCategory.getSpecificDayAzkar(category)

        return getAzkar
    }
}