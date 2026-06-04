package com.example.application3.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.application3.ui.screens.PostDetailScreen
import com.example.application3.ui.screens.PostListScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.application3.ui.screens.HistoryScreen

object Routes {
    const val POST_LIST = "post_list"
    const val POST_DETAIL = "post_detail/{postId}"

    const val HISTORY = "history"

    fun postDetail(id: Int) = "post_detail/$id"
}

@Composable
fun PostNavGraph() {
    val navController = rememberNavController()
    val viewModel: PostViewModel = hiltViewModel()
    val historyViewModel: HistoryViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = Routes.POST_LIST) {
        composable(Routes.POST_LIST) {
            PostListScreen(
                uiState = viewModel.listState,
                searchQuery = viewModel.searchQuery,
                onSearchChange = viewModel::onSearchQueryChange,
                onPostClick = { postId ->
                    navController.navigate(Routes.postDetail(postId))
                },
                onHistoryClick = {
                    navController.navigate(Routes.HISTORY)
                },
                onRetry = { viewModel.onSearchQueryChange(viewModel.searchQuery) }
            )
        }
        composable(
            route = Routes.POST_DETAIL,
            arguments = listOf(navArgument("postId") { type = NavType.IntType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getInt("postId") ?: return@composable
            val viewModel: PostViewModel = hiltViewModel()

            LaunchedEffect(postId) {
                viewModel.loadPostDetails(postId)
            }

            PostDetailScreen(
                state = viewModel.detailState,
                onRetry = { viewModel.loadPostDetails(postId) },
//                onPostLoaded = { post ->
//                    historyViewModel.addToHistory(post.id, post.title)
//                }
            )
        }
        composable(Routes.HISTORY) {
            HistoryScreen(
                uiState = historyViewModel.uiState,
                onClearHistory = { historyViewModel.clearHistory() },
                onPostClick = { postId ->
                    viewModel.loadPostDetails(postId)
                    navController.navigate(Routes.postDetail(postId))
                },
                onRetry = { historyViewModel.loadHistory() },
                onRefresh = { historyViewModel.loadHistory() }
            )
        }
    }
}