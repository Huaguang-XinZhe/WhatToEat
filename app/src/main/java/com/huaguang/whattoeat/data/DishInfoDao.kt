package com.huaguang.whattoeat.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query

@Dao
interface DishInfoDao {
    @Query("SELECT * FROM dishes WHERE name = :dishName")
    fun getDishInfoByName(dishName: String): LiveData<DishInfo?>
}
