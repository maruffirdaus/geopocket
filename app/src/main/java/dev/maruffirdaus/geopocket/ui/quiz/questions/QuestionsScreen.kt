package dev.maruffirdaus.geopocket.ui.quiz.questions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adamglin.PhosphorIcons
import com.adamglin.phosphoricons.Regular
import com.adamglin.phosphoricons.regular.ArrowLeft
import com.adamglin.phosphoricons.regular.PencilLine
import dev.maruffirdaus.geopocket.ui.navigation.AppNavKey
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.quiz.navigation.QuizNavKey
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun QuestionsScreen(
    viewModel: QuestionsViewModel = koinViewModel(),
    navHandler: NavHandler = koinInject()
) {
    QuestionsScreenContent(
        navHandler = navHandler
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun QuestionsScreenContent(
    navHandler: NavHandler
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kuis")
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
                actions = {
                    FilledTonalButton(
                        onClick = {
                            navHandler.push(QuizNavKey.Scratchpad)
                        },
                        modifier = Modifier
                            .height(ButtonDefaults.ExtraSmallContainerHeight)
                            .padding(horizontal = 8.dp),
                        contentPadding = ButtonDefaults.ExtraSmallContentPadding
                    ) {
                        Icon(
                            imageVector = PhosphorIcons.Regular.PencilLine,
                            contentDescription = "Scratchpad",
                            modifier = Modifier.size(ButtonDefaults.ExtraSmallIconSize)
                        )
                        Spacer(Modifier.width(ButtonDefaults.ExtraSmallIconSpacing))
                        Text("Coretan")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {}
    }
}

@Composable
@Preview
private fun QuestionsScreenPreview() {
    GeoPocketTheme {
        QuestionsScreenContent(
            navHandler = NavHandler()
        )
    }
}