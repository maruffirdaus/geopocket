package dev.maruffirdaus.geopocket.di

import android.content.Context
import androidx.room.Room
import dev.maruffirdaus.geopocket.data.local.AppDatabase
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
class DatabaseModule {
    @Singleton
    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "geopocket-db").build()

    @Singleton
    fun provideSubtopicProgressDao(database: AppDatabase) = database.subtopicProgressDao()
}