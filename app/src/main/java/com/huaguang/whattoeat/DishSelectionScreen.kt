package com.huaguang.whattoeat

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
const val clickNumDefaultValue = 0

/**
 * 根据菜谱返回一个随机菜肴，包括菜名和价格。
 * 注意！这里的价格没有 -1
 */
fun randomDish(dishes: List<String>): Dish {
    val randomIndex = Random.nextInt(0, dishes.size)
    val (name, price) = dishes[randomIndex].split(" ")
    return Dish(name, price.toInt() - 1)
}


@Preview(showBackground = true)
@Composable
fun WhatToEat() {
    val displayDish = remember { mutableStateOf(displayDishDefaultValue) }
    val clickNum = remember { mutableStateOf(clickNumDefaultValue) }
    val totalPrice = remember { mutableStateOf(0) }
    val checkList = mutableListOf<String>()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TitleText("吃什么？")
        DishText(displayDish)
        RegularDishButton(displayDish, totalPrice)
        ExpensiveDishButtonRow(
            clickNum, displayDish, totalPrice, checkList
        )
    }
}

@Composable
fun TitleText(title: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge)
}

@Composable
fun DishText(displayDish: MutableState<String>) {
    Text(
        text = displayDish.value,
        modifier = Modifier
            .padding(0.dp, 35.dp, 0.dp, 35.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
            .padding(10.dp)
    )
}

@Composable
fun RegularDishButton(
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>
) {
    Button(
        onClick = {
            updateRegularDish(displayDish, totalPrice)
        }
    ) {
        Text(text = "日常随机")
    }
}

@Composable
fun ExpensiveDishButtonRow(
    clickNum: MutableState<Int>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    checkList: MutableList<String>
) {
    Row {
        IconButton(
            onClick = { restart(clickNum, displayDish, totalPrice, checkList) },
            modifier = Modifier.padding(0.dp, 0.dp, 10.dp, 0.dp),
            enabled = clickNum.value > 0
        ) {
            Icon(
                painter = painterResource(R.drawable.start_over),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color(1, 148, 7, 255)
            )
        }

        Button(
            onClick = {
                updateExpensiveDish(clickNum, displayDish, totalPrice, checkList)
            },
            enabled = clickNum.value < 10
        ) {
            Text(text = "整点好的")
        }

        Text(
            text = "小计：${totalPrice.value} 元",
            modifier = Modifier
                .padding(20.dp, 0.dp, 0.dp, 0.dp)
                .align(Alignment.CenterVertically)
        )
    }
}

private fun restart(
    clickNum: MutableState<Int>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    checkList: MutableList<String>
) {
    clickNum.value = clickNumDefaultValue
    displayDish.value = displayDishDefaultValue
    totalPrice.value = 0
    checkList.clear()
}


private fun updateRegularDish(
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>
) {
    val (name, realPrice) = randomDish(regularDishes)
    displayDish.value = "$name $realPrice 元"
    totalPrice.value = realPrice
}

private fun updateExpensiveDish(
    clickNum: MutableState<Int>,
    displayDish: MutableState<String>,
    totalPrice: MutableState<Int>,
    checkList: MutableList<String>
) {
    clickNum.value++
    var (name, realPrice) = randomDish(aMoreExpensiveDishes)

    //第一次，直接加入查重列表中，不判断
    if(clickNum.value > 1) {
        while (name in checkList) {
            //以下代码是错误的，在 Kotlin 中，解构声明不能直接用于更新已有变量。
//            (name, realPrice) = randomDish(aMoreExpensiveDishes)
            //但可以使用解构声明为新变量赋值，然后将新变量的值赋给已有变量。
            // 更新 name 和 realPrice 变量的值
            val (newName, newRealPrice) = randomDish(aMoreExpensiveDishes)
            name = newName
            realPrice = newRealPrice
        }
    }
    checkList.add(name)
    Log.i("整点好的点击", "checkList = $checkList")

    val dish = "${clickNum.value}. $name $realPrice 元"
    //displayDish 可能含有日常随机菜肴，为避免混排，在第一次点击整点好的时必须将 displayDish 的值清空
    displayDish.value = if (clickNum.value == 1) dish else {
        displayDish.value + "\n\n$dish"
    }

    //第一次点击，totalPrice 可能含有日常随机菜肴的值，必须先清除，但第二次以上就不能。
    if (clickNum.value == 1) totalPrice.value = 0
    totalPrice.value += realPrice
}