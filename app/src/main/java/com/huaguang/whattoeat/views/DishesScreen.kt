package com.huaguang.whattoeat.views


import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.huaguang.whattoeat.data.DishInfo
import com.huaguang.whattoeat.viewModel.DishesScreenViewModel

@Composable
fun DishesScreen(
    viewModel: DishesScreenViewModel,
    highlightDishes: List<DishInfo>,
    totalExpense: Int
) {
    LaunchedEffect(Unit) {
        //在 Compose 的生命周期里只会执行一次，所以在后续页面访问时不会重新加载数据。
        viewModel.loadMenuData()
    }

    viewModel.highlightDishes.value = highlightDishes
    viewModel.totalExpense = totalExpense

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (summaryRef, dishListRef) = createRefs()

        Summary(
            viewModel = viewModel,
            modifier = Modifier.constrainAs(summaryRef) {
                start.linkTo(parent.start, 20.dp)
                top.linkTo(parent.top, 55.dp)
            }
        )

        DishList(
            viewModel = viewModel,
            modifier = Modifier.constrainAs(dishListRef) {
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
                bottom.linkTo(parent.bottom)
                height = Dimension.wrapContent
            }
        )
    }
}

@Composable
fun DishList(
    viewModel: DishesScreenViewModel,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(viewModel.dishes.value) { dish ->
            DishRow(viewModel, dish)
        }
    }
}

@Composable
fun DishRow(
    viewModel: DishesScreenViewModel,
    dish: DishInfo
) {
    Row(
        modifier = Modifier.padding(horizontal = 35.dp, vertical = 15.dp)
    ) {
        Text(
            text = dish.name,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge
        )

        if (dish.eatenTimes > 0) {
            if (dish.eatenTimes == 1) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Eaten",
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.CenterVertically),
                    tint = Color.Green
                )
            } else {
                Text(
                    text = dish.eatenTimes.toString(),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        IconButton(
            onClick = { viewModel.onDeleteItem(dish) }
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier.padding(horizontal = 15.dp)
            )
        }
    }
}

@Composable
fun Summary(
    viewModel: DishesScreenViewModel,
    modifier: Modifier
) {
    Log.i("吃什么？", "Summary 执行！！！")
    Row(
        modifier = modifier
    ) {
        Text("完吃率: ${viewModel.finishedRate.value}%")

        Spacer(modifier = Modifier.width(25.dp))

        Text("总消费: ${viewModel.totalExpense}")
    }
}
