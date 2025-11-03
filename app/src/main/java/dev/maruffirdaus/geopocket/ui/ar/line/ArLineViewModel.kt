package dev.maruffirdaus.geopocket.ui.ar.line

import androidx.lifecycle.ViewModel
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import dev.maruffirdaus.geopocket.ui.ar.common.util.NodeUtil
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.node.ViewNode2
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ArLineViewModel(
    private val nodeUtil: NodeUtil
) : ViewModel() {
    private val _uiState = MutableStateFlow(ArLineUiState())
    val uiState = _uiState.asStateFlow()

    fun createMarkerNode(
        engine: Engine,
        materialLoader: MaterialLoader,
        windowManager: ViewNode2.WindowManager,
        anchor: Anchor
    ) {
        if (uiState.value.markerNodes.size < 2) {
            _uiState.update {
                val markerNode = nodeUtil.createMarkerNode(
                    engine = engine,
                    windowManager = windowManager,
                    materialLoader = materialLoader,
                    anchor = anchor,
                    label = ('A' + it.markerNodes.size).toString()
                )
                it.copy(
                    markerNodes = it.markerNodes + markerNode,
                    lineNodes = if (it.markerNodes.isNotEmpty()) {
                        it.lineNodes + nodeUtil.createLineBetweenNodes(
                            engine = engine,
                            materialLoader = materialLoader,
                            windowManager = windowManager,
                            startNode = it.markerNodes.last(),
                            endNode = markerNode
                        )
                    } else {
                        it.lineNodes
                    }
                )
            }
        } else {
            changeErrorMessage("You can only add 2 markers")
        }
    }

    fun undoMarkerNode() {
        _uiState.update {
            it.copy(
                markerNodes = it.markerNodes.dropLast(1),
                lineNodes = it.lineNodes.dropLast(1)
            )
        }
    }

    fun clearMarkerNodes() {
        _uiState.update {
            it.copy(
                markerNodes = emptyList(),
                lineNodes = emptyList()
            )
        }
    }

    fun changeErrorMessage(errorMessage: String?) {
        _uiState.update {
            it.copy(errorMessage = errorMessage)
        }
    }
}