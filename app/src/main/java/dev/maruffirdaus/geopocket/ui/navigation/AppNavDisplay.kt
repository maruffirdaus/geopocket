package dev.maruffirdaus.geopocket.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.maruffirdaus.geopocket.ui.ar.ArScreen
import dev.maruffirdaus.geopocket.ui.common.model.ArPlacingMode
import dev.maruffirdaus.geopocket.ui.main.MainScreen
import dev.maruffirdaus.geopocket.ui.main.home.HomeScreen

@Composable
fun AppNavDisplay(
    backStack: NavBackStack<NavKey> = rememberNavBackStack(AppNavKey.Main)
) {
    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<AppNavKey.Main> {
                MainScreen(
                    homeScreen = {
                        HomeScreen(
                            onNavigate = { key ->
                                backStack.add(key)
                            }
                        )
                    }
                )
            }
            entry<AppNavKey.Ar> { key ->
                ArScreen(
                    mode = ArPlacingMode.valueOf(key.mode),
                    onNavigateBack = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    }
                )
            }
        }
    )
}