package com.example.front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.front.data.ThingAPI
import com.example.front.ui.theme.FrontTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    val thingAPI = ThingAPI()
    val things by produceState<List<ThingAPI.Thing>>(initialValue = emptyList()) {
        value = withContext(Dispatchers.IO) {
            (thingAPI.getAllThing()?.data ?: emptyList())
        }
    }

    Scaffold {
        Column(modifier = Modifier.fillMaxSize()) {
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
                onClick = {
                    // Обработка нажатия на кнопку добавления
                },
                modifier = Modifier.padding(10.dp)
            ) {
                Text(text = "Добавить")
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(things) { thing ->
                    Text(
                        text = thing.title,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
//                                // Переход на другую activity при нажатии
//                                val context = LocalContext.current
//                                val intent = Intent(context, DetailActivity::class.java)
//                                intent.putExtra("thing_id", thing.id)
//                                context.startActivity(intent)
                            }
                    )
                }
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
