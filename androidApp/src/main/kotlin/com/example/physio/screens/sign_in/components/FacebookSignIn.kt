package com.example.physio.screens.sign_in.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.physio.ui.icons.FacebookIcon


@Composable
fun FacebookSignIn(
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = { },
        modifier = modifier
            .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),

        ) {
        Icon(
            imageVector = FacebookIcon,
            tint = Color.Gray,
            contentDescription = "Facebook Sign In button",
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .size(24.dp),
        )
    }
}