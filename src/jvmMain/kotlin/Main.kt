// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import navigation.Screen

@Composable
@Preview
fun App() {
    var screenState = remember { mutableStateOf<Screen>(Screen.GenerateScreen) }

    MaterialTheme {
        Column {
            navBar(modifier = Modifier.fillMaxWidth(), screenState = screenState.value,
                onScreenStateChange = { newScreen -> screenState.value = newScreen })
            when (screenState.value) {
                is Screen.GenerateScreen -> generateScreen(){}
                is Screen.ResultsScreen -> resultsScreen()
            }
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication, title = "Cucumber Converter"
    ) {
        App()
    }
}
