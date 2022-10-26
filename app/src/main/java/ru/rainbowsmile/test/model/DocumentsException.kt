package ru.rainbowsmile.test.model

import com.squareup.moshi.Json

data class DocumentsException(
    @Json(name = "error")
    val error: Int,
    @Json(name = "error_msg")
    val errorMsg: Any? = null
)
