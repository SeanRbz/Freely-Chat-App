package com.example.composeapp.di.module

import android.content.Context
import androidx.room.Room
import com.example.composeapp.repository.local.FreelyDatabase
import com.example.composeapp.repository.local.SentMessageDao
import com.example.composeapp.repository.local.SessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideFreelyDatabase(@ApplicationContext context: Context): FreelyDatabase =
        Room.databaseBuilder(context, FreelyDatabase::class.java, "loop.db").build()

    @Provides
    fun provideSessionDao(database: FreelyDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideSentMessageDao(database: FreelyDatabase): SentMessageDao = database.sentMessageDao()
}
