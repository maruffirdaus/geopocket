package dev.maruffirdaus.geopocket.ui.ar.activity.extension

import dev.maruffirdaus.geopocket.domain.topic.constraint.SegmentConstraint

fun SegmentConstraint.toHint(): String {
    val minLength = minLength?.times(100)?.toInt()
    val maxLength = maxLength?.times(100)?.toInt()

    return when {
        minLength != null && maxLength != null && minLength == maxLength -> "Garis $id = $minLength cm"
        minLength != null && maxLength != null -> "Garis $id: $minLength – $maxLength cm"
        minLength != null -> "Garis $id ≥ $minLength cm"
        maxLength != null -> "Garis $id ≤ $maxLength cm"
        else -> ""
    }
}