package com.example.front.ui.theme

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

object Errors {
    @Composable
    fun ErrorMessage(text: String) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.Transparent
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Red,
                textAlign = TextAlign.Center
            )
        }
    }
}