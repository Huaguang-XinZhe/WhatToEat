package com.huaguang.whattoeat

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


//@Preview(showBackground = true)
@Composable
fun SwipeToRefreshTest(
    modifier: Modifier = Modifier
) {
    val list = remember {
        List(4){ "Item $it" }.toMutableStateList()
    }
    var refreshing by remember { mutableStateOf(false) } // 修改这一行
    // 用协程模拟一个耗时加载
    val scope = rememberCoroutineScope()
    val state = rememberPullRefreshState(refreshing = refreshing, onRefresh = {
        scope.launch {
            refreshing = true
            delay(1000) // 模拟数据加载
            list += "Item ${list.size}"
            refreshing = false
        }
    })
    Box(modifier = modifier
        .fillMaxSize()
        .pullRefresh(state)
    ){
        //注意，这里的大小必须换成 fillMaxSize()，如果使用 fillMaxWidth()，那它就只会在列表中有元素显示的区域生效，
        //换句话说，在无列表元素显示的空白区域下拉刷新是没有效果的。
        LazyColumn(Modifier.fillMaxSize()) {
            items(list) { item ->
                Text(
                    text = item,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                )
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }
}



@Composable
fun TopToast(
    message: String,
    showToast: MutableState<Boolean>,
    durationMillis: Long = 3000L,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(showToast.value) {
        if (showToast.value) {
            coroutineScope.launch {
                delay(durationMillis)
                onDismiss()
            }
        }
    }

    if (showToast.value) {
        val enterTransition = remember { slideInVertically(initialOffsetY = { -it }) }
        val exitTransition = remember { slideOutVertically(targetOffsetY = { -it }) }

        AnimatedVisibility(
            visible = showToast.value,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = getStatusBarHeight())
                    .fillMaxWidth()
                    .wrapContentHeight(),
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreen() {
    val showToast = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopToast("This is a top toast", showToast) {
            showToast.value = false
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { showToast.value = true }) {
            Text("Show Top Toast")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@SuppressLint("InternalInsetResource", "DiscouragedApi")
@Composable
fun getStatusBarHeight(): Dp {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        "status_bar_height", "dimen", "android"
    )
    val statusBarHeightPixels = if (resourceId > 0) {
        context.resources.getDimensionPixelSize(resourceId)
    } else 0

    return with(LocalDensity.current) {
        statusBarHeightPixels.toDp()
    }
}







