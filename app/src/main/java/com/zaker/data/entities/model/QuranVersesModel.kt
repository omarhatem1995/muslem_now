package com.zaker.data.entities.model

import android.app.Application
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName

data class QuranVersesModel(

    @field:SerializedName("quran_aya")
    val quranAya: Int? = null,

    @field:SerializedName("mobile_code")
    val mobileCode: String? = null,

    @field:SerializedName("line")
    val line: Int? = null,

    @field:SerializedName("sura")
    val sura: Int? = null,

    @field:SerializedName("aya")
    val aya: Int? = null,

    @field:SerializedName("simple")
    val simple: String? = null,

    @field:SerializedName("web_code_v3")
    val webCodeV3: String? = null,

    @field:SerializedName("type")
    val type: Int? = null,

    @field:SerializedName("juz")
    val juz: Int? = null,

    @field:SerializedName("hizb")
    val hizb: Int? = null,

    @field:SerializedName("unicode")
    val unicode: String? = null,

    @field:SerializedName("id")
    val id: Int? = null,

    @field:SerializedName("text")
    val text: String? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("position")
    val position: Int? = null,

    @field:SerializedName("web_code")
    val webCode: String? = null,

    @field:SerializedName("rub")
    val rub: Int? = null
)


private fun List<QuranVersesModel>.toEntities(): List<QuranVersesEntity> {
    return this.map {
        QuranVersesEntity(
            id = it.id, quranAya = it.quranAya,


            mobileCode = it.mobileCode,


            line = it.line,


            sura = it.sura,


            aya = it.aya,


            simple = it.simple,


            webCodeV3 = it.webCodeV3,


            type = it.type,


            juz = it.juz,


            hizb = it.hizb,


            unicode = it.unicode,


            text = it.text,


            page = it.page,


            position = it.position,


            webCode = it.webCode,


            rub = it.rub
        )
    }
}

fun getLocalQuranResponse(application: Application): List<QuranVersesEntity> {


	val json = application.assets.open("words.json").bufferedReader().use { it.readText() }

	val quranList = GsonBuilder().create().fromJson(json,Array<QuranVersesModel>::class.java).asList()

	return quranList.toEntities()

}