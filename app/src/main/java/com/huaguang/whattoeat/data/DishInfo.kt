package com.huaguang.whattoeat.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "dish_info")
data class DishInfo(
    @PrimaryKey
    val name: String,
    var eatenTimes: Int
)
