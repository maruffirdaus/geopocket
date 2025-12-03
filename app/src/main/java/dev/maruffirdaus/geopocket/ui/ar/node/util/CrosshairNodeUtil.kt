package dev.maruffirdaus.geopocket.ui.ar.node.util

import androidx.compose.runtime.Composable
import com.google.android.filament.Engine
import com.google.ar.core.Pose
import dev.romainguy.kotlin.math.Float3
import dev.romainguy.kotlin.math.Quaternion
import io.github.sceneview.ar.arcore.position
import io.github.sceneview.ar.arcore.quaternion
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.Node
import io.github.sceneview.node.ViewNode2

object CrosshairNodeUtil {
    fun create(
        engine: Engine,
        windowManager: ViewNode2.WindowManager,
        materialLoader: MaterialLoader,
        content: @Composable () -> Unit
    ): ViewNode2 = ViewNode2(
        engine = engine,
        windowManager = windowManager,
        materialLoader = materialLoader,
        unlit = true,
        content = content
    ).apply {
        pxPerUnits = 2000f
        collisionShape = null
        isPositionEditable = false
        updateGeometrySize()
    }

    fun update(node: Node, pose: Pose) {
        node.apply {
            val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), -90f)

            worldPosition = pose.position
            quaternion = pose.quaternion * correction
        }
    }

    fun calculateCorrectedPose(pose: Pose): Pose {
        val correction = Quaternion.fromAxisAngle(Float3(1f, 0f, 0f), 0f)
        val correctedQuaternion = pose.quaternion * correction

        val correctedPose = Pose(
            floatArrayOf(pose.tx(), pose.ty(), pose.tz()),
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