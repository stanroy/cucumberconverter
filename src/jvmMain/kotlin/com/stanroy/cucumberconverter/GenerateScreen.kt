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
    var textInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
        TextField(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            value = textInput,
            onValueChange = { textInput = it },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RectangleShape
        )
        Button(
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f), onClick = {
                val scenarios = splitScenarios(textInput)
                onGenerate(createSwiftFunctions(scenarios))
            }, shape = RectangleShape
        ) {
            Text("GENERATE")

        }
    }
}

fun createSwiftFunctions(scenarios: List<List<String>>): List<String> {
    val functions = mutableListOf<String>()
    scenarios.forEach {
        // scenario main function
        val scenarioList = it.toMutableList()
        val scenarioComment = "//${scenarioList.first().filterNot { char -> char == ':' }}"
        val scenarioMainFunction = "func test_${
            scenarioComment.removePrefix("//Scenario ").lowercase().replace(" ", "_").plus("() throws {\n")
        }"
        scenarioList.removeFirst()

        // scenario step functions
        val stepFunctions = filterStepFunctions(scenarioList)


        // scenario full function
        val swiftFunction = "".let { func ->
            func + "$scenarioComment\n" + "$scenarioMainFunction\n" + "${stepFunctions.trimEnd()}\n\n" +
                    // close func
                    "}\n\n"
        }

        functions.add(swiftFunction)
    }
    return functions
}

fun filterStepFunctions(steps: List<String>): String {
    var stepFunctions = ""
    steps.forEach { step ->
        val hasParams = step.contains("\"")
        val testFunc = createTestFunction(step, hasParams)
        val comment = testFunc.first
        val func = testFunc.second

        stepFunctions += comment
        stepFunctions += "$func\n\n"

    }
    return stepFunctions
}

fun createTestFunction(step: String, hasParams: Boolean): Pair<String, String> {
    val func: String
    // basic function comment
    var comment = "\t//$step\n"
    // regex for step prefix
    val reg = """(\b(When|Then|Given|And|But)\b)""".toRegex()

    // regex to find params in steps
    val regParams = "\"([^\"]*)\"".toRegex()
    val matches = regParams.findAll(step)
    val results = matches.map { match ->
        match.value.replace("\"", "")
    }.toMutableList()

    if (!hasParams) {
        func = "\ttry test${
            step.replace(reg, "").lowercase().replace("'", "").replace(" ", "_").plus("()")
        }"
    } else {
        var funcParams = ""
        val newStep = step.replace(regParams, "").replace("  ", " ").trimEnd()
        results.forEachIndexed { index, param ->
            // put params in function comment
            comment += "\t//param$index: \"$param\"\n"
            funcParams += "param$index: TYPE, "
        }

        val funcParamsFiltered = if (results.count() == 1)
            funcParams.replace(",", "").trimEnd()
        else funcParams.trimEnd().removeSuffix(",")

        func = "\ttry test${
            newStep.replace(reg, "")
                .lowercase()
                .replace("'", "")
                .replace(" ", "_")
                .replace("\"", "")
                .plus("($funcParamsFiltered)")
        }"
    }

    return Pair(comment, func)
}

fun splitScenarios(text: String): MutableList<List<String>> {
    val regex = """((?=Scenario)|(?=Given)|(?=When)|(?=Then))|(?=And)|(?=But)""".toRegex()
    val filteredList = text.split(regex).filterNot { step ->
        step.isEmpty()
    }.filterNot { step -> step.contains("#") }.toMutableList() // remove gherkin comments
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