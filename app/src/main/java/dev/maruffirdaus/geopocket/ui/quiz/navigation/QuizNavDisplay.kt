package dev.maruffirdaus.geopocket.ui.quiz.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import dev.maruffirdaus.geopocket.domain.topic.Subtopic
import dev.maruffirdaus.geopocket.ui.common.model.AngleResult
import dev.maruffirdaus.geopocket.ui.common.model.SegmentResult
import dev.maruffirdaus.geopocket.ui.navigation.NavHandler
import dev.maruffirdaus.geopocket.ui.quiz.questions.QuestionsScreen
import dev.maruffirdaus.geopocket.ui.quiz.questions.QuestionsViewModel
import dev.maruffirdaus.geopocket.ui.quiz.scratchpad.ScratchpadScreen
import dev.maruffirdaus.geopocket.ui.quiz.scratchpad.ScratchpadViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

@Composable
fun QuizNavDisplay(
    subtopic: Subtopic,
    segments: Map<String, SegmentResult>,
    angles: Map<String, AngleResult>,
    navHandler: NavHandler = koinInject()
) {
    val questionsViewModel: QuestionsViewModel = koinViewModel {
        parametersOf(subtopic, segments, angles)
    }
    val scratchpadViewModel: ScratchpadViewModel = koinViewModel()

    NavDisplay(
        backStack = navHandler.quizBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<QuizNavKey.Questions> {
                QuestionsScreen(
                    viewModel = questionsViewModel
                )
            }
            entry<QuizNavKey.Scratchpad> {
                ScratchpadScreen(
                    viewModel = scratchpadViewModel
                )
            }
        }
    )
}