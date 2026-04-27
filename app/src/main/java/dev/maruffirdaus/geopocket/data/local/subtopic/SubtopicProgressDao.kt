package dev.maruffirdaus.geopocket.data.local.subtopic

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface SubtopicProgressDao {
    @Upsert
    suspend fun upsert(subtopicProgress: SubtopicProgress)

    @Query("SELECT * FROM subtopicprogress")
    suspend fun getAll(): List<SubtopicProgress>

    @Query("DELETE FROM subtopicprogress")
    suspend fun deleteAll()
}