package me.wtuer.study

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import me.wtuer.study.ui.screen.NavScreen
import me.wtuer.study.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = primaryColor,
                    onPrimary = primaryColor,
                    background = backgroundColor,
//                    onBackground = backgroundColor,
                    secondary = primaryColor,
                    surface = primaryColor,
                    onSurface = Color.White,
                    onSecondary = primaryColor,
                    onSurfaceVariant = Color.White,
                    primaryContainer = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavScreen()
                }
            }
        }
    }

}

