package me.wtuer.study.ui.screen

import android.app.Activity
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.twotone.Shuffle
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import me.wtuer.study.primaryColor
import me.wtuer.study.toast
import me.wtuer.study.viewmodel.MainViewModel
import me.wtuer.study.viewmodel.UIState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun NavScreen(
    mainViewModel: MainViewModel = viewModel()
) {
    LaunchedEffect(key1 = Unit) {
        mainViewModel.requestFavBookList()
    }
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current
    SideEffect {
        systemUiController.setSystemBarsColor(primaryColor)
    }
    val bookList by mainViewModel.bookList.collectAsState()
    val favList by mainViewModel.favBookList.collectAsState()
    val navigator = rememberNavController()
    var showSearchInput by remember {
        mutableStateOf(false)
    }
    var showSearchButton by remember {
        mutableStateOf(true)
    }
    var text by rememberSaveable {
        mutableStateOf("")
    }


    val keyboardController = LocalSoftwareKeyboardController.current

    var title by rememberSaveable {
        mutableStateOf("题库")
    }
    var showRandomButton by remember {
        mutableStateOf(false)
    }
    var showModeChangeButton by remember {
        mutableStateOf(false)
    }
    var inReadMode by rememberSaveable {
        mutableStateOf(true)
    }
    var showFavButton by remember {
        mutableStateOf(true)
    }
    var showTopBar by remember {
        mutableStateOf(true)
    }
    var showBackButton by remember {
        mutableStateOf(false)
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AnimatedVisibility(visible = showTopBar) {
                TopAppBar(
                    title = {
                        Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                                    showSearchInput = false
                                    showSearchButton = true
                                    showTopBar = true
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
                        AnimatedVisibility(visible = showRandomButton) {
                            IconButton(onClick = {
                                "随机30题".toast()
                                mainViewModel.randomBookQuestion()
                            }) {
                                Icon(imageVector = Icons.TwoTone.Shuffle, contentDescription = null)
                            }
                        }
                        AnimatedVisibility(visible = showModeChangeButton) {
                            IconButton(onClick = {
                                inReadMode = !inReadMode
                                if (inReadMode) {
                                    "看题模式"
                                } else {
                                    "刷题模式"
                                }.toast()
                            }) {
                                Icon(
                                    imageVector = if (inReadMode) Icons.Default.EditNote else Icons.Default.Edit,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility(visible = showBackButton) {
                            IconButton(onClick = {
                                showBackButton = false
                                showFavButton = true
                                navigator.popBackStack()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility(visible = showFavButton) {
                            IconButton(onClick = {
                                showFavButton = false
                                showBackButton = true
                                navigator.popBackStack("fav", true)
                                navigator.navigate("fav")
                                title = "收藏"
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        },

        ) { padding ->
        NavHost(navController = navigator, startDestination = "bookList") {
            composable("bookList") {
                when (bookList) {
                    is UIState.OnLoading -> LoadingScreen()
                    is UIState.OnError -> ErrorScreen(bookList as UIState.OnError)
                    is UIState.OnFinished -> BookListScreen(
                        padding = padding,
                        state = (bookList as UIState.OnFinished),
                        onItemClick = {
                            title = it.replace(".json", "")
                            showSearchButton = false
                            showSearchInput = false
                            showFavButton = false
                            navigator.navigate("book/$it")
                        },
                        onItemLongPress = {
                            "${it.replace(".json", "")} 添加成功".toast()
                            mainViewModel.addBook(it)
                        }
                    )

                    is UIState.OnEmpty -> EmptyScreen(text = (bookList as UIState.OnEmpty).text)
                }
            }
            composable("fav") { _ ->
                FavourScreen(
                    favList = favList,
                    paddingValues = padding,
                    onItemClick = {
                        title = it.replace(".json", "")
                        showSearchButton = false
                        showFavButton = false

                        navigator.navigate("book/$it")
                    },
                    onBackPressed = {
                        title = "题库"
                        showBackButton = false
                        showSearchButton = true
                        showFavButton = true
                        showTopBar = true
                        showRandomButton = false
                        showModeChangeButton = false
                        navigator.popBackStack()

                    },
                    onItemLongPress = {
                        "${it.replace(".json", "")} 移除成功".toast()
                        mainViewModel.deleteBook(it)
                    },
                    setTitle = {
                        title = it
                        showRandomButton = false
                        showModeChangeButton = false
                    }
                )
            }
            composable(
                route = "book/{bookName}",
                arguments = listOf(
                    navArgument("bookName") {
                        type = NavType.StringType
                    }
                )) { navBackStackEntry ->
                if (navBackStackEntry.arguments != null) {

                    BookScreen(
                        padding = padding,
                        bookName = navBackStackEntry.arguments!!.getString("bookName", "")!!,
                        readMode = inReadMode,
                        mainViewModel = mainViewModel,
                        onBackPressed = {
                            showTopBar = true
                            showSearchButton = true
                            showFavButton = true

                            showRandomButton = false
                            showModeChangeButton = false
                            navigator.popBackStack()


                        },
                        setTitle = {
                            title = it
                            showRandomButton = true
                            showModeChangeButton = true
                            showBackButton = false
                        },
                        currentTopItemIndex = {
                            showTopBar = it == 0
                            if (!showTopBar) {
                                WindowCompat.setDecorFitsSystemWindows(
                                    (context as Activity).window,
                                    false
                                )
                            }
                            systemUiController.setSystemBarsColor(
                                color = if (showTopBar) primaryColor else Color.Transparent,
                                !showTopBar
                            )

                        }
                    )
                } else {
                    ErrorScreen(onError = UIState.OnError(Exception("啊偶，好像没有找到这本书哦")))
                }
            }
        }

    }
    BackHandler {
        (context as Activity).moveTaskToBack(false)
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