package com.example.front

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.front.ui.theme.FrontTheme

class ThingParameters(private val thingTitle: String) : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FrontTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainDrawer(
                        thingTitle = thingTitle,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun MainDrawer(
    thingTitle: String,
    modifier: Modifier = Modifier) {
    Text(
        text = "Hello $thingTitle!",
        modifier = modifier
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
fun GreetingPreview3() {
    FrontTheme {
        MainDrawer("Android")
    }
}