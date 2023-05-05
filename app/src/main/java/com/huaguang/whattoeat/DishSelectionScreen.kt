package com.huaguang.whattoeat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random


data class Dish(val name: String, val realPrice: Int)

val regularDishes = listOf(
    "酸辣土豆丝木桶饭 11",
    "松仁玉米木桶饭 13",
    "韭菜炒蛋木桶饭 13",
    "肉沫豆腐木桶饭 13",
    "西红柿鸡蛋木桶饭 13",
    "碎椒炒鸡蛋木桶饭 13",
    "外婆菜炒杏爆菇 13",
    "肉沫咸菜木桶饭 13",
    "外婆菜炒包莱木桶饭 13",
    "黄瓜炒火腿肠木桶饭 14",
    "四季莲藕炒肉木桶饭 14",
    "土豆片炒肉 14",
    "悠县香干木桶饭 14",
    "千张丝炒肉木桶饭 14",
    "洋葱炒鸡蛋木桶饭 14",
    "红萝卜炒肉木桶饭 14",
    "木耳炒肉木桶饭 14",
    "莴笋炒肉木桶饭 14",
    "鱼香肉丝木桶饭 14",
    "辣椒炒肉木桶饭 15",
    "腐竹炒肉木桶饭 15",
    "蒜苔炒肉木桶饭 15",
    "黄豆烧豆腐木桶饭 15",
    "肉沫茄子木桶饭 15",
    "农家一碗香木桶饭 16",
    "开胃罗汉笋肉丝木桶饭 16",
    "蛋炒粉 8",
    "蛋炒饭 8",
    "肉炒粉 10",
    "扬州炒饭 10"
)
val aMoreExpensiveDishes = listOf(
    "鲜鱼翻身木桶饭 17",
    "红烧下饭凉拌鲫鱼 17",
    "双椒鸡木桶饭 18",
    "糖醋里脊木桶饭 19",
    "辣子鸡木桶饭 19",
    "酸辣鸡杂木桶饭 20",
    "啤酒鸭木桶饭 21",
    "小炒黄牛肉木桶饭 22",
    "腐竹烧牛腩木桶饭 22",
    "酸菜肥肠木桶饭 22",
    "鲜嫩牛蛙木桶饭 23",
    "孜然羊肉木桶饭 25"
)

const val displayDishDefaultValue = "随机，随机啦 ～(￣▽￣～)~"

/**
 * 根据菜谱返回一个随机菜肴（未分列）
 * 注意：这里的价格还没有进行处理（未减 1）
 */
fun randomDish(dishes: List<String>): String {
    val randomIndex = Random.nextInt(0, dishes.size)
    return dishes[randomIndex]
}

fun splitToDish(randomDish: String): Dish {
    val (name, price) = randomDish.split(" ")
    return Dish(name, price.toInt() - 1)
}


@Preview(showBackground = true)
@Composable
fun WhatToEat() {
    val displayDish = remember { mutableStateOf(displayDishDefaultValue) }
    val totalPrice = remember { mutableStateOf(0) }
    val displayList = mutableListOf<String>()
    val allowClick = remember { mutableStateOf(true) }
    val aDishMode = remember { mutableStateOf(true) }
    val modeText = remember { mutableStateOf("日常单品") }
    val bottomButtonText = remember { mutableStateOf("确认") }
    val bbClickState = remember { mutableStateOf(false) }
    val context = LocalContext.current
    var refreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val state = rememberPullRefreshState(
        refreshing = refreshing,
        onRefresh = {
            scope.launch {
                refreshing = true
                delay(500) // 这里添加一个延迟，模拟耗时操作
                restart(displayDish, totalPrice, displayList, allowClick, bottomButtonText)
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

        //这里必须用 LazyColumn，可滚动的组件，要不然下拉刷新会失效
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item {
                TitleText("吃什么？")
            }
            item {
                UiChangeText(displayDish, totalPrice)
            }
            item {
                RandomButtonRow(displayDish, totalPrice, displayList,
                    allowClick, aDishMode, context, bbClickState)
            }
            item {
                ConfirmButton(bottomButtonText, displayList, aDishMode,
                    displayDish, totalPrice, context, allowClick, bbClickState)
            }
        }

        TopRow(aDishMode, modeText, displayDish,
            totalPrice, displayList, allowClick, bottomButtonText)
    }
}

@Composable
fun RefreshTip(tip: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 25.dp, 0.dp, 0.dp),
        //注意，这行代码必须设置宽度才会生效
        horizontalArrangement = Arrangement.Center
    ) {
        Text(tip)
    }
}

@Composable
fun TopRow(
    aDishMode: MutableState<Boolean>,
    modeText: MutableState<String>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    displayList: MutableList<String>,
    allowClick: MutableState<Boolean>,
    bottomButtonText: MutableState<String>
) {
    Row(
        modifier = Modifier.padding(15.dp, 35.dp, 0.dp, 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = aDishMode.value,
            onCheckedChange = {
                //这里必须要用 it，它是点击切换后产生的最新状态，
                // 不能用 false 或 true，它们是单极的！
                aDishMode.value = it
                //注意，在 Switch 中，切换变化是双向的，必须根据 it 的的状态去逐一定制
                modeText.value = if (it) "日常一菜" else "小聚组合"
                //只要切换就必须 restart
                restart(displayDish, totalPrice, displayList, allowClick, bottomButtonText)
            }
        )
        
        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = modeText.value,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "下拉 ↓ 刷新 ⟳ 哦",
            modifier = Modifier.padding(0.dp, 0.dp, 15.dp, 0.dp)
        )
    }
}


