package dev.maruffirdaus.geopocket.ui.ar.model

data class PreviewState(
    val segment: SegmentNodeState? = null,
    val closingSegment: SegmentNodeState? = null,
    val angle: AngleNodeState? = null,
    val closingAngle: AngleNodeState? = null,
    val closingAngle2: AngleNodeState? = null
)
