package me.wtuer.study.viewmodel

import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.wtuer.study.app.BaseApp
import java.io.InputStreamReader

@Serializable
@Keep
data class Books(
    val bookList: List<String> = listOf()
)

sealed class UIState {
    object OnLoading : UIState()
    class OnFinished(val data: Books) : UIState()
    class OnError(val exception: Exception) : UIState()
    class OnEmpty(val text: String) : UIState()
}

class MainViewModel : ViewModel() {


    private val _booksList = MutableStateFlow<UIState>(UIState.OnLoading)
    val bookList = _booksList.asStateFlow()

    private val _favBookList = MutableStateFlow<UIState>(UIState.OnLoading)
    val favBookList = _favBookList.asStateFlow()

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
                val bookList = json.decodeFromString<Books>(bookListReader.getOrThrow())
                _booksList.value = UIState.OnFinished(bookList)
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
                _booksList.value = UIState.OnError(Exception("书籍加载错误"))
            }
        }
    }

    fun requestFavBookList() {
        viewModelScope.launch {
            _favBookList.value = UIState.OnLoading
            delay(200)
            val sp = BaseApp.context.getSharedPreferences("fav", Context.MODE_PRIVATE)
            val favours = sp.getStringSet("fav", setOf())!!.toList()
            _favBookList.value = UIState.OnFinished(Books(bookList = favours))

        }
    }

    init {
        requestBooks()
    }
}