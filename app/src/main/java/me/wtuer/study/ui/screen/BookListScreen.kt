package me.wtuer.study.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
    state: UIState.OnFinished,
    onItemClick: (bookName: String) -> Unit,
    onItemLongPress: (bookName: String) -> Unit
) {
    val listState = rememberLazyStaggeredGridState()
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = listState,
        contentPadding = padding
    ) {
        item {
            Surface(
                color = primaryColor,
                modifier = Modifier
                    .heightIn(40.dp)
                    .fillMaxWidth()
                    .padding(10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = """
                        Tips:
                        1.点击右上角放大镜按钮可以进行搜索
                        
                        2.点击右上角爱心按钮可以进入收藏页面
                        
                        3.长按题库中的某一书籍卡片可以进行书籍收藏
                        
                        4.长按收藏中的某一书籍卡片可以进行书籍取消收藏
                        
                        5.在某一本书籍题目页面可以点击右上角的随机按钮可以从题目列表中随机30题进行刷题看题
                        
                        6.在某一本书籍题目页面可以点击右上角的🖊按钮可以切换模式（刷题/看题）
                    """.trimIndent(),
                        color = backgroundColor
                    )
                }
            }
        }
        itemsIndexed(state.data.bookList) { index: Int, item: String ->
            Spacer(modifier = Modifier.size(10.dp))
            BookItem(
                text = item,
                onClick = { onItemClick(item) },
                onItemLongPress = { onItemLongPress(item) })
            Spacer(modifier = Modifier.size(10.dp))
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BookItem(
    text: String,
    onClick: () -> Unit,
    onItemLongPress: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding( 10.dp)
            .heightIn(80.dp)
            .combinedClickable(
                enabled = true,
                indication = rememberRipple(
                    bounded = true, radius = 500.dp,
                    backgroundColor
                ),
                interactionSource = interactionSource,
                onClick = {
                    onClick()
                },
                onLongClick = { onItemLongPress() }
            )

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
            Text(text = text.replace(".json", ""), color = backgroundColor)
        }
    }
}