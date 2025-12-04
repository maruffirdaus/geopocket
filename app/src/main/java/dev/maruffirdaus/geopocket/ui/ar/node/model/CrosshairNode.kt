package dev.maruffirdaus.geopocket.ui.ar.node.model

import androidx.compose.runtime.Composable
import com.google.android.filament.Engine
import com.google.ar.core.Pose
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.quaternion
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.ViewNode2

class CrosshairNode(
    engine: Engine,
    windowManager: ViewNode2.WindowManager,
    materialLoader: MaterialLoader,
    content: @Composable () -> Unit
) {
    val node = ViewNode2(
        engine = engine,
        windowManager = windowManager,
        materialLoader = materialLoader,
        unlit = true,
        content = content
    ).apply {
        pxPerUnits = 2000f
        collisionShape = null
        isPositionEditable = false
    }

    fun update(pose: Pose) {
        node.apply {
            val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)

            worldPosition = pose.position
            quaternion = pose.quaternion * correction
        }
    }

    fun calculateCorrectedPose(): Pose {
        val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), 90f)
        val correctedQuaternion = node.quaternion * correction

        val correctedPose = Pose(
            floatArrayOf(node.worldPosition.x, node.worldPosition.y, node.worldPosition.z),
            floatArrayOf(
                correctedQuaternion.x,
                correctedQuaternion.y,
                correctedQuaternion.z,
                correctedQuaternion.w
            )
        )

        return correctedPose
    }

    fun destroy() {
        node.destroy()
    }
}