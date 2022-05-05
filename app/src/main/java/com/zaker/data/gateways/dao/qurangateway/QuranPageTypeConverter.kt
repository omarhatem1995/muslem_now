package com.zaker.data.gateways.dao.qurangateway

import androidx.room.TypeConverter
import com.zaker.data.entities.model.QuranPage
import com.zaker.data.entities.model.QuranVersesEntity

class QuranPageTypeConverter {

    @TypeConverter
    fun versesToPage(list:List<QuranVersesEntity>):QuranPage
    {
        return QuranPage(list,list[0].page,list.last().line)
    }
}