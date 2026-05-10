package dev.maruffirdaus.geopocket.ui.topic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.common.extensions.alignHorizontalSpace
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import dev.maruffirdaus.geopocket.ui.topic.component.SubtopicCard
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TopicScreen(
    viewModel: TopicViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TopicScreenContent(
        uiState = uiState,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopicScreenContent(
    uiState: TopicUiState,
    navHandler: NavHandler
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(uiState.topic.title)
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
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = innerPadding + PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val items = Subtopic.entries.filter { it.topic == uiState.topic }.sortedBy { it.order }

            items(items) { item ->
                val subtopicProgress = uiState.subtopicProgresses.firstOrNull { it.id == item.id }
                val unlockedSubtopicIds = uiState.subtopicProgresses.map { it.id }.toSet()

                SubtopicCard(
                    subtopic = item,
                    unlocked = item.order == 0 || item.id in unlockedSubtopicIds,
                    onClick = {
                        navHandler.push(AppNavKey.Instructions(item.name))
                    },
                    modifier = Modifier.alignHorizontalSpace(),
                    highestScore = subtopicProgress?.highestScore,
                    completed = subtopicProgress?.isCompleted ?: false
                )
            }
        }
    }
}

@Composable
@Preview
private fun TopicScreenPreview() {
    GeoPocketTheme {
        TopicScreenContent(
            uiState = TopicUiState(),
            navHandler = NavHandler()
        )
    }
}