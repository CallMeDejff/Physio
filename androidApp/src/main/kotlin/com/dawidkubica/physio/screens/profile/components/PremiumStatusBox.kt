package com.dawidkubica.physio.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.navigation.ProfileScreen
import com.dawidkubica.physio.ui.theme.GreenConfirmed
import com.dawidkubica.physio.ui.theme.RedConfirmed
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun PremiumStatusBox(
    navigate: (String) -> Unit,
    isPremium: Boolean
) {
    Button(
        onClick = { navigate(ProfileScreen.PayWall.route) },
        colors = ButtonDefaults.buttonColors(
            containerColor = GreenConfirmed
        ),
        modifier = Modifier
            .padding(start = 22.dp, end = 22.dp, top = 4.dp, bottom = 4.dp)
            .background(
                color = if (isPremium) GreenConfirmed else RedConfirmed,
                shape = RoundedCornerShape(24.dp)
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Premium",
                style = typography.headlineLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.size(8.dp))

            Icon(
                imageVector = if (isPremium) Icons.Outlined.Check else Icons.Outlined.DoNotDisturbOn,
                tint = Color.White,
                contentDescription = "Premium check"
            )
        }
    }
}