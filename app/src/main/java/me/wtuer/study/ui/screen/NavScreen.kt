package me.wtuer.study.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wtuer.study.viewmodel.MainViewModel
import me.wtuer.study.viewmodel.UIState

@Composable
fun NavScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.bookList.collectAsState()
    when (uiState) {
        is UIState.OnLoading -> {}
        is UIState.OnError -> {}
        is UIState.OnFinished -> {}
    }

}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
    }
}