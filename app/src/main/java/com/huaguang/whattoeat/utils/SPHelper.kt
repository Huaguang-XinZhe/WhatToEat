package com.huaguang.whattoeat.utils

import android.content.Context
import android.content.SharedPreferences
import java.util.Date

class SPHelper(context: Context) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("WhatToEatPrefs", Context.MODE_PRIVATE)

    var totalExpense: Int
        get() = sharedPreferences.getInt("KEY_TOTAL_EXPENSE", 0)
        set(value) = sharedPreferences.edit().putInt("KEY_TOTAL_EXPENSE", value).apply()

    var isCodeExecuted: Boolean
        get() = sharedPreferences.getBoolean("KEY_CODE_EXECUTED", false)
        set(value) = sharedPreferences.edit().putBoolean("KEY_CODE_EXECUTED", value).apply()

    var lastExecutedDate: Long
        get() = sharedPreferences.getLong("KEY_LAST_EXECUTED_DATE", 0L)
        set(value) = sharedPreferences.edit().putLong("KEY_LAST_EXECUTED_DATE", value).apply()

    fun isOneDayPassed(): Boolean {
        val currentDate = Date().time
        val lastDate = lastExecutedDate
        val oneDayInMillis = 24 * 60 * 60 * 1000
        return (currentDate - lastDate) >= oneDayInMillis
    }
}
