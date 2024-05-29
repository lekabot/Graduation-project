package com.example.front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
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
import androidx.compose.ui.res.stringResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPromptCreate(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    val thingList = api.getAllThing()

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
                },
                onSearch = {},
                active = false,
                onActiveChange = {},
                placeholder = { Text(stringResource(R.string.find_name)) },
            ) {}

            Button(
                onClick = { /* Добавление элемента */ },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Добавить")
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(thingList.data.size) { index ->
                    ThingSquare(
                        text = thingList.data[index].title)
                }
            }
        }
    }
}

@Composable
fun ThingSquare(text: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            IconButton(onClick = { /*api.deleteThing()*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Delete"
                )
            }
            Text(
                text = text,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            IconButton(onClick = { /* Handle edit action */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit"
                )
            }

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
