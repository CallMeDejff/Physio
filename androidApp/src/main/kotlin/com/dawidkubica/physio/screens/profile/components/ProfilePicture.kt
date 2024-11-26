package com.dawidkubica.physio.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.PurpleGrey80
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ProfilePicture(displayName: String, lastname: String = "") {
    val firstLetter = displayName.firstOrNull()?.toString()?.uppercase() ?: ""
    val lastLetter = lastname.firstOrNull()?.toString()?.uppercase() ?: ""

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(64.dp)
            .background(Color.White, shape = CircleShape)
    ) {
        Text(
            text = firstLetter + lastLetter,
            color = PurpleGrey80,
            style = typography.bodyLarge
        )
    }
}
