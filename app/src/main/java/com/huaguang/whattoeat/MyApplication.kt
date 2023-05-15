package com.huaguang.whattoeat

import android.app.Application
import androidx.room.Room
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.viewModel.DishesScreenViewModel

class MyApplication : Application() {
    lateinit var appDatabase: AppDatabase
        private set

    lateinit var dishesScreenViewModel: DishesScreenViewModel
        private set

    override fun onCreate() {
        super.onCreate()
        appDatabase = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "dish_database"
        ).build()

        dishesScreenViewModel = DishesScreenViewModel(appDatabase)
    }
}

