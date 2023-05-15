package com.huaguang.whattoeat.data

import com.huaguang.whattoeat.aMoreExpensiveDishes
import com.huaguang.whattoeat.regularDishes

fun getMenuData(): List<DishInfo> {
    val regularDishesInfo = regularDishes.map { dish ->
        val dishName = dish.substringBefore(' ')
        DishInfo(dishName, 0)
    }

    val aMoreExpensiveDishesInfo = aMoreExpensiveDishes.map { dish ->
        val dishName = dish.substringBefore(' ')
        DishInfo(dishName, 0)
    }

    return regularDishesInfo + aMoreExpensiveDishesInfo
}