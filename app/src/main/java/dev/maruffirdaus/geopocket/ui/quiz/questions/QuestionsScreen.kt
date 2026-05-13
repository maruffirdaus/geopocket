package dev.maruffirdaus.geopocket.ui.quiz.questions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import com.adamglin.phosphoricons.regular.PencilLine
import dev.maruffirdaus.geopocket.ui.common.component.PageIndicator
import dev.maruffirdaus.geopocket.ui.common.extension.alignHorizontalSpace
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.quiz.navigation.QuizNavKey
import dev.maruffirdaus.geopocket.ui.quiz.questions.component.OptionCard
import dev.maruffirdaus.geopocket.ui.quiz.questions.component.ShapeCanvas
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun QuestionsScreen(
    viewModel: QuestionsViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QuestionsScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuestionsScreenContent(
    uiState: QuestionsUiState,
    onEvent: (QuestionsEvent) -> Unit,
    navHandler: NavHandler
) {
    val layoutDirection = LocalLayoutDirection.current
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kuis")
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
                },
                actions = {
                    FilledTonalButton(
                        onClick = {
                            navHandler.push(QuizNavKey.Scratchpad)
                        },
                        modifier = Modifier
                            .height(ButtonDefaults.ExtraSmallContainerHeight)
                            .padding(horizontal = 8.dp),
                        contentPadding = ButtonDefaults.ExtraSmallContentPadding
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.PencilLine,
                            contentDescription = "Scratchpad",
                            modifier = Modifier.size(ButtonDefaults.ExtraSmallIconSize)
                        )
                        Spacer(Modifier.width(ButtonDefaults.ExtraSmallIconSpacing))
                        Text("Coretan")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            val pagerState = rememberPagerState { uiState.questions.size }
            val isLastPage = pagerState.currentPage == pagerState.pageCount - 1
            val canFinish = uiState.selectedOptionIds.size == uiState.questions.size

            PageIndicator(
                size = uiState.questions.size,
                currentIndex = pagerState.currentPage,
                onClick = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection)
                    )
                    .padding(vertical = 16.dp)
                    .alignHorizontalSpace(16.dp)
            )
            HorizontalDivider()
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                val questions = uiState.questions[index]
                val selectedOptionId = uiState.selectedOptionIds[index]

                val scrollState = rememberScrollState()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(
                            start = innerPadding.calculateStartPadding(layoutDirection),
                            end = innerPadding.calculateEndPadding(layoutDirection)
                        )
                        .padding(vertical = 16.dp)
                        .alignHorizontalSpace(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card {
                        ShapeCanvas(
                            subtopic = uiState.subtopic,
                            segments = uiState.segments,
                            angles = uiState.angles,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(12.dp)
                        )
                    }
                    Text(
                        text = questions.text,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        questions.options.forEach {
                            OptionCard(
                                text = it.text,
                                selected = selectedOptionId == it.id,
                                onClick = {
                                    onEvent(QuestionsEvent.OnSelectOption(index, it.id))
                                }
                            )
                        }
                    }
                }
            }
            HorizontalDivider()
            Button(
                onClick = {
                    if (canFinish) {
                        onEvent(
                            QuestionsEvent.OnFinish(
                                onResultSaved = { score, isCompleted ->
                                    navHandler.replace(QuizNavKey.Result(score, isCompleted))
                                }
                            )
                        )
                        return@Button
                    }
                    if (!isLastPage) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        end = innerPadding.calculateEndPadding(layoutDirection)
                    )
                    .padding(vertical = 16.dp)
                    .alignHorizontalSpace(16.dp),
                enabled = !isLastPage || canFinish
            ) {
                Text(
                    text = if (isLastPage || canFinish) "Selesai" else "Berikutnya"
                )
            }
        }
    }
}

@Composable
@Preview
private fun QuestionsScreenPreview() {
    GeoPocketTheme {
        QuestionsScreenContent(
            uiState = QuestionsUiState(),
            onEvent = {},
            navHandler = NavHandler()
        )
    }
}