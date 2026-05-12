package dev.maruffirdaus.geopocket.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import dev.maruffirdaus.geopocket.ui.quiz.navigation.QuizNavKey
import org.koin.core.annotation.Singleton

@Singleton
class NavHandler {
    val appBackStack = mutableStateListOf<AppNavKey>(AppNavKey.Home)
    val quizBackStack = mutableStateListOf<QuizNavKey>(QuizNavKey.Questions)

    fun push(key: NavKey) {
        when (key) {
            is AppNavKey.Quiz -> {
                reset<QuizNavKey>()
                appBackStack.add(key)
            }

            is AppNavKey -> appBackStack.add(key)
            is QuizNavKey -> quizBackStack.add(key)
        }
    }

    fun replace(key: NavKey) {
        when (key) {
            is AppNavKey.Quiz -> {
                reset<QuizNavKey>()
                appBackStack[appBackStack.lastIndex] = key
            }

            is AppNavKey -> appBackStack[appBackStack.lastIndex] = key
            is QuizNavKey -> quizBackStack[quizBackStack.lastIndex] = key
        }
    }

    inline fun <reified T : NavKey> pop() {
        when (T::class) {
            AppNavKey::class -> if (appBackStack.size > 1) appBackStack.removeLastOrNull()
            QuizNavKey::class -> if (quizBackStack.size > 1) quizBackStack.removeLastOrNull()
        }
    }

    private inline fun <reified T : NavKey> reset() {
        when (T::class) {
            QuizNavKey::class -> {
                quizBackStack.clear()
                quizBackStack.add(QuizNavKey.Questions)
            }
        }
    }
}