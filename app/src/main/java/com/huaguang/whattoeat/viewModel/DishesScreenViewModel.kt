package com.huaguang.whattoeat.viewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.data.DishInfo
import com.huaguang.whattoeat.data.getMenuData
import kotlinx.coroutines.launch

class DishesScreenViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val dishInfoDao = appDatabase.dishInfoDao()

    private val menuData = MutableLiveData<List<DishInfo>>()
    val highlightDishes = MutableLiveData<List<DishInfo>>(emptyList())
    var totalExpense: Int = 0

    init {
        loadMenuData()
    }

    fun getUpdateDishes(): List<DishInfo> {
        val updateDishes = menuData.value!!.map { dish ->
            val highlightDish = highlightDishes.value!!.find { it.name == dish.name }
            if (highlightDish != null) {
                DishInfo(dish.name, highlightDish.eatenTimes)
            } else {
                dish
            }
        }

        //注意，这里一定要更新菜单数据！！！
        menuData.value = updateDishes

        return updateDishes
    }

    fun calFinishedRate(): Int {
        val currentDishesCount = menuData.value!!.count()
        val totalEaten = menuData.value!!.count { it.eatenTimes > 0 }
        val finishedRate = if (currentDishesCount > 0)
            totalEaten / currentDishesCount.toFloat() * 100 else 0f

        return finishedRate.toInt()
    }

    private fun loadMenuData() = viewModelScope.launch {
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
    }
}
