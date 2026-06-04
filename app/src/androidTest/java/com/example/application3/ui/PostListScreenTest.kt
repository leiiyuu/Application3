package com.example.application3.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import com.example.application3.model.Post
import com.example.application3.ui.screens.PostListScreen
import org.junit.Rule
import org.junit.Test

class PostListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val samplePosts = listOf(
        Post(1, "First Post", "Body 1", 1),
        Post(2, "Second Post", "Body 2", 1)
    )

    @Test
    fun successState_showsPosts() {
        composeTestRule.setContent {
            PostListScreen(
                uiState = PostListUiState.Success(samplePosts),
                searchQuery = "",
                onSearchChange = {},
                onPostClick = {},
                onHistoryClick = {},
                onRetry = {}
            )
        }
        composeTestRule.onNodeWithText("First Post").assertIsDisplayed()
        composeTestRule.onNodeWithText("Second Post").assertIsDisplayed()
    }

    @Test
    fun errorState_showsRetryButton() {
        composeTestRule.setContent {
            PostListScreen(
                uiState = PostListUiState.Error("Ошибка загрузки"),
                searchQuery = "",
                onSearchChange = {},
                onPostClick = {},
                onHistoryClick = {},
                onRetry = {}
            )
        }
        composeTestRule.onNodeWithText("Ошибка загрузки").assertIsDisplayed()
        composeTestRule.onNodeWithText("Повторить").assertIsDisplayed()
    }

    @Test
    fun emptyState_showsNoPostsMessage() {
        composeTestRule.setContent {
            PostListScreen(
                uiState = PostListUiState.Empty(),
                searchQuery = "",
                onSearchChange = {},
                onPostClick = {},
                onHistoryClick = {},
                onRetry = {}
            )
        }
        composeTestRule.onNodeWithText("Посты не найдены").assertIsDisplayed()
    }

    @Test
    fun validationErrorState_showsValidationMessage() {
        composeTestRule.setContent {
            PostListScreen(
                uiState = PostListUiState.ValidationError("Введите ID пользователя (число)"),
                searchQuery = "abc",
                onSearchChange = {},
                onPostClick = {},
                onHistoryClick = {},
                onRetry = {}
            )
        }
        composeTestRule.onNodeWithText("Введите ID пользователя (число)").assertIsDisplayed()
    }

    @Test
    fun clickOnPost_callsOnPostClick() {
        var clickedPostId = -1
        composeTestRule.setContent {
            PostListScreen(
                uiState = PostListUiState.Success(samplePosts),
                searchQuery = "",
                onSearchChange = {},
                onPostClick = { clickedPostId = it },
                onHistoryClick = {},
                onRetry = {}
            )
        }
        composeTestRule.onNodeWithText("First Post").performClick()
        assert(clickedPostId == 1)
    }
}