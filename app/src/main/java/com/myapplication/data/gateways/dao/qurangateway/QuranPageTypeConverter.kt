package com.myapplication.data.gateways.dao.qurangateway

import androidx.room.TypeConverter
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity

class QuranPageTypeConverter {

    @TypeConverter
    fun versesToPage(list:List<QuranVersesEntity>):QuranPage
    {
        return QuranPage(list,list[0].page,list.last().line)
    }
}