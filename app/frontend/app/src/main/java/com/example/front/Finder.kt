package com.example.front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.front.ui.theme.FrontTheme

class Finder : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontTheme {
                SerchPromptCreate()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerchPromptCreate(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    SearchBar(
        modifier = Modifier.fillMaxWidth()
            .padding(10.dp),
        query = searchText,
        onQueryChange = {text ->
            searchText = text
        },
        onSearch = {},
        active = false,
        onActiveChange = {},
        placeholder = { Text("Поиск...") },
    ) {

    }
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview2() {
    FrontTheme {
        SerchPromptCreate()
    }
}