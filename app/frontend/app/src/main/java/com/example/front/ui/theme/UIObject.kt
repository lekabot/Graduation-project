package com.example.front.ui.theme

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.front.Registration

object UIObject {
    @Composable
    fun Link( text: String, dest: Class<*>) {
        val context = LocalContext.current

        Text(
            modifier = Modifier.clickable {
                val intent = Intent(context, dest)
                context.startActivity(intent)
            },
            text = text,
            color = Color.Blue
        )
    }
}