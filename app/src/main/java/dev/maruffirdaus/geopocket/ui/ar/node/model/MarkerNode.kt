package dev.maruffirdaus.geopocket.ui.ar.node.model

import com.google.android.filament.Engine
import com.google.android.filament.MaterialInstance
import com.google.ar.core.Anchor
import com.google.ar.core.Pose
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.collision.Box
import io.github.sceneview.collision.Vector3
import io.github.sceneview.node.CylinderNode
import java.util.UUID

class MarkerNode(
    engine: Engine,
    materialInstance: MaterialInstance,
    anchor: Anchor,
    onPoseChange: MarkerNode.(Pose) -> Unit,
    val id: String = UUID.randomUUID().toString()
) {
    val node = AnchorNode(
        engine = engine,
        anchor = anchor,
        onPoseChanged = { pose ->
            onPoseChange(pose)
        }
    ).apply {
        val cylinderNode = CylinderNode(
            engine = engine,
            radius = 0.005f,
            height = 0.0001f,
            materialInstance = materialInstance
        )
        collisionShape = Box(Vector3(0.1f, 0.1f, 0.1f))
        addChildNode(cylinderNode)
    }
    val connectedNodeIds = mutableListOf<String>()

    fun destroy() {
        node.destroy()
    }
}