private fun restart(
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    displayList: MutableList<String>,
    allowClick: MutableState<Boolean>,
    bottomButtonText: MutableState<String>
) {
    displayDish.value = displayDishDefaultValue
    totalPrice.value = 0
    displayList.clear()
    allowClick.value = true
    bottomButtonText.value = "确认"
}

@Composable
fun ConfirmButton(
    bottomButtonText: MutableState<String>,
    displayList: MutableList<String>,
    aDishMode: MutableState<Boolean>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    context: Context,
    allowClick: MutableState<Boolean>,
    bbClickState: MutableState<Boolean>
) {
    OutlinedButton(
        onClick = {
            execute(displayList, aDishMode, bottomButtonText,
                bbClickState, displayDish, totalPrice, context, allowClick)
        },
        modifier = Modifier
            .padding(0.dp, 20.dp, 0.dp, 0.dp)
            .width(150.dp),
        enabled = bbClickState.value
    ) {
        Text(bottomButtonText.value)
    }
}

fun execute(
    displayList: MutableList<String>,
    aDishMode: MutableState<Boolean>,
    bottomButtonText: MutableState<String>,
    bbClickState: MutableState<Boolean>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    context: Context,
    allowClick: MutableState<Boolean>
) {
    //注意，这里必须用 when，分开用 if 的话，它会连续执行，而 when 就不会，如果找到匹配的，那它执行完就退出了！
    when (bottomButtonText.value) {
        "确认" -> {
            // 在这里执行确认逻辑
            // TODO: 这里将数据传到统计页面
            allowClick.value = false

            if (aDishMode.value) {
                bbClickState.value = false
            } else {
                bottomButtonText.value = "导出"
            }
        }
        "导出" -> {
            // 在这里执行导出逻辑
            val exportText = "${displayDish.value}\n\n小计：${totalPrice.value} 元"
            copyToClipboard(context, exportText)
            Toast.makeText(context, "组合菜品已导出到系统剪贴板", Toast.LENGTH_SHORT).show()
            bbClickState.value = false
            allowClick.value = false

            bottomButtonText.value = "已导出"
            bbClickState.value = false
        }
    }
}

fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}


@Composable
fun UiChangeText(
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = displayDish.value,
            modifier = Modifier
                .padding(0.dp, 35.dp, 0.dp, 35.dp)
                .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                .padding(10.dp)
        )

        Text(
            text = "小计：${totalPrice.value} 元",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TitleText(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun RandomButtonRow(
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    displayList: MutableList<String>,
    allowClick: MutableState<Boolean>,
    aDishMode: MutableState<Boolean>,
    context: Context,
    bbClickState: MutableState<Boolean>
) {
    val expensiveDishList = mutableListOf<Boolean>()

    Row(
        modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp)
    ) {
        Button(
            onClick = {
                bbClickState.value = true
                updateDish(false, displayDish, totalPrice,
                    displayList, context, allowClick, expensiveDishList, aDishMode)
            },
            enabled = allowClick.value
        ) {
            Text(text = "日常随机")
        }

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            onClick = {
                bbClickState.value = true
                updateDish(true, displayDish, totalPrice,
                    displayList, context, allowClick, expensiveDishList, aDishMode)
            },
            enabled = allowClick.value
        ) {
            Text(text = "整点好的")
        }
    }
}



fun updateDish(
    isExpensive: Boolean, //用于判别更新来自哪个按钮（是日常随机？还是整点好的？）
    displayDish: MutableState<String>, //随机生成并展示的菜品（或单个，或多个）
    totalPrice: MutableState<Int>, //小计的金额
    displayList: MutableList<String>,
    context: Context,
    allowClick: MutableState<Boolean>,
    expensiveDishList: MutableList<Boolean>,
    aDishMode: MutableState<Boolean>
) {

    val dishList = if (isExpensive) aMoreExpensiveDishes else regularDishes
    var randomDish = randomDish(dishList)

    if (aDishMode.value) {
        val (name, realPrice) = splitToDish(randomDish)
        displayDish.value = "$name $realPrice 元"
        totalPrice.value = realPrice
        return
    }

    if (isExpensive) {
        expensiveDishList.add(true)
    } else {
        expensiveDishList.add(false)
    }

    while (displayList.contains(randomDish)) {
        randomDish = randomDish(dishList)
    }

    if (displayList.size < 10) {
        displayList.add(randomDish)
        getDisplayValue(displayList, displayDish, totalPrice, expensiveDishList)
    } else {
        allowClick.value = false
        Toast.makeText(context, "都点了 10 道菜了，还吃？",
            Toast.LENGTH_SHORT).show()
    }
}


fun getDisplayValue(
    displayList: MutableList<String>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    expensiveDishList: MutableList<Boolean>
) {
    val builder = StringBuilder()
    var total = 0

    displayList.forEachIndexed { index, s ->
        val (name, realPrice) = splitToDish(s)
        val emoji = if (expensiveDishList[index]) "\uD83D\uDD25" else ""
        builder.append("${index + 1}. $name $realPrice 元$emoji\n\n")
        total += realPrice
    }

    displayDish.value = builder.toString().dropLast(2)
    totalPrice.value = total
}
