package com.huaguang.whattoeat.screens


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.huaguang.whattoeat.data.DishInfo

@Composable
fun DishesScreen(
    navController: NavController,
    dishes: List<DishInfo>,
    totalExpense: Int
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (summaryRef, dishListRef) = createRefs()

        Summary(
            dishes = dishes,
            totalExpense = totalExpense,
            modifier = Modifier.constrainAs(summaryRef) {
                start.linkTo(parent.start, 20.dp)
                top.linkTo(parent.top, 55.dp)
            }
        )

        DishList(
            dishes = dishes,
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
    dishes: List<DishInfo>,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
        items(dishes) {dish -> 
            DishRow(dish)
        }
    }
}

@Composable
fun DishRow(dish: DishInfo) {
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
            onClick = { /**/ }
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
    modifier: Modifier,
    dishes: List<DishInfo>,
    totalExpense: Int
) {
    val currentDishesCount = dishes.count()
    val totalEaten = dishes.count { it.eatenTimes > 0 }
    val finishedRate = if (currentDishesCount > 0)
        totalEaten / currentDishesCount.toFloat() * 100 else 0f

    Row(
        modifier = modifier
    ) {
        Text("完吃率: ${finishedRate.toInt()}%")

        Spacer(modifier = Modifier.width(25.dp))

        Text("总消费: $totalExpense")
    }
}
