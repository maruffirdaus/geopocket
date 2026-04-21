package dev.maruffirdaus.geopocket.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.maruffirdaus.geopocket.data.local.subtopic.SubtopicProgress
import dev.maruffirdaus.geopocket.data.local.subtopic.SubtopicProgressDao

@Database(entities = [SubtopicProgress::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subtopicProgressDao(): SubtopicProgressDao
}