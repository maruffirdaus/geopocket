package dev.maruffirdaus.geopocket.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.maruffirdaus.geopocket.ui.ar.ArScreen
import dev.maruffirdaus.geopocket.ui.home.HomeScreen
import dev.maruffirdaus.geopocket.ui.main.MainScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Main
    ) {
        composable<AppDestination.Main> {
            MainScreen(
                homeScreen = {
                    HomeScreen(
                        navController = navController
                    )
                }
            )
        }

        composable<AppDestination.Ar> {
            ArScreen(
                navController = navController
            )
        }
    }
}