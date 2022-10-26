package ru.rainbowsmile.test.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import ru.rainbowsmile.test.model.Document
import javax.inject.Inject

class DocumentsRepository @Inject constructor(
    private val documentsApi: DocumentsApi
) {
    suspend fun getDocuments(): Flow<List<Document>> = flow {
        val response = documentsApi.getDocuments()
        if (response.body() != null) {
            val documentsBody = documentsApi.getDocuments().body()
            val documents = documentsBody?.data as List<Document>
            emit(documents)
        } else {
            throw RequestFailureException()
        }
    }.flowOn(Dispatchers.IO)
}
