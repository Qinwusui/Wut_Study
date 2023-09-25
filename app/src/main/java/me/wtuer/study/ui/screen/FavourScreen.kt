package me.wtuer.study.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import me.wtuer.study.viewmodel.MainViewModel
import me.wtuer.study.viewmodel.UIState

@Composable
fun FavourScreen(
    paddingValues: PaddingValues,
    onItemClick: (bookName: String) -> Unit,
    onItemLongPress: (bookName: String) -> Unit,
    onBackPressed: () -> Unit,
    favList: UIState,
    setTitle: (title: String) -> Unit
) {
    LaunchedEffect(key1 = Unit) {
        setTitle("收藏")
    }
    when (favList) {
        is UIState.OnEmpty -> EmptyScreen(text = "啊偶，好像没有找到一本书...")
        is UIState.OnError -> ErrorScreen(onError = favList)
        is UIState.OnFinished -> FavBookListScreen(
            paddingValues = paddingValues,
            state = favList,
            onItemClick = onItemClick,
            onItemLongPress = onItemLongPress
        )

        is UIState.OnLoading -> LoadingScreen()
    }
    BackHandler {
        setTitle("题库")
        onBackPressed()
    }
}

@Composable
fun FavBookListScreen(
    paddingValues: PaddingValues,
    state: UIState.OnFinished,
    onItemClick: (bookName: String) -> Unit,
    onItemLongPress: (bookName: String) -> Unit
) {
    val bookList = state.data.bookList
    val listState = rememberLazyListState()
    LazyColumn(state = listState, contentPadding = paddingValues) {
        itemsIndexed(bookList) { index: Int, item: String ->
            Spacer(modifier = Modifier.height(10.dp))
            BookItem(
                text = item,
                onClick = { onItemClick(item) },
                onItemLongPress = { onItemLongPress(item) })
            Spacer(modifier = Modifier.height(10.dp))

        }
    }
}
