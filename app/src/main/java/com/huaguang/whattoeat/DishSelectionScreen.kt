package com.huaguang.whattoeat

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.util.concurrent.atomic.AtomicInteger
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

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TitleText("吃什么？")
        UiChangeText(displayDish, totalPrice)
        RandomButtonRow(displayDish, totalPrice, displayList, allowClick)
    }
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
    allowClick: MutableState<Boolean>
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.padding(0.dp, 25.dp, 0.dp, 0.dp)
    ) {
        Button(
            onClick = {
                updateDish(false, displayDish,
                    totalPrice, displayList, context, allowClick)
            },
            enabled = allowClick.value
        ) {
            Text(text = "日常随机")
        }

        Spacer(modifier = Modifier.width(20.dp))

        Button(
            onClick = {
                updateDish(true, displayDish,
                    totalPrice, displayList, context, allowClick)
            },
            enabled = allowClick.value
        ) {
            Text(text = "整点好的")
        }
    }
}

fun updateDish(
    isExpensive: Boolean,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    displayList: MutableList<String>,
    context: Context,
    allowClick: MutableState<Boolean>
) {
    val dishList = if (isExpensive) aMoreExpensiveDishes else regularDishes
    var randomDish = randomDish(dishList)

    while (displayList.contains(randomDish)) {
        randomDish = randomDish(dishList)
    }

    if (displayList.size < 10) {
        displayList.add(randomDish)
        getDisplayValue(displayList, displayDish, totalPrice)
    } else {
        allowClick.value = false
        Toast.makeText(context, "都点了 10 道菜了，还吃？",
            Toast.LENGTH_SHORT).show()
    }
}

fun getDisplayValue(
    displayList: MutableList<String>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>
) {
    val total = AtomicInteger(0)
    val formattedList = displayList.mapIndexed { index, s ->
        val (name, realPrice) = splitToDish(s)
        total.addAndGet(realPrice)
        "${index + 1}. $name $realPrice 元"
    }.joinToString(separator = "\n\n")

    displayDish.value = formattedList
    totalPrice.value = total.get()
}


//@Composable
//fun ExpensiveDishButtonRow(
//    clickNum: MutableState<Int>,
//    displayDish: MutableState<String>,
//    totalPrice: MutableState<Int>,
//    displayList: MutableList<String>
//) {
//    Row {
//        IconButton(
//            onClick = { restart(clickNum, displayDish, totalPrice, displayList) },
//            modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp),
//            enabled = clickNum.value > 0
//        ) {
//            Icon(
//                painter = painterResource(R.drawable.start_over),
//                contentDescription = null,
//                modifier = Modifier.size(24.dp),
//                tint = Color(1, 148, 7, 255)
//            )
//        }
//
//    }
//}

//private fun restart(
//    clickNum: MutableState<Int>,
//    displayDish: MutableState<String>,
//    totalPrice: MutableState<Int>,
//    displayList: MutableList<String>
//) {
//    clickNum.value = clickNumDefaultValue
//    displayDish.value = displayDishDefaultValue
//    totalPrice.value = 0
//    displayList.clear()
//}
