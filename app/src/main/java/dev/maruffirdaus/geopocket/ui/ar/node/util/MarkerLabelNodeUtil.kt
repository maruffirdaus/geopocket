package dev.maruffirdaus.geopocket.ui.ar.node.util

import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.ModelNode

object MarkerLabelNodeUtil {
    fun create(
        modelInstance: ModelInstance
    ): ModelNode = ModelNode(
        modelInstance = modelInstance,
        scaleToUnits = 0.05f
    )
}