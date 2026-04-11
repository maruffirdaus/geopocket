package dev.maruffirdaus.geopocket.ui.ar.model

import com.google.ar.core.Pose
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.math.Position

data class PlacementIndicatorNode(
    val worldPosition: Position = Position(),
    val quaternion: Quaternion = Quaternion()
) {
    fun calculateCorrectedPose(): Pose {
        val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), 90f)
        val correctedQuaternion = quaternion * correction

        val correctedPose = Pose(
            floatArrayOf(worldPosition.x, worldPosition.y, worldPosition.z),
            floatArrayOf(
                correctedQuaternion.x,
                correctedQuaternion.y,
                correctedQuaternion.z,
                correctedQuaternion.w
            )
        )

        return correctedPose
    }
}
