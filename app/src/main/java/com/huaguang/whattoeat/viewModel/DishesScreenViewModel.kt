package com.huaguang.whattoeat.viewModel

import android.util.Log
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.data.DishInfo
import com.huaguang.whattoeat.data.getMenuData
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

    fun onDeleteItem(dish: DishInfo) {
        Log.i("吃什么？", "删除按钮点击了！！！")
        val newMenuData = menuData.value.toMutableList()
        newMenuData.remove(dish)
        menuData.value = newMenuData
        // TODO: 判断 SnackBar 点没点撤销，没点就删除数据库和源菜品列表中的数据
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

    private fun calFinishedRate(): Int {
        Log.i("吃什么？", "计算完吃率！！！")
        val currentDishesCount = dishes.value.count()
        val totalEaten = dishes.value.count { it.eatenTimes > 0 }
        val finishedRate = if (currentDishesCount > 0)
            totalEaten / currentDishesCount.toFloat() * 100 else 0f

        return finishedRate.toInt()
    }
}

