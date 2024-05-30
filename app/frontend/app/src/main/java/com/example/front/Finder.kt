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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.front.data.ThingAPI
import com.example.front.ui.theme.FrontTheme

val api = ThingAPI()

class Finder : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontTheme {
                SearchPromptCreate()
            }
        }
    }
}

@Composable
fun ThingSquare(
    text: String,
    modifier: Modifier = Modifier,
    onDelete: (String) -> Unit,
    onUpdate: (String, String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newText by remember { mutableStateOf(text) }

    Box(
        modifier = modifier
            .border(6.dp, Color(0xFF62519F), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { onDelete(text) }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete"
                )
            }
            if (isEditing) {
                TextField(
                    value = newText,
                    onValueChange = { newText = it },
                    modifier = Modifier.padding(horizontal = 16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            onUpdate(text, newText)
                            isEditing = false
                        }
                    )
                )
            } else {
                Text(
                    text = text,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            IconButton(onClick = { isEditing = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit"
                )
            }
        }
    }
}


@Composable
fun ShowAllThings(thingList: List<ThingAPI.Thing>, onDelete: (String) -> Unit, onUpdate: (String, String) -> Unit) {
    if (thingList.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No items found")
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(thingList.size) { index ->
                ThingSquare(
                    text = thingList[index].title,
                    onDelete = onDelete,
                    onUpdate = onUpdate
                )
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}


@Composable
fun AddButton(modifier: Modifier = Modifier, onAdd: (String) -> Unit) {
    var isTextFieldVisible by remember { mutableStateOf(false) }
    var textState by remember { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(6.dp, Color(0xFF62519F), RoundedCornerShape(12.dp))
            .padding(10.dp)
            .height(60.dp)
            .width(200.dp)
            .clickable { isTextFieldVisible = !isTextFieldVisible },
        contentAlignment = Alignment.Center,
    ) {
        if (isTextFieldVisible) {
            BasicTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onAdd(textState)
                        isTextFieldVisible = false
                        textState = ""
                    }
                ),
                singleLine = true,
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Add",
                tint = Color(0xFF62519F),
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPromptCreate(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    var thingList by remember { mutableStateOf(api.getAllThing().data) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                query = searchText,
                onQueryChange = { text ->
                    searchText = text
                    thingList = if (text.isEmpty()) {
                        api.getAllThing().data
                    } else {
                        api.getThing(text).data
                    }
                },
                onSearch = {
                    thingList = if (searchText.isEmpty()) {
                        api.getAllThing().data
                    } else {
                        api.getThing(searchText).data
                    }
                },
                active = false,
                onActiveChange = {},
                placeholder = { Text(stringResource(R.string.find_name)) },
            ) {}

            AddButton(onAdd = { newText ->
                api.addThing(newText)
                thingList = if (searchText.isEmpty()) {
                    api.getAllThing().data
                } else {
                    api.getThing(searchText).data
                }
            })
            Spacer(modifier = Modifier.height(10.dp))
            ShowAllThings(
                thingList = thingList,
                onDelete = { title ->
                    api.deleteThing(title)
                    thingList = if (searchText.isEmpty()) {
                        api.getAllThing().data
                    } else {
                        api.getThing(searchText).data
                    }
                },
                onUpdate = { oldTitle, newTitle ->
                    api.updateThingName(oldTitle, newTitle)
                    thingList = if (searchText.isEmpty()) {
                        api.getAllThing().data
                    } else {
                        api.getThing(searchText).data
                    }
                }
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview2() {
    FrontTheme {
        SearchPromptCreate()
    }
}
