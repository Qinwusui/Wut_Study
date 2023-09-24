package me.wtuer.study.ui.screen

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.wtuer.study.primaryColor
import me.wtuer.study.viewmodel.MainViewModel
import me.wtuer.study.viewmodel.UIState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by mainViewModel.bookList.collectAsState()
    var showSearchInput by remember {
        mutableStateOf(false)
    }
    var showSearchButton by remember {
        mutableStateOf(true)
    }
    var text by rememberSaveable {
        mutableStateOf("")
    }
    val navigator = rememberNavController()
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setSystemBarsColor(primaryColor)
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "博学题库", maxLines = 1, overflow = TextOverflow.Ellipsis)
                },
                actions = {
                    AnimatedVisibility(
                        visible = showSearchInput,
                        modifier = Modifier.weight(1f, false)
                    ) {
                        SearchInput(
                            text = text,
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentHeight(),
                            onValueChange = { text = it },
                            onSearch = {
                                keyboardController?.hide()
                                mainViewModel.searchBook(text)
                            },
                            onClear = {
                                text = ""
                                keyboardController?.hide()

                                mainViewModel.requestBooks()
                            }
                        )
                    }
                    AnimatedVisibility(
                        visible = showSearchButton,
                        modifier = Modifier.weight(0.3f, false)
                    ) {
                        IconButton(onClick = {
                            showSearchInput = !showSearchInput
                            showSearchButton = !showSearchButton
                        }) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        }
                    }
                    IconButton(onClick = {

                    }) {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        NavHost(navController = navigator, startDestination = "main") {
            composable("main") {
                when (uiState) {
                    is UIState.OnLoading -> LoadingScreen()
                    is UIState.OnError -> ErrorScreen(uiState as UIState.OnError)
                    is UIState.OnFinished -> BookListScreen(
                        padding,
                        (uiState as UIState.OnFinished)
                    )

                    is UIState.OnEmpty -> EmptyScreen(text = (uiState as UIState.OnEmpty).text)
                }
            }
            composable("book") {

            }
        }

    }

}

@Composable
fun EmptyScreen(text: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = text, fontSize = 12.sp)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchInput(
    modifier: Modifier,
    text: String,
    onValueChange: (text: String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    val imeManager = LocalSoftwareKeyboardController.current
    BasicTextField(
        value = text,
        onValueChange = onValueChange,
        singleLine = true,
        modifier = modifier,
        textStyle = TextStyle(
            color = Color.White
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onSearch()
            },
            onGo = { onSearch() },
            onSearch = { onSearch() }
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        cursorBrush = Brush.horizontalGradient(
            listOf(
                Color.White,
                Color(0xff6492c2),
                Color(0xff326da1),
                Color(0xff285288)
            )
        ),
        decorationBox = { innerTextField: @Composable () -> Unit ->

            Card(
                colors = CardDefaults.cardColors(
                    contentColor = Color.White,
                    containerColor = Color.White.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.weight(1f, false)
                ) {
                    Column(
                        modifier = Modifier
                            .height(40.dp)
                            .weight(1f, false)
                            .padding(start = 5.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        innerTextField()
                    }
                    IconButton(
                        onClick = {
                            onClear()
                            imeManager?.hide()
                        }, modifier = Modifier
                            .size(40.dp)
                            .weight(0.1f, false)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                    IconButton(onClick = {
                        onSearch()
                        imeManager?.hide()
                    }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                }
            }
        }
    )
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(10.dp))

        Text(text = "加载中..", fontSize = 12.sp)
    }
}

@Composable
fun ErrorScreen(onError: UIState.OnError) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.Close, contentDescription = null)
        Text(text = "啊偶,加载书籍列表失败了...")
        Text(text = "${onError.exception.message}")
    }
}