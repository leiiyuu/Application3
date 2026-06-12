package com.example.application3.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.application3.data.local.HistoryDatabase
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryRepositoryIntegrationTest {

    private lateinit var database: HistoryDatabase
    private lateinit var repository: HistoryRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HistoryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = HistoryRepository(database.historyDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun addToHistoryAndGetAllHistory_returnsAllEntries() = runTest {
        repository.addToHistory(1, "First")
        repository.addToHistory(2, "Second")

        val all = repository.getAllHistory()
        assertEquals(2, all.size)
        assertTrue(all.any { it.postId == 1 })
        assertTrue(all.any { it.postId == 2 })
    }

    @Test
    fun clearHistory_removesAll() = runTest {
        repository.addToHistory(1, "First")
        repository.clearHistory()
        val all = repository.getAllHistory()
        assertEquals(0, all.size)
    }
}