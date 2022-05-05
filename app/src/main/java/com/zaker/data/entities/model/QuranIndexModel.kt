package com.zaker.data.entities.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class QuranIndexModel (
    @SerializedName("place")
    @Expose
    var place: String? = null,

    @SerializedName("type")
    @Expose
    var type: String? = null,

    @SerializedName("count")
    @Expose
    var count: Int? = null,

    @SerializedName("title")
    @Expose
    var title: String? = null,

    @SerializedName("titleAr")
    @Expose
    var titleAr: String? = null,

    @SerializedName("index")
    @Expose
    var index: String? = null,

    @SerializedName("pages")
    @Expose
    var pages: String? = null,

    @SerializedName("page")
    @Expose
    var page: String? = null,

    @SerializedName("indexOut")
    @Expose
    var indexOut: String? = null,

    @SerializedName("juz")
    @Expose
    var juz: List<Juz>? = null,
)

class Juz (
    @SerializedName("index")
    @Expose
    var index: String? = null,

    @SerializedName("verse")
    @Expose
    var verse: Verse? = null
)

class Verse (
    @SerializedName("start")
    @Expose
    var start: String? = null,

    @SerializedName("end")
    @Expose
    var end: String? = null
)