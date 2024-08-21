package com.example.physio.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.physio.ui.BottomNavigationBar
import com.example.physio.ui.PhysioBarTheme

@Composable
fun DashboardScreen (
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
){
    PhysioBarTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            BottomNavigationBar()
        }
    }
}