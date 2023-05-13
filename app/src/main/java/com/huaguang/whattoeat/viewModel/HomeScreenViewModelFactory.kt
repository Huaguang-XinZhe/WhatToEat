package com.huaguang.whattoeat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.utils.SPHelper

class HomeScreenViewModelFactory(
    private val appDatabase: AppDatabase,
    private val spHelper: SPHelper
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeScreenViewModel::class.java)) {
            return HomeScreenViewModel(appDatabase, spHelper) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
