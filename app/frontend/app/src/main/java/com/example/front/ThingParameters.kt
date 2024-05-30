package com.example.front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.front.data.ParameterAPI
import com.example.front.ui.theme.FrontTheme

val param_api = ParameterAPI()

class ThingParameters : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val itemTitle = intent.getStringExtra("item_title")

        setContent {
            FrontTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        MainScreen(thingTitle = itemTitle ?: "")
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(thingTitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddParameterButton(
            thingTitle = thingTitle,
            createParameter = { title, key, value ->
                param_api.createParameter(title, key, value)
            }
        )
    }
}

@Composable
fun AddParameterButton(
    thingTitle: String,
    createParameter: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isKeyInputVisible by remember { mutableStateOf(false) }
    var isValueInputVisible by remember { mutableStateOf(false) }
    var keyText by remember { mutableStateOf("") }
    var valueText by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .border(2.dp, Color(0xFF62519F), RoundedCornerShape(12.dp))
            .padding(16.dp)
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when {
                isValueInputVisible -> {
                    OutlinedTextField(
                        value = valueText,
                        onValueChange = { valueText = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        label = { Text("Введите значение") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (valueText.isNotEmpty()) {
                                    createParameter(thingTitle, keyText, valueText)
                                    keyText = ""
                                    valueText = ""
                                    isKeyInputVisible = false
                                    isValueInputVisible = false
                                }
                            }
                        ),
                        singleLine = true,
                    )
                }

                isKeyInputVisible -> {
                    OutlinedTextField(
                        value = keyText,
                        onValueChange = { keyText = it },
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxWidth(),
                        label = { Text("Введите ключ") },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (keyText.isNotEmpty()) {
                                    isKeyInputVisible = false
                                    isValueInputVisible = true
                                }
                            }
                        ),
                        singleLine = true,
                    )
                }

                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "Add",
                        tint = Color(0xFF62519F),
                        modifier = Modifier
                            .size(48.dp)
                            .clickable {
                                isKeyInputVisible = true
                            }
                    )
                }
            }
        }
    }
}