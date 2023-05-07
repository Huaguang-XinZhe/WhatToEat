package com.huaguang.whattoeat

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.List
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    private var doubleBackToExitPressedOnce = false

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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            finish()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "再按一次返回键退出", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }

//    private fun isOnMainScreen(navController: NavController): Boolean {
//        val currentRoute = navController.currentBackStackEntry?.destination?.route
//        return currentRoute == Screen.HomeScreen.route || currentRoute == Screen.DishesScreen.route
//    }

}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContent() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = true
    val selectedState = remember { mutableStateOf(0) }
    val iconList = listOf(
        IconInfo(Icons.Rounded.Home, "主页"),
        IconInfo(Icons.Rounded.List, "菜单")
    )

    val navController = rememberNavController()
    val dishesScreenArgs = remember { mutableStateOf<Pair<String, Int>?>(null) }
    val setDishesScreenArgs: (Pair<String, Int>?) -> Unit = { newValue ->
        dishesScreenArgs.value = newValue
    }

    SideEffect {
        // 设置状态栏图标和文字颜色
        systemUiController.setSystemBarsColor(
            color = Color.Transparent, // 状态栏背景颜色
            darkIcons = useDarkIcons // 是否使用深色图标和文字
        )
    }

    // 你的其他 UI 代码...
    Scaffold(
        bottomBar = {
            NavigationBar {
                iconList.forEachIndexed { index, iconInfo ->
                    NavigationBarItem(
                        selected = index == selectedState.value,
                        onClick = {
                            selectedState.value = index

                            val route = if (index == 1) {
                                if (dishesScreenArgs.value != null) {
                                    val (dishes, totalExpense) = dishesScreenArgs.value!!
                                    Screen.DishesScreen.createRoute(dishes, totalExpense)
                                } else {
                                    Screen.DishesScreen.createRoute()// 使用默认值
                                }
                            } else {
                                Screen.HomeScreen.route
                            }
                            navController.navigate(route) {
                                //这里可以添加动画

                                launchSingleTop = true
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = iconInfo.imageVector,
                                contentDescription = null
                            )
                        }
                    )


                }
            }
        }
    ) {
        //注意，如果有 Scaffold 存在的话，NavHost 必须放在其中，否则跳转页不会显示
        NavHost(navController, startDestination = Screen.HomeScreen.route) {
            composable(Screen.HomeScreen.route) {
                HomeScreen(dishesScreenArgs, setDishesScreenArgs)

                selectedState.value = 0
            }
            composable(
                route = Screen.DishesScreen.route,
                arguments = listOf(
                    navArgument("dishes") { type = NavType.StringType },
                    navArgument("totalExpense") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val dishesJson = backStackEntry.arguments?.getString("dishes") ?: ""
                val dishes = dishesJson.let { Json.decodeFromString<List<DishInfo>>(it) }
                val totalExpense = backStackEntry.arguments?.getInt("totalExpense") ?: 0

                DishesScreen(navController, dishes, totalExpense)

                selectedState.value = 1
            }
        }
    }

}



data class IconInfo(val imageVector: ImageVector, val label: String)


sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object DishesScreen : Screen("screen_b/{dishes}/{totalExpense}") {
        fun createRoute(
            dishesJson: String = "[]",
            totalExpense: Int = 0
        ) = "screen_b/$dishesJson/$totalExpense"
    }
}



@Composable
fun DishesScreen2(navController: NavController, dishes: List<DishInfo>, totalExpense: Int) {
    Log.i("吃什么？", "DishesScreen2 执行了。。。")
    Column {
        Text("Dishes:")
        for (dish in dishes) {
            Text("${dish.name} (${dish.eatenTimes} times)")
        }
        Text("Total Expense: $totalExpense")

        Button(onClick = { navController.popBackStack() }) {
            Text("Go back")
        }
    }
}
