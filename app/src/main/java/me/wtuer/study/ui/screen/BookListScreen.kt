package me.wtuer.study.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.wtuer.study.backgroundColor
import me.wtuer.study.primaryColor
import me.wtuer.study.viewmodel.UIState

@Composable
fun BookListScreen(
    padding: PaddingValues,
    state: UIState.OnFinished
) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState, contentPadding = padding) {
        itemsIndexed(state.data.bookList) { index: Int, item: String ->
            Spacer(modifier = Modifier.height(10.dp))
            BookItem(text = item)
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
fun BookItem(text: String) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .heightIn(80.dp)
            .clickable(
                enabled = true,
                indication = rememberRipple(
                    bounded = true, radius = 500.dp,
                    backgroundColor
                ),
                interactionSource = interactionSource,

                ) {

            }

            .background(Color.White, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(15.dp),
        color = primaryColor
    ) {
        Column(
            modifier = Modifier
                .heightIn(40.dp)
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = text.replace(".json",""), color = backgroundColor)
        }
    }
}