package com.example.application3.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.application3.data.HistoryRepository
import com.example.application3.data.IPostRepository
import com.example.application3.data.PostRepository
import com.example.application3.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


sealed class PostListUiState {
    object Loading : PostListUiState()
    data class Success(val posts: List<Post>) : PostListUiState()
    data class Error(val message: String) : PostListUiState()
    data class Empty(val query: String = "") : PostListUiState()
    data class ValidationError(val message: String) : PostListUiState()
}

sealed class PostDetailUiState {
    object Loading : PostDetailUiState()
    data class Success(val post: Post) : PostDetailUiState()
    data class Error(val message: String) : PostDetailUiState()
}

@HiltViewModel
class PostViewModel@Inject constructor(
    private val repository: IPostRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    var listState by mutableStateOf<PostListUiState>(PostListUiState.Loading)
        private set

    var detailState by mutableStateOf<PostDetailUiState>(PostDetailUiState.Loading)
        private set

    private var searchJob: Job? = null
    private var loadAllJob: Job? = null

    private var detailJob: Job? = null

    var searchQuery by mutableStateOf("")
        private set

    init {
        loadAllPosts()
    }

    fun loadAllPosts() {
        loadAllJob?.cancel()
        loadAllJob = viewModelScope.launch {
            listState = PostListUiState.Loading
            try {
                val posts = repository.getPosts()
                listState = if (posts.isEmpty()) {
                    PostListUiState.Empty()
                } else {
                    PostListUiState.Success(posts)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                listState = PostListUiState.Error("Ошибка загрузки")
            }
        }
    }

    fun onSearchQueryChange(newValue: String) {
        searchQuery = newValue
        searchJob?.cancel()
        loadAllJob?.cancel()

        val query = newValue.trim()

        if (query.isBlank()) {
            loadAllPosts()
            return
        }

        val userId = query.toIntOrNull()

        if (userId == null) {
            listState = PostListUiState.ValidationError(
                "Введите ID пользователя (число)"
            )
            return
        }

        searchJob = viewModelScope.launch {
            delay(500)
            listState = PostListUiState.Loading
            try {
                val result = repository.searchPostsByUser(userId)
                if (query != searchQuery.trim()) return@launch

                listState = if (result.isEmpty()) {
                    PostListUiState.Empty(query = userId.toString())
                } else {
                    PostListUiState.Success(result)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                listState = PostListUiState.Error("Ошибка загрузки")
            }
        }
    }


    private fun addToHistory(post: Post) {
        viewModelScope.launch {
            historyRepository.addToHistory(post.id, post.title)
        }
    }

    fun loadPostDetails(id: Int) {
        detailJob?.cancel()
        detailJob = viewModelScope.launch {
            detailState = PostDetailUiState.Loading
            try {
                val post = repository.getPostById(id)
                detailState = PostDetailUiState.Success(post)
                addToHistory(post)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                detailState = PostDetailUiState.Error("Ошибка загрузки поста")
            }
        }
    }
}