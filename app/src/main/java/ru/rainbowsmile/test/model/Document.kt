package ru.rainbowsmile.test.model

import com.squareup.moshi.Json

data class Document(
    @Json(name = "id_pos")
    var id: Int,
    @Json(name = "id_record")
    val idRecord: Int,
    @Json(name = "nom_route")
    val numRoute: String,
    @Json(name = "nom_zak")
    val numOrder: String,
    @Json(name = "id_hd_route")
    val hdRoute: Int? = null,
    @Json(name = "nom_nakl")
    val numNakl: String? = null
)
