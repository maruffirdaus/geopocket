package dev.maruffirdaus.geopocket.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import dev.maruffirdaus.geopocket.data.local.AppDatabase
import dev.maruffirdaus.geopocket.data.local.dataStore
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

@Module
@Suppress("UNUSED")
class DataModule {
    @Singleton
    fun provideDataStore(context: Context): DataStore<Preferences> = context.dataStore

    @Singleton
    fun provideDatabase(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "geopocket-db").build()

    @Singleton
    fun provideSubtopicProgressDao(database: AppDatabase) = database.subtopicProgressDao()
}