package com.myapplication.data.gateways.dao.qurangateway

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.myapplication.data.entities.model.QuranPage
import com.myapplication.data.entities.model.QuranVersesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuranDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun loadQuran(List:List<QuranVersesEntity>)


    @Query("Select * From QuranTable")
     fun getAllQuran(): Flow<List<QuranVersesEntity>>

    @Query("Select * From QuranTable Where page >= :initial <= :page And page < 605")
       suspend fun getQuranPaged(page:Int,initial: Int):List<QuranVersesEntity>

     @Query("Select MAX(page) From QuranTable ")
     suspend fun getLastPage():Int?
}