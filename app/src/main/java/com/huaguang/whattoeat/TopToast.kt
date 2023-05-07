package com.huaguang.whattoeat

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 下面的代码能实现基本功能，但样式不好看，待日后修改。
 */

@Composable
fun TopToast(
    message: String,
    showToast: MutableState<Boolean>,
    durationMillis: Long = 3000L,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(showToast.value) {
        if (showToast.value) {
            coroutineScope.launch {
                delay(durationMillis)
                onDismiss()
            }
        }
    }

    if (showToast.value) {
        val enterTransition = remember { slideInVertically(initialOffsetY = { -it }) }
        val exitTransition = remember { slideOutVertically(targetOffsetY = { -it }) }

        AnimatedVisibility(
            visible = showToast.value,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Surface(
                modifier = Modifier
                    .padding(horizontal = 15.dp, vertical = getStatusBarHeight())
                    .fillMaxWidth()
                    .wrapContentHeight(),
                color = MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(10.dp),
                shadowElevation = 4.dp
            ) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 15.dp),
                    color = MaterialTheme.colorScheme.onError
                )
            }
        }
    }
}


@Composable
fun MainScreen() {
    val showToast = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopToast("This is a top toast", showToast) {
            showToast.value = false
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(onClick = { showToast.value = true }) {
            Text("Show Top Toast")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


@SuppressLint("InternalInsetResource", "DiscouragedApi")
@Composable
fun getStatusBarHeight(): Dp {
    val context = LocalContext.current
    val resourceId = context.resources.getIdentifier(
        "status_bar_height", "dimen", "android"
    )
    val statusBarHeightPixels = if (resourceId > 0) {
        context.resources.getDimensionPixelSize(resourceId)
    } else 0

    return with(LocalDensity.current) {
        statusBarHeightPixels.toDp()
    }
}