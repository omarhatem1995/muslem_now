package com.myapplication.data.gateways.dao.qurangateway

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myapplication.data.entities.model.QuranVersesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun loadQuran(List:List<QuranVersesEntity>)


    @Query("Select * From QuranTable")
     fun getAllQuran(): Flow<List<QuranVersesEntity>>
}