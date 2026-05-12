package dev.maruffirdaus.geopocket.ui.instructions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.common.component.PageIndicator
import dev.maruffirdaus.geopocket.ui.common.extensions.alignHorizontalSpace
import dev.maruffirdaus.geopocket.ui.instructions.component.InstructionsAnimationCanvas
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun InstructionsScreen(
    viewModel: InstructionsViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    InstructionsScreenContent(
        uiState = uiState,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun InstructionsScreenContent(
    uiState: InstructionsUiState,
    navHandler: NavHandler
) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass
    val isExpanded =
        windowSize.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(uiState.subtopic.title)
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
        }
    ) { innerPadding ->
        if (isExpanded) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(vertical = 16.dp)
            ) {
                InstructionsVisual(
                    subtopic = uiState.subtopic,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .padding(start = 16.dp)
                )
                InstructionsControls(
                    subtopic = uiState.subtopic,
                    navHandler = navHandler,
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                )
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            InstructionsVisual(
                subtopic = uiState.subtopic,
                modifier = Modifier
                    .alignHorizontalSpace(16.dp)
                    .aspectRatio(1f)
            )
            InstructionsControls(
                subtopic = uiState.subtopic,
                navHandler = navHandler,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun InstructionsVisual(
    subtopic: Subtopic,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        InstructionsAnimationCanvas(
            subtopic = subtopic,
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        )
    }
}

@Composable
private fun InstructionsControls(
    subtopic: Subtopic,
    navHandler: NavHandler,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val steps = subtopic.steps()
        val pagerState = rememberPagerState { steps.size }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .alignHorizontalSpace(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = steps[pagerState.currentPage],
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        PageIndicator(
            size = steps.size,
            currentIndex = pagerState.currentPage,
            onClick = { index ->
                scope.launch {
                    pagerState.animateScrollToPage(index)
                }
            },
            modifier = Modifier.alignHorizontalSpace(16.dp)
        )
        Button(
            onClick = {
                navHandler.replace(AppNavKey.AR(subtopic))
            },
            modifier = Modifier.alignHorizontalSpace(16.dp)
        ) {
            Text("Mulai")
        }
    }
}

@Composable
@Preview
private fun InstructionsScreenPreview() {
    GeoPocketTheme {
        InstructionsScreenContent(
            uiState = InstructionsUiState(),
            navHandler = NavHandler()
        )
    }
}