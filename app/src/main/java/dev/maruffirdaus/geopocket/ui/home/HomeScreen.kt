package dev.maruffirdaus.geopocket.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import dev.maruffirdaus.geopocket.R
import dev.maruffirdaus.geopocket.ui.AppDestination
import dev.maruffirdaus.geopocket.ui.home.component.HomeCard
import dev.maruffirdaus.geopocket.ui.home.type.HomeItem
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        onArLineClick = {
            navController.navigate(AppDestination.Ar)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onArLineClick: () -> Unit
) {
    Column {
        LargeTopAppBar(
            title = {
                Text(stringResource(R.string.app_name))
            },
            windowInsets = WindowInsets(),
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(HomeItem.entries) { item ->
                HomeCard(
                    item = item,
                    onClick = onArLineClick,
                    modifier = Modifier.aspectRatio(1f)
                )
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    GeoPocketTheme {
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer
        ) {
            HomeScreenContent(
                uiState = HomeUiState(),
                onArLineClick = {}
            )
        }
    }
}