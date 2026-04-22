package dev.maruffirdaus.geopocket.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.Topic
import dev.maruffirdaus.geopocket.ui.achievement.AchievementScreen
import dev.maruffirdaus.geopocket.ui.ar.ARScreen
import dev.maruffirdaus.geopocket.ui.home.HomeScreen
import dev.maruffirdaus.geopocket.ui.instructions.InstructionsScreen
import dev.maruffirdaus.geopocket.ui.settings.SettingsScreen
import dev.maruffirdaus.geopocket.ui.topic.TopicScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavDisplay(
    backStack: NavBackStack<NavKey> = rememberNavBackStack(AppNavKey.Home)
) {
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<AppNavKey.Home> {
                HomeScreen(
                    onNavigate = { key ->
                        backStack.add(key)
                    }
                )
            }
            entry<AppNavKey.Topic> { key ->
                TopicScreen(
                    onNavigate = { key ->
                        backStack.add(key)
                    },
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    },
                    viewModel = koinViewModel {
                        parametersOf(Topic.valueOf(key.topic))
                    }
                )
            }
            entry<AppNavKey.Instructions> { key ->
                InstructionsScreen(
                    onNavigate = { key ->
                        backStack.add(key)
                    },
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    },
                    viewModel = koinViewModel {
                        parametersOf(Subtopic.valueOf(key.subtopic))
                    }
                )
            }
            entry<AppNavKey.AR> { key ->
                ARScreen(
                    subtopic = Subtopic.valueOf(key.subtopic),
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    }
                )
            }
            entry<AppNavKey.Achievement> {
                AchievementScreen(
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    }
                )
            }
            entry<AppNavKey.Settings> {
                SettingsScreen(
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    }
                )
            }
        }
    )
}