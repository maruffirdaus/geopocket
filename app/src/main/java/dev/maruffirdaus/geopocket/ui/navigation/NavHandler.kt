package dev.maruffirdaus.geopocket.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import org.koin.core.annotation.Singleton

@Singleton
class NavHandler {
    val backStack = mutableStateListOf<AppNavKey>(AppNavKey.Home)
    private var pendingResult: ((Any) -> Unit)? = null

    fun push(key: AppNavKey, onResult: ((Any) -> Unit)? = null) {
        pendingResult = onResult
        backStack.add(key)
    }

    fun pop(result: Any? = null) {
        if (backStack.size > 1) {
            result?.let { pendingResult?.invoke(it) }
            pendingResult = null
            backStack.removeLastOrNull()
        }
    }
}