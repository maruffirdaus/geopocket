package dev.maruffirdaus.geopocket.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    homeScreen: @Composable () -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MainScreenContent(
        uiState = uiState,
        homeScreen = homeScreen,
        onSelectedNavItemChange = viewModel::changeSelectedNavItem
    )
}

@Composable
fun MainScreenContent(
    uiState: MainUiState,
    homeScreen: @Composable () -> Unit,
    onSelectedNavItemChange: (NavItem) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavItem.entries.forEach { navItem ->
                    val isSelected = uiState.selectedNavItem == navItem

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            onSelectedNavItemChange(navItem)
                        },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) {
                                    navItem.selectedIcon
                                } else {
                                    navItem.unselectedIcon
                                },
                                contentDescription = navItem.label
                            )
                        },
                        label = {
                            Text(navItem.label)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (uiState.selectedNavItem) {
                NavItem.HOME -> homeScreen()
            }
        }
    }
}

@Composable
@Preview
private fun MainScreenPreview() {
    GeoPocketTheme {
        MainScreenContent(
            uiState = MainUiState(),
            homeScreen = {},
            onSelectedNavItemChange = {}
        )
    }
}