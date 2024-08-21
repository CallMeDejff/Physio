package com.example.physio.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.physio.screens.SignUpScreen
import com.example.physio.ui.PhysioTheme
import com.example.physio.viewmodels.SignUpViewModel


class SignUpActivity : ComponentActivity() {
    private val signupViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this) {
            finish()
        }

        setContent {
            PhysioTheme {
                SignUpScreen(
                    signupViewModel = signupViewModel
                )
            }
        }
    }
}