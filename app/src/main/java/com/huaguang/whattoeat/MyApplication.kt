package com.huaguang.whattoeat

import android.app.Application
import androidx.room.Room
import com.huaguang.whattoeat.data.AppDatabase

class MyApplication : Application() {
    lateinit var appDatabase: AppDatabase

    override fun onCreate() {
        super.onCreate()
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "dish_database"
        ).build()
    }
}
