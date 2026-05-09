package dev.maruffirdaus.geopocket.ui.home

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.Gear
import dev.maruffirdaus.geopocket.R
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.Topic
import dev.maruffirdaus.geopocket.ui.common.extensions.alignHorizontalSpace
import dev.maruffirdaus.geopocket.ui.home.component.TopicCard
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    navHandler: NavHandler
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(
                        onClick = {
                            navHandler.push(AppNavKey.Settings)
                        }
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.Gear,
                            contentDescription = "Pengaturan"
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(Topic.entries.sortedBy { it.order }) { item ->
                val subtopicIds = Subtopic.entries.filter { it.topic == item }.map { it.id }.toSet()
                val unlockedSubtopics = uiState.subtopicProgresses.filter { it.id in subtopicIds }
                val completedSubtopic = unlockedSubtopics.filter { it.isCompleted }

                TopicCard(
                    topic = item,
                    unlocked = item.order == 0 || unlockedSubtopics.isNotEmpty(),
                    onClick = {
                        navHandler.push(AppNavKey.Topic(item.name))
                    },
                    modifier = Modifier.alignHorizontalSpace(),
                    progress = if (unlockedSubtopics.isEmpty()) 0f else {
                        completedSubtopic.size.toFloat() / subtopicIds.size.toFloat()
                    }
                )
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    GeoPocketTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            navHandler = NavHandler()
        )
    }
}