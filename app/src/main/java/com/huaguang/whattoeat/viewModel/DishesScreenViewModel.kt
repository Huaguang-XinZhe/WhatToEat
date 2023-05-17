package com.huaguang.whattoeat.viewModel

import android.util.Log
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.data.DeleteItem
import com.huaguang.whattoeat.data.DishInfo
import com.huaguang.whattoeat.data.aMoreExpensiveDishes
import com.huaguang.whattoeat.data.getMenuData
import com.huaguang.whattoeat.data.regularDishes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class DishesScreenViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val dishInfoDao = appDatabase.dishInfoDao()
    private var dataLoaded = false
    private val menuData = mutableStateOf<List<DishInfo>>(emptyList())
    val highlightDishes = mutableStateOf<List<DishInfo>>(emptyList())
    var totalExpense: Int = 0
    //使用派生状态自动更新，依赖 menuData、highlightDishes
    val dishes = derivedStateOf {
        val menu = menuData.value
        val highlight = highlightDishes.value
        menu.map { dish ->
            val highlightDish = highlight.find { it.name == dish.name }
            if (highlightDish != null) {
                DishInfo(dish.name, highlightDish.eatenTimes)
            } else {
                dish
            }
        }
    }
    //内部依赖 dishes
    val finishedRate = derivedStateOf { calFinishedRate() }
    val snackBarHostState = SnackbarHostState()
    lateinit var deleteItem: DeleteItem
    val snackBarDismissed = MutableStateFlow(false)
    private val undoClicked = MutableStateFlow(false)

    fun onDeleteItem(dish: DishInfo) {
        Log.i("吃什么？", "删除按钮点击了！！！")
        dish.deleteFromUI()
        // TODO: 判断 SnackBar 点没点撤销，没点就删除数据库和源菜品列表中的数据

        //显示 SnackBar
        viewModelScope.launch {
            // 必须开启新的协程，要不然会阻塞 showSnackbar 的执行
            launch {
                snackBarDismissed.collect { dismissed ->
                    if (dismissed && !undoClicked.value) {
                        // SnackBar 已消失，执行删除操作
                        deleteItem.dish.deleteFromDB()
                        deleteItem.dish.deleteFromSL()
                    }
                    // 重置 snackBarDismissed 和 undoClicked 的状态
                    snackBarDismissed.value = false
                    undoClicked.value = false
                }
            }

            snackBarHostState.showSnackbar(
                message = "条目已删除",
                actionLabel = "撤销",
                duration = SnackbarDuration.Short
            )
        }
    }


    private suspend fun DishInfo.deleteFromDB() {
        // 使用你的 Dao 删除 dish
        dishInfoDao.deleteDishInfo(this)
    }

    // 缓存方案，避免每次都从数据库获取数据
    private fun DishInfo.deleteFromSL() {
        // 判断菜品是在那个列表中，再筛选
        val isInRegular = regularDishes.any {
            it.substringBefore(' ') == this.name
        }

        if (isInRegular) {
            regularDishes = regularDishes.filter {
                it.substringBefore(' ') != this.name
            }
        } else {
            Log.i("吃什么？", "是贵的菜！")
            aMoreExpensiveDishes = aMoreExpensiveDishes.filter {
                it.substringBefore(' ') != this.name
            }
            Log.i("吃什么？", "更新后的 aMoreExpensiveDishes = $aMoreExpensiveDishes")
        }
    }

    fun restoreItem(insertIndex: Int, dish: DishInfo) {
        val newMenuData = menuData.value.toMutableList()
        newMenuData.add(insertIndex, dish)
        menuData.value = newMenuData
        undoClicked.value = true
    }

    fun loadMenuData() = viewModelScope.launch {
        if (!dataLoaded) {
            Log.i("吃什么？", "loadMenuData 执行！！！从数据库中查数据")
            val dishesFromDb = dishInfoDao.getAllDishInfo()
            if (dishesFromDb.isNotEmpty()) {
                menuData.value = dishesFromDb
            } else {
                Log.i("吃什么？", "数据库中没查到数据，便插入！！！")
                val defaultDishes = getMenuData()
                menuData.value = defaultDishes
                defaultDishes.forEach { dishInfo ->
                    dishInfoDao.insertDishInfo(dishInfo)
                }
            }

            dataLoaded = true
        }
    }

    private fun DishInfo.deleteFromUI() {
        val newMenuData = menuData.value.toMutableList()
        val index = newMenuData.indexOf(this)
        deleteItem = DeleteItem(index, this)
        newMenuData.remove(this)
        menuData.value = newMenuData
    }

    private fun calFinishedRate(): Int {
        Log.i("吃什么？", "计算完吃率！！！")
        val currentDishesCount = dishes.value.count()
        val totalEaten = dishes.value.count { it.eatenTimes > 0 }
        val finishedRate = if (currentDishesCount > 0)
            totalEaten / currentDishesCount.toFloat() * 100 else 0f

        return finishedRate.toInt()
    }
}

