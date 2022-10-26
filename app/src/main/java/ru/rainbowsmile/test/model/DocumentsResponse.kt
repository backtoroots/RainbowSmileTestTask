package ru.rainbowsmile.test.model

import com.squareup.moshi.Json

data class DocumentsResponse(
    @Json(name = "exception")
    val exception: DocumentsException,
    @Json(name = "data")
    val data: List<Document>
)
