package com.huaguang.whattoeat.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map

class DishRepository(private val appDatabase: AppDatabase) {

    private val dishInfoDao = appDatabase.dishInfoDao()

    fun getEatenTimesForDish(dishName: String): LiveData<Int> {
        return dishInfoDao.getDishInfoByName(dishName).map { dishInfo ->
            dishInfo?.eatenTimes ?: 0
        }
    }

    // 其他方法...
}
