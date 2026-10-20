package com.example.composeapp.repository.local

import androidx.room.Database
import androidx.room.RoomDatabase

/** The on-device SQLite database ("loop.db"). Changing a table needs a version bump and a Migration. */
@Database(entities = [SessionEntity::class, SentMessageEntity::class], version = 1, exportSchema = false)
abstract class FreelyDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao

    abstract fun sentMessageDao(): SentMessageDao
}
