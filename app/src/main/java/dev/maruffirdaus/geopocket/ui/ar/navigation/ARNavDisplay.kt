package dev.maruffirdaus.geopocket.ui.ar.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.ar.activity.ARActivityScreen
import dev.maruffirdaus.geopocket.ui.ar.result.ARResultScreen
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun ARNavDisplay(
    subtopic: Subtopic,
    navHandler: NavHandler = koinInject()
) {
    NavDisplay(
        backStack = navHandler.arBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<ARNavKey.Activity> {
                ARActivityScreen(
                    viewModel = koinViewModel {
                        parametersOf(subtopic)
                    }
                )
            }
            entry<ARNavKey.Result> { key ->
                ARResultScreen(
                    viewModel = koinViewModel {
                        parametersOf(key.subtopic, key.segments, key.angles, key.completionImage)
                    }
                )
            }
        }
    )
}