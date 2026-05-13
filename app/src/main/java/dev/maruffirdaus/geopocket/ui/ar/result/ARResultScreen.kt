package dev.maruffirdaus.geopocket.ui.ar.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.ui.common.extension.alignHorizontalSpace
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import me.saket.telephoto.zoomable.coil3.ZoomableAsyncImage
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ARResultScreen(
    viewModel: ARResultViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ARResultScreenContent(
        uiState = uiState,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ARResultScreenContent(
    uiState: ARResultUiState,
    navHandler: NavHandler
) {
    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text("Berhasil")
                },
                subtitle = {
                    Text("Kamu telah menyelesaikan aktivitas ini dengan baik")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navHandler.pop<AppNavKey>()
                        }
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.ArrowLeft,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 16.dp)
                .alignHorizontalSpace(16.dp)
        ) {
            ZoomableAsyncImage(
                model = uiState.completionImage,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    navHandler.replace(
                        AppNavKey.Quiz(
                            subtopic = uiState.subtopic,
                            segments = uiState.segments,
                            angles = uiState.angles
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Mulai kuis")
            }
        }
    }
}

@Composable
@Preview
private fun ARResultScreenPreview() {
    GeoPocketTheme {
        ARResultScreenContent(
            uiState = ARResultUiState(),
            navHandler = NavHandler()
        )
    }
}