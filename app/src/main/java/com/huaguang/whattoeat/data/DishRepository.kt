package com.huaguang.whattoeat.data

class DishRepository(private val appDatabase: AppDatabase) {

    private val dishInfoDao = appDatabase.dishInfoDao()
    suspend fun updateEatenTimesForDish(dishName: String): Int {
        val currentDishInfo = dishInfoDao.getDishInfoByName(dishName)
        return if (currentDishInfo != null) {
            val updatedEatenTimes = currentDishInfo.eatenTimes + 1
            //更新数据库中的值，这里只修改了 eatenTimes
            dishInfoDao.updateDishInfo(currentDishInfo.copy(eatenTimes = updatedEatenTimes))
            updatedEatenTimes
        } else {
            dishInfoDao.insertDishInfo(DishInfo(name = dishName, eatenTimes = 1))
            1
        }
    }

}
