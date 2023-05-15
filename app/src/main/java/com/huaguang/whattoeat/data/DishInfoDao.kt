package com.huaguang.whattoeat.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DishInfoDao {
//    @Query("SELECT * FROM dish_info WHERE name = :dishName")
//    fun getDishInfoByName(dishName: String): LiveData<DishInfo?>

    @Query("SELECT * FROM dish_info WHERE name = :dishName")
    suspend fun getDishInfoByName(dishName: String): DishInfo?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDishInfo(dishInfo: DishInfo): Int

    @Insert
    suspend fun insertDishInfo(dishInfo: DishInfo): Long

    @Query("SELECT * FROM dish_info")
    suspend fun getAllDishInfo(): List<DishInfo>

}
