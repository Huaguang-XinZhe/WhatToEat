package com.huaguang.whattoeat

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue

/**
 * 这些组件依然不完善，还存在滑动牵连删除，删除不尽等问题。
 * 暂且用官方库，待之后学完相关知识后再研究。
 */

@Composable
fun SwipeToDeleteItem(
    item: String,
    onItemDeleted: () -> Unit
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java) as Vibrator

    var offsetX by remember { mutableStateOf(0f) }
    val threshold = 300f
    var hasVibrated by remember { mutableStateOf(false) }
    val animatedOffsetX by animateDpAsState(targetValue = offsetX.dp)

    fun vibrate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(200)
        }
    }

    fun handleDrag(dragAmount: Offset) {
        offsetX += dragAmount.x

        if (!hasVibrated) {
            vibrate()
            hasVibrated = true
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(x = animatedOffsetX)
            .background(Color.White)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {  },
                    onDrag = { change, dragAmount ->
                        if (change.positionChange() != Offset.Zero) change.consume()
                        handleDrag(dragAmount)
                    },
                    onDragEnd = {
                        if (offsetX.absoluteValue > threshold) {
                            onItemDeleted()
                        } else {
                            offsetX = 0f
                        }
                        hasVibrated = false
                    }
                )
            }
    ) {
        // 在这里添加列表项的内容
        Text(text = item, Modifier.padding(16.dp))
    }
}

@Composable
fun SwipeToDeleteScreen() {
    val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        items(items) { item ->
            SwipeToDeleteItem(
                item = item,
                onItemDeleted = {
                    items.remove(item)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SwipeExample() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        // 创建一个列表
//        val items = listOf("Item 1", "Item 2", "Item 3")
        // 使用 SnapShotStateList 才能使用 remove() 函数将元素移除
        val items = remember { mutableStateListOf("Item 1", "Item 2", "Item 3") }

        items.forEachIndexed { index, item ->
            SwipeItem(
                key = index, // 为每个 Item 分配一个唯一的键
                item = item,
                onItemDeleted  = {
                    items.remove(item)
                }
            )
        }
    }
}

@Composable
fun SwipeItem(
    key: Int,
    item: String,
    onItemDeleted: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val threshold = 300f
    val animatedOffsetX by animateDpAsState(targetValue = offsetX.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .offset(x = animatedOffsetX)
            .background(Color.LightGray)
            .pointerInput(key) { // 使用分配的唯一键
                detectDragGestures(
                    onDragStart = { },
                    onDrag = { change, dragAmount ->
                        if (change.positionChange() != Offset.Zero) change.consume()
                        offsetX += dragAmount.x
                    },
                    onDragEnd = {
                        if (offsetX.absoluteValue > threshold) {
                            onItemDeleted()
                        } else {
                            offsetX = 0f
                        }
                    }
                )
            }
    ) {
        Text(text = item, Modifier.padding(16.dp))
    }
}

