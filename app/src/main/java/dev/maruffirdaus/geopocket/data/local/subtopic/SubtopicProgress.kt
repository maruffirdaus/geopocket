package dev.maruffirdaus.geopocket.data.local.subtopic

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SubtopicProgress(
    @PrimaryKey val id: String,
    val isCompleted: Boolean = false,
    val highestScore: Int = 0
)
