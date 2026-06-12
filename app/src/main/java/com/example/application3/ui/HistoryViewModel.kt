package com.example.application3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application3.data.HistoryRepository
import com.example.application3.data.local.HistoryEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryUiState {
    object Loading : HistoryUiState()
    data class Success(val entries: List<HistoryEntity>) : HistoryUiState()
    data class Error(val message: String) : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repository: HistoryRepository
) : ViewModel() {

    var uiState by mutableStateOf<HistoryUiState>(HistoryUiState.Loading)
        private set

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            uiState = HistoryUiState.Loading
            try {
                val entries = repository.getAllHistory()
                uiState = HistoryUiState.Success(entries)
            } catch (e: Exception) {
                uiState = HistoryUiState.Error("Не удалось загрузить историю")
            }
        }
    }

    fun addToHistory(postId: Int, title: String) {
        viewModelScope.launch {
            repository.addToHistory(postId, title)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            loadHistory()
        }
    }
}