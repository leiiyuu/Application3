package com.example.application3.di

import android.content.Context
import androidx.room.Room
import com.example.application3.data.local.HistoryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): HistoryDatabase =
        Room.databaseBuilder(
            context,
            HistoryDatabase::class.java,
            "history_db"
        ).build()

    @Provides
    @Singleton
    fun provideHistoryDao(db: HistoryDatabase) = db.historyDao()
}