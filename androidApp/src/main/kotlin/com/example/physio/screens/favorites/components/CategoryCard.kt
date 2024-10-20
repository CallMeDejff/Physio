package com.example.physio.screens.favorites.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.physio.ui.theme.PurpleGrey40
import com.example.physio.ui.theme.PurpleGrey80
import com.example.physio.ui.theme.typography

@Composable
fun CategoryCard(
    title: String,
    content: String) {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    Card(
        modifier = Modifier
            .width(screenWidth * 7 / 10)
            .fillMaxHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, style = typography.headlineSmall)
            Text(text = content, style = typography.bodyMedium)
        }
    }
}