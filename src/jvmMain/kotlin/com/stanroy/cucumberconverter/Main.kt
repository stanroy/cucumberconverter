// Copyright 2000-2021 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.stanroy.cucumberconverter.generateScreen
import com.stanroy.cucumberconverter.navigation.Screen
import com.stanroy.cucumberconverter.navigation.navBar
import com.stanroy.cucumberconverter.resultsScreen
import com.stanroy.cucumberconverter.theme.CucumberConverterTheme

@Composable
@Preview
fun App() {
    val screenState = remember { mutableStateOf<Screen>(Screen.GenerateScreen) }

    CucumberConverterTheme {
        Column {
            navBar(modifier = Modifier.fillMaxWidth(), screenState = screenState,
                onScreenStateChange = { newScreen -> screenState.value = newScreen })
            when (screenState.value) {
                is Screen.GenerateScreen -> generateScreen(onGenerate = {
                    screenState.value = Screen.ResultsScreen(results = it)
                })

                is Screen.ResultsScreen -> resultsScreen(screenState.value.params)
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
