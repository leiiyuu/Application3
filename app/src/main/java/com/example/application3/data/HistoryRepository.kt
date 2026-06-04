package com.example.application3.data

import com.example.application3.data.local.HistoryDao
import com.example.application3.data.local.HistoryEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val dao: HistoryDao
) {
    suspend fun getAllHistory(): List<HistoryEntity> = withContext(Dispatchers.IO) {
        dao.getAll()
    }

    suspend fun addToHistory(postId: Int, title: String) {
        dao.insert(HistoryEntity(postId = postId, title = title))
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}