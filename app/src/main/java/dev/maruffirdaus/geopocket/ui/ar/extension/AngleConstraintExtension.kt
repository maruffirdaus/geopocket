package dev.maruffirdaus.geopocket.ui.ar.extension

import dev.maruffirdaus.geopocket.domain.topic.constraint.AngleConstraint

fun AngleConstraint.toHint(): String {
    val minDegree = minDegree?.toInt()
    val maxDegree = maxDegree?.toInt()

    return when {
        minDegree != null && maxDegree != null && minDegree == maxDegree -> "Sudut $id = $minDegree°"
        minDegree != null && maxDegree != null -> "Sudut $id: $minDegree° – $maxDegree°"
        minDegree != null -> "Sudut $id ≥ $minDegree°"
        maxDegree != null -> "Sudut $id ≤ $maxDegree°"
        else -> ""
    }
}