package ru.rainbowsmile.test.repository

import retrofit2.Response
import retrofit2.http.GET
import ru.rainbowsmile.test.model.DocumentsResponse

interface DocumentsApi {
    @GET("getdocumentlist")
    suspend fun getDocuments(): Response<DocumentsResponse>
}
