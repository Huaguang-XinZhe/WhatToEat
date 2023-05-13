package com.huaguang.whattoeat.data

// AppDatabase.kt

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DishInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dishInfoDao(): DishInfoDao
}
