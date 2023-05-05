package com.huaguang.whattoeat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 设置沉浸式状态栏，让应用内容延伸到状态栏区域
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //以上方法比这个方法要好，它实现了兼容（下面的方法只能用于 API 30 以上的手机）
//        window.setDecorFitsSystemWindows(false)

        val spHelper = SPHelper(this)

        setContent {
            AppContent()
        }

    }
}

@Composable
fun AppContent() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true

    SideEffect {
        // 设置状态栏图标和文字颜色
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, // 状态栏背景颜色
            darkIcons = useDarkIcons // 是否使用深色图标和文字
        )
    }

    // 你的其他 UI 代码...
    WhatToEat()
}