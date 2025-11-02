package dev.maruffirdaus.geopocket.ui.ar.common.util

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import dev.maruffirdaus.geopocket.ui.ar.common.component.DashedLineWithLabel
import dev.maruffirdaus.geopocket.ui.ar.common.component.Marker
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.math.Direction
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.Node
import io.github.sceneview.node.ViewNode2
import kotlin.math.sqrt

object NodeUtil {
    fun createMarkerNode(
        engine: Engine,
        windowManager: ViewNode2.WindowManager,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        label: String
    ): AnchorNode {
        return AnchorNode(engine, anchor).apply {
            isPositionEditable = false

            val viewNode = ViewNode2(
                engine = engine,
                windowManager = windowManager,
                materialLoader = materialLoader,
                unlit = true
            ) {
                GeoPocketTheme {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        Marker(label)
                    }
                }
            }.apply {
                pxPerUnits = 1000f
                rotation = Rotation(x = -90f)
            }

            addChildNode(viewNode)
        }
    }

    fun createLineBetweenNodes(
        engine: Engine,
        materialLoader: MaterialLoader,
        windowManager: ViewNode2.WindowManager,
        startNode: Node,
        endNode: Node
    ): Node {
        val startPos = startNode.worldPosition
        val endPos = endNode.worldPosition

        val distance = calculateDistance(startNode, endNode)

        return ViewNode2(
            engine = engine,
            windowManager = windowManager,
            materialLoader = materialLoader,
            unlit = true
        ) {
            val density = LocalDensity.current

            GeoPocketTheme {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    with(density) {
                        DashedLineWithLabel(
                            label = "$distance m",
                            modifier = Modifier.width((distance * 1000f).toDp())
                        )
                    }
                }
            }
        }.apply {
            pxPerUnits = 1000f
            worldPosition = (startPos + endPos) / 2f
            lookAt(endNode)
            rotation = Rotation(x = -90f, y = -90f)
        }
    }

    fun calculateDistance(node1: Node, node2: Node): Float {
        val pos1 = node1.worldPosition
        val pos2 = node2.worldPosition

        val dx = pos2.x - pos1.x
        val dy = pos2.y - pos1.y
        val dz = pos2.z - pos1.z

        return sqrt(dx * dx + dy * dy + dz * dz)
    }
}