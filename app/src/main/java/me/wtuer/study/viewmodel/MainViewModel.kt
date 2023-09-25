package me.wtuer.study.viewmodel

import android.content.Context
import androidx.annotation.Keep
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.wtuer.study.app.BaseApp
import me.wtuer.study.loge
import java.io.InputStreamReader

@Serializable
@Keep
data class Books(
    val bookList: List<String> = listOf()
)

@Serializable
@Keep
data class Book(
    val items: List<String>
)

sealed class UIState {
    object OnLoading : UIState()
    class OnFinished(val data: Books) : UIState()
    class OnError(val exception: Exception) : UIState()
    class OnEmpty(val text: String) : UIState()
}

sealed class BookState {
    object OnLoading : BookState()
    class onFinished(val data: Book) : BookState()
    class onError(val exception: Exception) : BookState()
    class onEmpty(val text: String) : BookState()
}

class MainViewModel : ViewModel() {


    private val _booksList = MutableStateFlow<UIState>(UIState.OnLoading)
    val bookList = _booksList.asStateFlow()

    private val _favBookList = MutableStateFlow<UIState>(UIState.OnLoading)
    val favBookList = _favBookList.asStateFlow()

    private val _currentBook = MutableStateFlow<BookState>(BookState.OnLoading)
    val currentBook = _currentBook.asStateFlow()
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    fun requestBooks() {
        viewModelScope.launch {
            _booksList.value = UIState.OnLoading
            delay(400)
            val assets = BaseApp.context.assets
            val bookListReader = kotlin.runCatching {
                InputStreamReader(assets.open("bookList.json")).readText()
            }
            if (bookListReader.isSuccess) {
                val bookList =
                    json.decodeFromString<Books>(bookListReader.getOrThrow()).bookList.shuffled()
                _booksList.value = UIState.OnFinished(Books(bookList))
            } else {
                _booksList.value = UIState.OnError(Exception(bookListReader.exceptionOrNull()))
            }
        }

    }

    fun searchBook(keyWord: String) {
        viewModelScope.launch {
            if (_booksList.value is UIState.OnFinished) {
                val list = (_booksList.value as UIState.OnFinished).data.bookList
                _booksList.value = UIState.OnLoading
                delay(300)

                val searchList = list.filter {
                    it.contains(keyWord.replace(".json", ""))
                }
                _booksList.value = if (searchList.isEmpty()) {
                    UIState.OnEmpty("啊偶，你的关键词好像有点特别哦，什么都没有找到...")
                } else {
                    UIState.OnFinished(Books(bookList = searchList))
                }

            } else {
                _booksList.value = UIState.OnError(Exception("啊偶，书籍加载出错了..."))
            }
            if (_favBookList.value is UIState.OnFinished) {
                val favList = (_favBookList.value as UIState.OnFinished).data.bookList
                _favBookList.value = UIState.OnLoading
                delay(300)
                val searchFavList = favList.filter {
                    it.contains(keyWord.replace(".json", ""))
                }
                _favBookList.value = if (searchFavList.isEmpty()) {
                    UIState.OnEmpty("啊偶，你搜索的关键词有一点特殊哦...")
                } else {
                    UIState.OnFinished(Books(bookList = searchFavList))
                }
            } else {
                _favBookList.value = UIState.OnError(Exception("啊偶，书籍加载出错了..."))

            }

        }
    }

    fun requestFavBookList() {
        viewModelScope.launch {
            _favBookList.value = UIState.OnLoading
            delay(200)
            val sp = BaseApp.context.getSharedPreferences("fav", Context.MODE_PRIVATE)
            val favours = sp.getStringSet("fav", setOf())!!.toList()
            _favBookList.value = if (favours.isEmpty()) {
                UIState.OnEmpty("啊偶，收藏书籍列表为空...")
            } else {
                UIState.OnFinished(Books(bookList = favours))
            }
            favours.loge()
        }
    }

    fun addBook(text: String) {
        viewModelScope.launch {
            if (_booksList.value is UIState.OnFinished) {
                val sp = BaseApp.context.getSharedPreferences("fav", Context.MODE_PRIVATE)
                val s = sp.getStringSet("fav", setOf())!!.toMutableSet()
                s.add(text)
                sp.edit(commit = true) {
                    putStringSet("fav", s)
                }
                _favBookList.value = UIState.OnFinished(Books(s.toList()))
                "添加完成".loge()
            } else {
                _favBookList.value = UIState.OnLoading
            }
        }
    }

    fun deleteBook(text: String) {
        viewModelScope.launch {
            if (_favBookList.value is UIState.OnFinished) {
                val sp = BaseApp.context.getSharedPreferences("fav", Context.MODE_PRIVATE)
                val s = sp.getStringSet("fav", setOf())!!.toMutableSet()
                s.remove(text)
                sp.edit(true) {
                    putStringSet("fav", s)
                }
                _favBookList.value = UIState.OnFinished(Books(s.toList()))
            } else {
                _favBookList.value = UIState.OnLoading

            }
        }
    }

    fun requestBook(bookName: String) {
        viewModelScope.launch {
            _currentBook.value = BookState.OnLoading
            delay(1000)
            val assets = BaseApp.context.assets
            val state = kotlin.runCatching {
                InputStreamReader(assets.open(bookName)).buffered().readText()
            }
            _currentBook.value = if (state.isSuccess) {
                val book = json.decodeFromString<Book>(state.getOrThrow())
                BookState.onFinished(book)
            } else {
                BookState.onError(Exception(state.exceptionOrNull()))
            }
        }
    }

    fun randomBookQuestion() {
        if (_currentBook.value is BookState.onFinished) {
            val take = (_currentBook.value as BookState.onFinished).data.items.shuffled().take(30)
            _currentBook.value = BookState.onFinished(Book(items = take))
        }
    }

    init {
        requestBooks()
        requestFavBookList()

    }
}