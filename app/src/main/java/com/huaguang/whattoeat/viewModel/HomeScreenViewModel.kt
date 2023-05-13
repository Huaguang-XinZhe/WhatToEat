package com.huaguang.whattoeat.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.huaguang.whattoeat.DishRepository
import com.huaguang.whattoeat.aMoreExpensiveDishes
import com.huaguang.whattoeat.data.AppDatabase
import com.huaguang.whattoeat.data.Dish
import com.huaguang.whattoeat.data.DishInfo
import com.huaguang.whattoeat.displayDishDefaultValue
import com.huaguang.whattoeat.regularDishes
import com.huaguang.whattoeat.utils.SPHelper
import com.huaguang.whattoeat.utils.copyToClipboard
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.random.Random

class HomeScreenViewModel(
    private val appDatabase: AppDatabase,
    private val spHelper: SPHelper
) : ViewModel() {
    private val displayList = mutableListOf<String>()

    val displayDish = mutableStateOf(displayDishDefaultValue)
    val totalPrice = mutableStateOf(0)
    val allowClick = mutableStateOf(true)
    val aDishMode = mutableStateOf(true)
    val modeText = mutableStateOf("日常一菜")
    val bottomButtonText = mutableStateOf("确认")
    val bbClickState = mutableStateOf(false)

    // 这里可以添加其他方法，例如 `restart()` 和 `updateDish()`，以处理 HomeScreen 逻辑

    fun restart() {
        displayDish.value = displayDishDefaultValue
        totalPrice.value = 0
        displayList.clear()
        allowClick.value = true
        bottomButtonText.value = "确认"
    }


    fun execute(
        setDishesScreenArgs: (Pair<String, Int>?) -> Unit,
        dishes: List<DishInfo>
    ): ((context: Context) -> Unit)? {
        Log.i("吃什么？", "execute 里面执行！！！")
        //注意，这里必须用 when，分开用 if 的话，它会连续执行，而 when 就不会，如果找到匹配的，那它执行完就退出了！
        when (bottomButtonText.value) {
            "确认" -> {
                Log.i("吃什么？", "when 确认块执行")
                // 在这里执行确认逻辑
                Log.i("吃什么？", "dishes = $dishes")
                val totalExpense = getTotalExpense()
                Log.i("吃什么？", "totalExpense = $totalExpense")
                // 只传参不导航
                val dishesJson = Json.encodeToString(dishes)
                setDishesScreenArgs(dishesJson to totalExpense) // 更新参数值

                allowClick.value = false

                if (aDishMode.value) {
                    bbClickState.value = false
                } else {
                    bottomButtonText.value = "导出"
                }

                //把新的总消费金额存起来
                spHelper.totalExpense = totalExpense
            }
            "导出" -> {
                // 在这里执行导出逻辑
                val exportText = "${displayDish.value}\n\n小计：${totalPrice.value} 元"

                bbClickState.value = false
                allowClick.value = false

                bottomButtonText.value = "已导出"
                bbClickState.value = false

                // 返回一个函数，该函数需要在 Composable 函数中调用
                return { context: Context ->
                    copyToClipboard(context, exportText)
                    Toast.makeText(context, "组合菜品已导出到系统剪贴板", Toast.LENGTH_SHORT).show()
                }
            }
        }

        //这里还非写个 return 不可，不写会报错！
        return null
    }


    fun updateDish(
        isExpensive: Boolean, //用于判别更新来自哪个按钮（是日常随机？还是整点好的？）
        expensiveDishList: MutableList<Boolean>,
    ): ((context: Context) -> Unit)? {

        val dishList = if (isExpensive) aMoreExpensiveDishes else regularDishes
        var randomDish = randomDish(dishList)

        if (aDishMode.value) {
            val (name, realPrice) = splitToDish(randomDish)
            displayDish.value = "$name $realPrice 元"
            totalPrice.value = realPrice

            // 即使是日常一菜也要加入 displayList
            displayList.add(randomDish)
            return null
        }

        if (isExpensive) {
            expensiveDishList.add(true)
        } else {
            expensiveDishList.add(false)
        }

        while (displayList.contains(randomDish)) {
            randomDish = randomDish(dishList)
        }

        if (displayList.size < 10) {
            displayList.add(randomDish)
            getDisplayValue(expensiveDishList)
        } else {
            allowClick.value = false
            return { context ->
                Toast.makeText(context, "都点了 10 道菜了，还吃？",
                    Toast.LENGTH_SHORT).show()
            }
        }

        return null
    }

    fun getDishes(): LiveData<List<DishInfo>> {
        val mediatorLiveData = MediatorLiveData<List<DishInfo>>()
        val list = mutableListOf<DishInfo>()

        fun String.addIn() {
            val name = this.split(" ")[0]
            val dishRepository = DishRepository(appDatabase)
            val eatenTimesLiveData: LiveData<Int> = dishRepository.getEatenTimesForDish(name)

            mediatorLiveData.addSource(eatenTimesLiveData) { eatenTimes ->
                list.add(DishInfo(name, eatenTimes))
                Log.i("吃什么？", "list 前 = $list")

                if (list.size == displayList.size) {
                    Log.i("吃什么？", "if 块执行！")
                    mediatorLiveData.value = list
                }
            }
        }

        if (aDishMode.value) {
            if (displayList.isNotEmpty()) {
                val dish = displayList.last()
                dish.addIn()
            }
        } else {
            Log.i("吃什么？", "displayList = $displayList")
            displayList.forEach { randomDish ->
                randomDish.addIn()
            }
            Log.i("吃什么？", "list = $list")
        }

        return mediatorLiveData
    }


    private fun getDisplayValue(expensiveDishList: MutableList<Boolean>) {
        val builder = StringBuilder()
        var total = 0

        displayList.forEachIndexed { index, s ->
            val (name, realPrice) = splitToDish(s)
            val emoji = if (expensiveDishList[index]) "\uD83D\uDD25" else ""
            builder.append("${index + 1}. $name $realPrice 元$emoji\n\n")
            total += realPrice
        }

        displayDish.value = builder.toString().dropLast(2)
        totalPrice.value = total
    }

    private fun getTotalExpense(): Int {
        var totalExpense = spHelper.totalExpense
        totalExpense += totalPrice.value

        return totalExpense
    }

    /**
     * 根据菜谱返回一个随机菜肴（未分列）
     * 注意：这里的价格还没有进行处理（未减 1）
     */
    private fun randomDish(dishes: List<String>): String {
        val randomIndex = Random.nextInt(0, dishes.size)
        return dishes[randomIndex]
    }

    private fun splitToDish(randomDish: String): Dish {
        val (name, price) = randomDish.split(" ")
        return Dish(name, price.toInt() - 1)
    }

}
