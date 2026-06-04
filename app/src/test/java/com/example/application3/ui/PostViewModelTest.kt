package com.example.application3.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.application3.CoroutineTestRule
import com.example.application3.data.HistoryRepository
import com.example.application3.data.PostRepository
import com.example.application3.model.Post
import io.mockk.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PostViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var repository: PostRepository
    private lateinit var historyRepository: HistoryRepository
    private lateinit var viewModel: PostViewModel

    private val samplePosts = listOf(
        Post(1, "Post 1", "Body 1", 1),
        Post(2, "Post 2", "Body 2", 1)
    )

    @Before
    fun setUp() {
        repository = mockk()
        historyRepository = mockk(relaxed = true)
        viewModel = PostViewModel(repository, historyRepository)
    }

    @Test
    fun initialListState_isLoading() {
        assertTrue(viewModel.listState is PostListUiState.Loading)
    }

    @Test
    fun loadAllPosts_success_updatesListState() = runTest {
        coEvery { repository.getPosts() } returns samplePosts

        viewModel.loadAllPosts()
        advanceUntilIdle()

        val state = viewModel.listState
        assertTrue(state is PostListUiState.Success)
        assertEquals(samplePosts, (state as PostListUiState.Success).posts)
    }

    @Test
    fun loadAllPosts_error_updatesErrorState() = runTest {
        coEvery { repository.getPosts() } throws RuntimeException("Network error")

        viewModel.loadAllPosts()
        advanceUntilIdle()

        val state = viewModel.listState
        assertTrue(state is PostListUiState.Error)
        assertEquals("Ошибка загрузки", (state as PostListUiState.Error).message)
    }

    @Test
    fun loadAllPosts_retryAfterError_success() = runTest {
        coEvery { repository.getPosts() } throws RuntimeException("Network error")
        viewModel.loadAllPosts()
        advanceUntilIdle()
        assertTrue(viewModel.listState is PostListUiState.Error)

        coEvery { repository.getPosts() } returns samplePosts
        viewModel.loadAllPosts()
        advanceUntilIdle()

        val state = viewModel.listState
        assertTrue(state is PostListUiState.Success)
        assertEquals(samplePosts, (state as PostListUiState.Success).posts)
    }

    @Test
    fun searchInvalidInput_returnsValidationError() = runTest {
        viewModel.onSearchQueryChange("abc")
        val state = viewModel.listState
        assertTrue(state is PostListUiState.ValidationError)
        assertEquals("Введите ID пользователя (число)", (state as PostListUiState.ValidationError).message)
    }

    @Test
    fun searchUserWithEmptyResult_returnsEmpty() = runTest {
        val userId = 999
        coEvery { repository.searchPostsByUser(userId) } returns emptyList()

        viewModel.onSearchQueryChange(userId.toString())
        coroutineTestRule.testDispatcher.scheduler.advanceTimeBy(500)
        advanceUntilIdle()

        val state = viewModel.listState
        assertTrue(state is PostListUiState.Empty)
        assertEquals(userId.toString(), (state as PostListUiState.Empty).query)
    }

    @Test
    fun loadPostDetails_cancelsPreviousJob() = runTest {
        val post1 = Post(1, "Post 1", "Body 1", 1)
        val post2 = Post(2, "Post 2", "Body 2", 2)

        val deferred1 = CompletableDeferred<Post>()
        val deferred2 = CompletableDeferred<Post>()

        coEvery { repository.getPostById(1) } coAnswers { deferred1.await() }
        coEvery { repository.getPostById(2) } coAnswers { deferred2.await() }

        viewModel.loadPostDetails(1)
        viewModel.loadPostDetails(2)

        deferred2.complete(post2)
        advanceUntilIdle()

        assertTrue(viewModel.detailState is PostDetailUiState.Success)
        assertEquals(post2, (viewModel.detailState as PostDetailUiState.Success).post)

        deferred1.complete(post1)
        advanceUntilIdle()

        assertTrue(viewModel.detailState is PostDetailUiState.Success)
        assertEquals(post2, (viewModel.detailState as PostDetailUiState.Success).post)

        coVerify(exactly = 1) { historyRepository.addToHistory(2, "Post 2") }
    }
}