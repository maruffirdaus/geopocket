package dev.maruffirdaus.geopocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dev.maruffirdaus.geopocket.ui.navigation.AppNavDisplay
import dev.maruffirdaus.geopocket.ui.theme.GeoPocketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GeoPocketTheme {
                AppNavDisplay()
            }
        }
    }
}