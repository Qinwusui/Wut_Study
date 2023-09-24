package me.wtuer.study.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wtuer.study.viewmodel.MainViewModel

@Composable
fun FavourScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val favList = mainViewModel.favBookList.collectAsState()

}