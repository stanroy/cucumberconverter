import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun generateScreen(onGenerate: (results: List<String>) -> Unit) {
    var text by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
        TextField(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            value = text,
            onValueChange = { text = it },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RectangleShape
        )
        Button(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f), onClick = {
                println(splitScenarios(text).count())
            }, shape = RectangleShape
        ) {
            Text("GENERATE")

        }
    }
}

fun splitScenarios(text: String): MutableList<List<String>> {
    val regex = """((?=Scenario)|(?=Given)|(?=When)|(?=Then))|(?=And)|(?=But)""".toRegex()
    val filteredList = text.split(regex).filterNot { step ->
        step.isEmpty()
    }.toMutableList()
    val bufferList = mutableListOf<String>()
    val scenarioParts = mutableListOf<List<String>>()

    while (filteredList.iterator().hasNext()) {
        if (filteredList.first().contains("Scenario") && bufferList.isEmpty()) {
            bufferList.add(filteredList.first().trimEnd())
            filteredList.removeFirst()
        }

        if (!filteredList.iterator().next().contains("Scenario")) {
            bufferList.add(filteredList.iterator().next().trimEnd())
            filteredList.remove(filteredList.iterator().next())
        } else {
            scenarioParts.add(bufferList.toMutableList())
            bufferList.clear()
        }
    }

    if (bufferList.isNotEmpty()) {
        scenarioParts.add(bufferList.toMutableList())
        bufferList.clear()
    }

    return scenarioParts
}