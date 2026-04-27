package dev.maruffirdaus.geopocket.data.repository

import dev.maruffirdaus.geopocket.data.local.subtopic.SubtopicProgress
import dev.maruffirdaus.geopocket.data.local.subtopic.SubtopicProgressDao
import org.koin.core.annotation.Singleton

@Singleton
class TopicRepository(
    private val subtopicProgressDao: SubtopicProgressDao
) {
    suspend fun getSubtopicProgresses(): List<SubtopicProgress> = subtopicProgressDao.getAll()
    suspend fun deleteSubtopicProgresses() = subtopicProgressDao.deleteAll()
}