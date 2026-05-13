package dev.maruffirdaus.geopocket.ui.quiz.result

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import dev.maruffirdaus.geopocket.ui.common.component.ScoreBadge
import dev.maruffirdaus.geopocket.ui.common.extension.alignHorizontalSpace
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun QuizResultScreen(
    viewModel: QuizResultViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QuizResultScreenContent(
        uiState = uiState,
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuizResultScreenContent(
    uiState: QuizResultUiState,
    navHandler: NavHandler
) {
    Scaffold(
        topBar = {
            LargeFlexibleTopAppBar(
                title = {
                    Text(if (uiState.completed) "Lulus" else "Belum lulus")
                },
                subtitle = {
                    Text(
                        text = if (uiState.completed) {
                            "Kamu telah memenuhi nilai minimum"
                        } else {
                            "Kamu belum memenuhi nilai minimum"
                        }
                    )
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
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(vertical = 16.dp)
                .alignHorizontalSpace(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(0.5f))
            ScoreBadge(
                score = uiState.score,
                completed = uiState.completed,
                modifier = Modifier.weight(2f)
            )
            Spacer(Modifier.weight(1f))
            Button(
                onClick = {
                    navHandler.pop<AppNavKey>()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Selesai")
            }
        }
    }
}

@Composable
@Preview
private fun QuizResultScreenPreview() {
    GeoPocketTheme {
        QuizResultScreenContent(
            uiState = QuizResultUiState(),
            navHandler = NavHandler()
        )
    }
}