package dev.maruffirdaus.geopocket.ui.instructions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.ui.instructions.component.InstructionsAnimationCanvas
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun InstructionsScreen(
    onNavigate: (AppNavKey) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: InstructionsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InstructionsScreenContent(
        uiState = uiState,
        onNavigate = onNavigate,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InstructionsScreenContent(
    uiState: InstructionsUiState,
    onNavigate: (AppNavKey) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(uiState.subtopic.title)
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.ArrowLeft,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            Card(
                modifier = Modifier.padding(16.dp)
            ) {
                InstructionsAnimationCanvas(
                    subtopic = uiState.subtopic,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }
            uiState.subtopic.steps().forEach { step ->
                Text(step)
            }
        }
    }
}

@Composable
@Preview
private fun InstructionsScreenPreview() {
    GeoPocketTheme {
        InstructionsScreenContent(
            uiState = InstructionsUiState(),
            onNavigate = {},
            onNavigateBack = {}
        )
    }
}