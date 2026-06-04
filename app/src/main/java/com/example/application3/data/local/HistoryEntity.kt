package com.example.application3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val postId: Int,
    val title: String,
    val timestamp: Long = System.currentTimeMillis()
)
