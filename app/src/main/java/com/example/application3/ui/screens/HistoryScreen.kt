package com.example.application3.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.application3.data.local.HistoryEntity
import com.example.application3.ui.HistoryUiState
import com.example.application3.utils.DateFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onClearHistory: () -> Unit,
    onPostClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit
) {

    LaunchedEffect(Unit) {
        onRefresh()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "История просмотров",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = onClearHistory,
                modifier = Modifier.padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Очистить")
            }
        }

        when (uiState) {
            is HistoryUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is HistoryUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry) { Text("Повторить") }
                }
            }
            is HistoryUiState.Success -> {
                if (uiState.entries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("История пуста")
                    }
                } else {
                    LazyColumn {
                        items(uiState.entries, key = { it.id }) { entry ->
                            HistoryItem(entry, onPostClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryItem(entry: HistoryEntity, onClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick(entry.postId) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = entry.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Post ID: ${entry.postId} • ${DateFormatter.format(entry.timestamp)}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
