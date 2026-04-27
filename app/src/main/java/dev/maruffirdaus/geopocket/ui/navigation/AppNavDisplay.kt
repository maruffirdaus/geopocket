package dev.maruffirdaus.geopocket.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.domain.topic.Topic
import dev.maruffirdaus.geopocket.ui.ar.ARScreen
import dev.maruffirdaus.geopocket.ui.home.HomeScreen
import dev.maruffirdaus.geopocket.ui.instructions.InstructionsScreen
import dev.maruffirdaus.geopocket.ui.licenses.LicensesScreen
import dev.maruffirdaus.geopocket.ui.quiz.navigation.QuizNavDisplay
import dev.maruffirdaus.geopocket.ui.settings.SettingsScreen
import dev.maruffirdaus.geopocket.ui.topic.TopicScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun AppNavDisplay(
    navHandler: NavHandler = koinInject()
) {
    NavDisplay(
        backStack = navHandler.appBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<AppNavKey.Home> {
                HomeScreen()
            }
            entry<AppNavKey.Topic> { key ->
                TopicScreen(
                    viewModel = koinViewModel {
                        parametersOf(Topic.valueOf(key.topic))
                    }
                )
            }
            entry<AppNavKey.Instructions> { key ->
                InstructionsScreen(
                    viewModel = koinViewModel {
                        parametersOf(Subtopic.valueOf(key.subtopic))
                    }
                )
            }
            entry<AppNavKey.AR> { key ->
                ARScreen(
                    viewModel = koinViewModel {
                        parametersOf(Subtopic.valueOf(key.subtopic))
                    }
                )
            }
            entry<AppNavKey.Quiz> { key ->
                QuizNavDisplay(
                    subtopic = key.subtopic,
                    segments = key.segments,
                    angles = key.angles
                )
            }
            entry<AppNavKey.Settings> {
                SettingsScreen()
            }
            entry<AppNavKey.Licenses> {
                LicensesScreen()
            }
        }
    )
}