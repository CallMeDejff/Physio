package com.example.physio.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.physio.screens.LoginScreen
import com.example.physio.ui.*
import com.example.physio.viewmodels.LoginViewModel
import com.physio.Greeting

class LoginActivity : ComponentActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("Login Activity", "Hello from shared module: " + (Greeting().greet()))


        setContent {
            PhysioTheme {
                LoginScreen(
                    loginViewModel = loginViewModel
                )
            }
        }
    }


}