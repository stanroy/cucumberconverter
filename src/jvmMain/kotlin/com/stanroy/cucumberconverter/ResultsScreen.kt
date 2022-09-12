import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape

@Composable
fun resultsScreen(results: List<String>?) {
    var textInput by remember { mutableStateOf("") }
    results?.forEach {
        textInput += it
    }
    Column {
        TextField(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            value = textInput,
            onValueChange = { textInput = it },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
            ),
            shape = RectangleShape,
        )
    }
}


//Scenario Maker starts a game
/*
func test_maker_starts_a_game() throws {

    //When the Maker starts a game by clicking "Start" button
    //param1: "Start"
    try test_the_maker_starts_a_game_by_clicking_param1_button(param1: TYPE)

        //Then the Maker waits for a Breaker to join
        try test_the_maker_waits_for_a_breaker_to_join()

}*/
