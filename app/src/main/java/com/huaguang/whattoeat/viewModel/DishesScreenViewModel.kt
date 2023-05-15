package com.huaguang.whattoeat.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.data.DishInfo
import com.huaguang.whattoeat.data.getMenuData
import kotlinx.coroutines.launch

class DishesScreenViewModel(private val appDatabase: AppDatabase) : ViewModel() {

    private val dishInfoDao = appDatabase.dishInfoDao()

    private val _menuData = MutableLiveData<List<DishInfo>>()
    val menuData: LiveData<List<DishInfo>>
        get() = _menuData

    init {
        loadMenuData()
    }

    private fun loadMenuData() = viewModelScope.launch {
        Log.i("吃什么？", "loadMenuData 执行！！！从数据库中查数据")
        val dishesFromDb = dishInfoDao.getAllDishInfo()
        if (dishesFromDb.isNotEmpty()) {
            _menuData.value = dishesFromDb
        } else {
            Log.i("吃什么？", "数据库中没查到数据，便插入！！！")
            val defaultDishes = getMenuData()
            _menuData.value = defaultDishes
            defaultDishes.forEach { dishInfo ->
                dishInfoDao.insertDishInfo(dishInfo)
            }
        }
    }
}
