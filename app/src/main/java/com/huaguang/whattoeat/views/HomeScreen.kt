package com.huaguang.whattoeat.views

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.utils.SPHelper
import com.huaguang.whattoeat.viewModel.HomeScreenViewModel
import com.huaguang.whattoeat.viewModel.HomeScreenViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    appDatabase: AppDatabase,
    spHelper: SPHelper,
    setDishesScreenArgs: (Pair<String, Int>?) -> Unit
) {
    val context = LocalContext.current
    val viewModel: HomeScreenViewModel = viewModel(
        factory = HomeScreenViewModelFactory(appDatabase, spHelper)
    )
    val scope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(false) }
    val state = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshing = true
                delay(500) // 这里添加一个延迟，模拟耗时操作
                viewModel.restart()
                refreshing = false
            }
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(state)
    ) {
        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                TitleText("吃什么？")
            }
            item {
                UiChangeText(viewModel)
            }
            item {
                RandomButtonRow(context, viewModel)
            }
            item {
                ConfirmButton(context, viewModel, scope, setDishesScreenArgs)
            }
        }

        TopRow(viewModel)
    }
}


@Composable
fun RandomButtonRow(
    context: Context,
    viewModel: HomeScreenViewModel
) {
    val expensiveDishList = mutableListOf<Boolean>()

    Row(
        modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp)
    ) {
        Button(
            onClick = {
                viewModel.bbClickState.value = true
                val contextAction = viewModel.updateDish(
                    isExpensive = false,
                    expensiveDishList = expensiveDishList
                )
                contextAction?.invoke(context)
            },
            enabled = viewModel.allowClick.value
        ) {
            Text(text = "日常随机")
        }

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            onClick = {
                viewModel.bbClickState.value = true
                val contextAction = viewModel.updateDish(
                    isExpensive = true,
                    expensiveDishList = expensiveDishList
                )
                contextAction?.invoke(context)
            },
            enabled = viewModel.allowClick.value
        ) {
            Text(text = "整点好的")
        }
    }
}

@Composable
fun TopRow(viewModel: HomeScreenViewModel) {
    Row(
        modifier = Modifier.padding(15.dp, 35.dp, 0.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = viewModel.aDishMode.value,
            onCheckedChange = {
                //这里必须要用 it，它是点击切换后产生的最新状态，
                // 不能用 false 或 true，它们是单极的！
                viewModel.aDishMode.value = it
                //注意，在 Switch 中，切换变化是双向的，必须根据 it 的的状态去逐一定制
                viewModel.modeText.value = if (it) "日常一菜" else "小聚组合"
                //只要切换就必须 restart
                viewModel.restart()
            }
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = viewModel.modeText.value,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "下拉 ↓ 刷新 ⟳ 哦",
            modifier = Modifier.padding(0.dp, 0.dp, 15.dp, 0.dp)
        )
    }
}


@Composable
fun ConfirmButton(
    context: Context,
    viewModel: HomeScreenViewModel,
    scope: CoroutineScope,
    setDishesScreenArgs: (Pair<String, Int>?) -> Unit
) {
    OutlinedButton(
        onClick = {
            scope.launch {
                // 调用 execute()，并获取返回的函数（如果有的话）
                val contextAction = viewModel.execute(setDishesScreenArgs)
                contextAction?.invoke(context)
            }
        },
        modifier = Modifier
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
            .width(150.dp),
        enabled = viewModel.bbClickState.value
    ) {
        Text(viewModel.bottomButtonText.value)
    }
}


@Composable
fun UiChangeText(viewModel: HomeScreenViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = viewModel.displayDish.value,
            modifier = Modifier
                .padding(0.dp, 35.dp, 0.dp, 35.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(10.dp)
        )

        Text(
            text = "小计：${viewModel.totalPrice.value} 元",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TitleText(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
}



