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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
fun ParameterSquare(
    parameter: ParameterAPI.Parameter,
    modifier: Modifier = Modifier,
    onDelete: (ParameterAPI.Parameter) -> Unit,
    onUpdate: (ParameterAPI.Parameter, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newText by remember { mutableStateOf(parameter.value) }

    Box(
        modifier = modifier
            .border(2.dp, Color(0xFF62519F), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        if (isEditing) {
            BasicTextField(
                value = newText,
                onValueChange = { newText = it },
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .wrapContentSize(Alignment.Center),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newText.isNotEmpty()) {
                            onUpdate(parameter, newText)
                            isEditing = false
                        }
                    }
                ),
                singleLine = true,
            )
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { onDelete(parameter) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete"
                    )
                }
                Text(
                    text = "${parameter.key}: ${parameter.value}",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                IconButton(onClick = { isEditing = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_edit),
                        contentDescription = "Edit"
                    )
                }
            }
        }
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

@Composable
fun MainScreen(thingTitle: String) {
    var paramList by remember { mutableStateOf(param_api.getParameters(thingTitle).data.toMutableList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AddParameterButton(
            thingTitle = thingTitle,
            createParameter = { title, key, value ->
                param_api.createParameter(title, key, value)
                paramList = param_api.getParameters(thingTitle).data.toMutableList()
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (paramList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Parameters not found")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(paramList.size) { index ->
                    ParameterSquare(
                        parameter = paramList[index],
                        onDelete = { parameter ->
                            param_api.deleteParameter(thingTitle, parameter.key, parameter.value)
                            paramList = param_api.getParameters(thingTitle).data.toMutableList()
                        },
                        onUpdate = { parameter, newValue ->
                            param_api.updateParameter(
                                ParameterAPI.OldParameter(thingTitle, parameter.key, parameter.value),
                                ParameterAPI.NewParameter(parameter.key, newValue)
                            )
                            paramList = param_api.getParameters(thingTitle).data.toMutableList()
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}
