package me.wtuer.study.ui.screen

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.wtuer.study.backgroundColor
import me.wtuer.study.loge
import me.wtuer.study.primaryColor
import me.wtuer.study.toast
import me.wtuer.study.viewmodel.Book
import me.wtuer.study.viewmodel.BookState
import me.wtuer.study.viewmodel.MainViewModel
import me.wtuer.study.viewmodel.UIState

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun BookScreen(
    bookName: String,
    mainViewModel: MainViewModel,
    padding: PaddingValues,
    readMode: Boolean,
    onBackPressed: () -> Unit,
    setTitle: (title: String) -> Unit,
    currentTopItemIndex: (Int) -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        setTitle(bookName.replace(".json", ""))
        mainViewModel.requestBook(bookName = bookName)
    }
    val book by mainViewModel.currentBook.collectAsState()
    LaunchedEffect(key1 = bookName) {
        if (bookName.isEmpty()) {
            onBackPressed()
        }
    }
    var showBackTopButton by remember {
        mutableStateOf(false)
    }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Scaffold(floatingActionButton = {
        AnimatedVisibility(visible = showBackTopButton) {
            androidx.compose.material.FloatingActionButton(onClick = {
                scope.launch {
                    listState.animateScrollToItem(0)
                }
            }, backgroundColor = primaryColor) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = backgroundColor
                )
            }
        }
    }) {
        when (book) {
            is BookState.OnLoading -> LoadingScreen()
            is BookState.onError -> ErrorScreen(onError = UIState.OnError((book as BookState.onError).exception))
            is BookState.onEmpty -> EmptyScreen(text = "好像没有题目哦")
            is BookState.onFinished -> QuestionList(
                book = (book as BookState.onFinished).data,
                padding = padding,
                readMode = readMode,
                listState = listState,
                currentTopItemIndex = {
                    showBackTopButton = it != 0
                    currentTopItemIndex(it)
                }
            )
        }
    }


    BackHandler {
        setTitle("题库")
        scope.launch {
            listState.scrollToItem(0)
            onBackPressed()

        }
    }
}

@Composable
fun QuestionList(
    book: Book,
    padding: PaddingValues,
    readMode: Boolean,
    listState: LazyListState,
    currentTopItemIndex: (Int) -> Unit
) {

    LaunchedEffect(key1 = listState) {
        snapshotFlow {
            listState.firstVisibleItemScrollOffset
        }.collect {
            it.loge()
            currentTopItemIndex(it)

        }
    }
    LazyColumn(state = listState, contentPadding = padding) {
        itemsIndexed(book.items) { index, item ->
            val groupAnswerQuestion = item.split("??")
            val answers = groupAnswerQuestion[1]
            val question = groupAnswerQuestion[0]
            val groupAnswers = answers.split("||")
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .heightIn(min = 40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                //题目
                Text(
                    text = "${index + 1}.${question}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(10.dp))

                //答案
                groupAnswers.forEachIndexed { i, s ->
                    if (s.isNotEmpty()) {
                        if (i != 0) {
                            Divider()
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (groupAnswers[i].startsWith("_")) {
                                        "回答正确"
                                    } else {
                                        "回答错误"
                                    }.toast()
                                }
                                .heightIn(40.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Text(
                                text = "${i.toChar() + 65}.${groupAnswers[i].replace("_", "")}",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            )
                            if (groupAnswers[i].startsWith("_") && readMode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = primaryColor
                                )
                            }

                        }
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))

            }
        }
        item {
            Spacer(modifier = Modifier.height(50.dp))

        }
    }
}