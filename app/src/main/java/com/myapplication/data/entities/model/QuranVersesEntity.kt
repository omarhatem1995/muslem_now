package com.myapplication.data.entities.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "QuranTable")
data class QuranVersesEntity(

                            @PrimaryKey
                            val id: Int? = null,

                             val quranAya: Int? = null,


                             val mobileCode: String? = null,


                             val line: Int? = null,


                             val sura: Int? = null,


                             val aya: Int? = null,


                             val simple: String? = null,


                             val webCodeV3: String? = null,


                             val type: Int? = null,


                             val juz: Int? = null,


                             val hizb: Int? = null,


                             val unicode: String? = null,


                             val text: String? = null,


                             val page: Int? = null,


                             val position: Int? = null,


                             val webCode: String? = null,


                             val rub: Int? = null
                                          )
