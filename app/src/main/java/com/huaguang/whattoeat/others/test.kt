package com.huaguang.whattoeat.others

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.pullrefresh.PullRefreshIndicator
import androidx.compose.material3.pullrefresh.pullRefresh
import androidx.compose.material3.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

sealed class Screen2(val route: String) {
    object ScreenA : Screen2("screen_a")
    object ScreenB : Screen2("screen_b/{value}") {
        fun createRoute(value: String) = "screen_b/$value"
    }
}

@Preview(showBackground = true)
@Composable
fun MyApp() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Screen2.ScreenA.route) {
        composable(Screen2.ScreenA.route) {
            ScreenA(navController)
            Log.i("吃什么？", "ScreenA：执行！")
        }
        composable(Screen2.ScreenB.route) { backStackEntry ->
            val value = backStackEntry.arguments?.getString("value")
            ScreenB(navController, value)
            Log.i("吃什么？", "ScreenB：执行！")
        }
    }
}


@Composable
fun ScreenA(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("这是页面 A")
        Button(onClick = {
            navController.navigate(Screen2.ScreenB.createRoute("你好，页面 B！"))
        }) {
            Text("跳转到页面 B")
        }
    }
}

@Composable
fun ScreenB(navController: NavController, value: String?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Text("这是页面 B")
        Text("接收到的参数值: $value")
        Button(onClick = { navController.popBackStack() }) {
            Text("返回页面 A")
        }
    }
}



//@OptIn(ExperimentalMaterialApi::class)
//@Composable
//fun SwipeToDeleteItem2(
//    item: String,
//    onItemDeleted: () -> Unit
//) {
//    val context = LocalContext.current
//    val vibrator = context.getSystemService(Vibrator::class.java) as Vibrator
//    var hasVibrated by remember { mutableStateOf(false) }
//
//    val dismissState = rememberDismissState()
//
//    fun vibrate() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            vibrator.vibrate(
//                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
//            )
//        } else {
//            @Suppress("DEPRECATION")
//            vibrator.vibrate(200)
//        }
//    }
//
//    SwipeToDismiss(
//        state = dismissState,
//        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
//        background = {
//            val color = when (dismissState.dismissDirection) {
//                DismissDirection.StartToEnd -> MaterialTheme.colors.error
//                DismissDirection.EndToStart -> MaterialTheme.colors.error
//                else -> MaterialTheme.colors.surface
//            }
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(color)
//            )
//        },
//        dismissContent = {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(Color.White)
//            ) {
//                Text(text = item, Modifier.padding(16.dp))
//            }
//        },
//        onDismissed = { onItemDeleted() }
//    )
//
//    LaunchedEffect(dismissState.currentValue) {
//        if (dismissState.currentValue != DismissValue.Default && !hasVibrated) {
//            vibrate()
//            hasVibrated = true
//        } else if (dismissState.currentValue == DismissValue.Default) {
//            hasVibrated = false
//        }
//    }
//}














