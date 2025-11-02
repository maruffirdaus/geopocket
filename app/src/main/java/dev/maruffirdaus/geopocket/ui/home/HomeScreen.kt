package dev.maruffirdaus.geopocket.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
            navController.navigate(AppDestination.ArLine)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onArLineClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CenterAlignedTopAppBar(
            title = {
                Text(stringResource(R.string.app_name))
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                onClick = onArLineClick,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Line",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
@Preview
private fun HomeScreenPreview() {
    GeoPocketTheme {
        Surface {
            HomeScreenContent(
                uiState = HomeUiState(),
                onArLineClick = {}
            )
        }
    }
}