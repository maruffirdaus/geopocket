package dev.maruffirdaus.geopocket.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.navigation3.runtime.NavKey
import dev.maruffirdaus.geopocket.ui.ar.navigation.ARNavKey
import dev.maruffirdaus.geopocket.ui.quiz.navigation.QuizNavKey
import org.koin.core.annotation.Singleton

@Singleton
class NavHandler {
    val appBackStack = mutableStateListOf<AppNavKey>(AppNavKey.Home)
    val arBackStack = mutableStateListOf<ARNavKey>(ARNavKey.Activity)
    val quizBackStack = mutableStateListOf<QuizNavKey>(QuizNavKey.Questions)

    fun push(key: NavKey) {
        when (key) {
            is AppNavKey.AR -> reset<ARNavKey>()
            is AppNavKey.Quiz -> reset<QuizNavKey>()
        }

        when (key) {
            is AppNavKey -> appBackStack.add(key)
            is ARNavKey -> arBackStack.add(key)
            is QuizNavKey -> quizBackStack.add(key)
        }
    }

    fun replace(key: NavKey) {
        when (key) {
            is AppNavKey.AR -> reset<ARNavKey>()
            is AppNavKey.Quiz -> reset<QuizNavKey>()
        }

        when (key) {
            is AppNavKey -> appBackStack[appBackStack.lastIndex] = key
            is ARNavKey -> arBackStack[arBackStack.lastIndex] = key
            is QuizNavKey -> quizBackStack[quizBackStack.lastIndex] = key
        }
    }

    inline fun <reified T : NavKey> pop() {
        when (T::class) {
            AppNavKey::class -> if (appBackStack.size > 1) appBackStack.removeLastOrNull()
            ARNavKey::class -> if (arBackStack.size > 1) arBackStack.removeLastOrNull()
            QuizNavKey::class -> if (quizBackStack.size > 1) quizBackStack.removeLastOrNull()
        }
    }

    private inline fun <reified T : NavKey> reset() {
        when (T::class) {
            ARNavKey::class -> {
                arBackStack.clear()
                arBackStack.add(ARNavKey.Activity)
            }

            QuizNavKey::class -> {
                quizBackStack.clear()
                quizBackStack.add(QuizNavKey.Questions)
            }
        }
    }
}