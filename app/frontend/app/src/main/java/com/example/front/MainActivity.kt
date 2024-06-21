package com.example.front

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.front.data.AuthAPI
import com.example.front.ui.theme.Errors.ErrorMessage
import com.example.front.ui.theme.FrontTheme
import com.example.front.ui.theme.UIObject.Link
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FrontTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreen()
                }
            }
        }
    }

    @Composable
    fun LoginScreen() {
        val api = AuthAPI()
        val context = LocalContext.current
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
//        var username = "admin"
//        var password = "admin"
        var errorMessage by remember { mutableStateOf("") }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Войти",
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Имя пользователя") },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        val loggedIn = api.login(
                            username=username,
                            password=password)
                        withContext(Dispatchers.Main) {
                            if (loggedIn) {
                                errorMessage = "Вы вошли"
                                val intent = Intent(context, Finder::class.java)
                                context.startActivity(intent)
                            } else {
                                errorMessage = "Неверный логин или пароль"
                            }
                        }
                    }
                },
            ) {
                Text("Войти")
            }
            Spacer(modifier = Modifier.height(16.dp))
            ErrorMessage(errorMessage)
            Spacer(modifier = Modifier.height(16.dp))
            Link(text = "Ешё нет аккаунта?", dest = Registration::class.java)
        }
    }


    @Preview(
        showBackground = true,
        showSystemUi = true,
    )
    @Composable
    fun LoginScreenPreview() {
        FrontTheme {
            LoginScreen()
        }
    }
}