package com.huaguang.whattoeat.data

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