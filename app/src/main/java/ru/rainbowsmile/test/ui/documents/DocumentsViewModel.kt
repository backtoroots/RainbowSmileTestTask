package ru.rainbowsmile.test.ui.documents

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ru.rainbowsmile.test.model.Document
import ru.rainbowsmile.test.model.ScreenState
import ru.rainbowsmile.test.repository.DocumentsRepository
import javax.inject.Inject

@HiltViewModel
class DocumentsViewModel @Inject constructor(private val repository: DocumentsRepository) :
    ViewModel() {

    private val _documentItems = MutableLiveData<List<Document>>()
    val documentItems: LiveData<List<Document>> = _documentItems
    private var maxId: Int = 0
    var copiesAmount = 0

    private val _screenState = MutableLiveData<ScreenState>()
    val screenState: LiveData<ScreenState> = _screenState

    init {
        viewModelScope.launch {
            _screenState.value = ScreenState.Loading

            repository.getDocuments()
                .catch { error ->
                    _screenState.value = ScreenState.Error(error)
                }
                .collect { documents ->
                    maxId = documents.map { it.id }.max()
                    _screenState.value = ScreenState.Success
                    _documentItems.value = documents
                }
        }
    }

    private fun getNewMaxId(): Int {
        maxId++
        return maxId
    }

    private fun addDocument(duplicatedDocumentPosition: Int, newDocumentPosition: Int = -1) {
        val newDocument = documentItems.value?.get(duplicatedDocumentPosition)?.copy()?.apply {
            id = getNewMaxId()
        }
        if (newDocument != null) {
            val tempDocuments = _documentItems.value?.toMutableList()
            if (newDocumentPosition >= 0) {
                tempDocuments?.add(newDocumentPosition, newDocument)
            } else {
                tempDocuments?.add(newDocument)
            }
            copiesAmount++
            _documentItems.value = tempDocuments!!
        }
    }

    fun duplicatedDocumentAsFirst(duplicatedDocumentPosition: Int) =
        addDocument(duplicatedDocumentPosition, 0)

    fun duplicateDocumentAsLast(duplicatedDocumentPosition: Int) =
        addDocument(duplicatedDocumentPosition)

    fun sortDocuments() {
        _documentItems.value = _documentItems.value?.sortedBy { it.idRecord }
    }
}
