package com.huaguang.whattoeat

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.huaguang.whattoeat.data.AppDatabase

class DishRepository(private val appDatabase: AppDatabase) {

    private val dishInfoDao = appDatabase.dishInfoDao()

    fun getEatenTimesForDish(dishName: String): LiveData<Int> {
        return dishInfoDao.getDishInfoByName(dishName).map { dishInfo ->
            dishInfo?.eatenTimes ?: 0
        }
    }

    // 其他方法...
}
