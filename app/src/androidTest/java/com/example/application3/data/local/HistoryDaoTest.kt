package com.example.application3.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryDaoTest {

    private lateinit var database: HistoryDatabase
    private lateinit var dao: HistoryDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, HistoryDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.historyDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndGetAllHistory_insertsAndReturnsSortedByTimestampDesc() = runTest {
        val entry1 = HistoryEntity(postId = 1, title = "Post 1", timestamp = 1000)
        val entry2 = HistoryEntity(postId = 2, title = "Post 2", timestamp = 2000)
        dao.insert(entry1)
        dao.insert(entry2)

        val all = dao.getAll()
        assertEquals(2, all.size)
        // порядок: сначала более новый (timestamp больше)
        assertEquals(2000, all[0].timestamp)
        assertEquals(1000, all[1].timestamp)
    }

    @Test
    fun clearAll_deletesAllEntries() = runTest {
        dao.insert(HistoryEntity(postId = 1, title = "Test", timestamp = 1000))
        dao.clearAll()
        val all = dao.getAll()
        assertTrue(all.isEmpty())
    }
}