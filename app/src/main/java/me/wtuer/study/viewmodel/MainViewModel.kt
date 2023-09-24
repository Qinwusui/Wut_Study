package me.wtuer.study.viewmodel

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import me.wtuer.study.app.BaseApp
import java.io.InputStreamReader

@Serializable
@Keep
data class Books(
    val items: List<String> = listOf()
)

sealed class UIState {
    object OnLoading : UIState()
    class OnFinished(data: Books) : UIState()
    class OnError(exception: Exception) : UIState()
}

class MainViewModel : ViewModel() {


    private val _booksList = MutableStateFlow<UIState>(UIState.OnLoading)
    val bookList = _booksList.asStateFlow()


    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private fun requestBooks() {
        _booksList.value = UIState.OnLoading
        val assets = BaseApp.context.assets
        val bookListReader = InputStreamReader(assets.open("bookList.json")).readText()
        val bookList = json.decodeFromString<Books>(bookListReader)
        _booksList.value = UIState.OnFinished(bookList)
    }

    init {
        requestBooks()
    }
}