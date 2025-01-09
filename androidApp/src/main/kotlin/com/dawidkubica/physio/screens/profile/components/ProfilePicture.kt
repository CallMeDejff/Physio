package com.dawidkubica.physio.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawidkubica.physio.models.Provider
import com.dawidkubica.physio.ui.icons.FacebookIcon
import com.dawidkubica.physio.ui.icons.GoogleIcon
import com.dawidkubica.physio.ui.theme.GreenConfirmed

@Composable
fun ProfilePicture(
    displayName: String, lastname: String = "",
    providerId: Provider,
) {
    val firstLetter = displayName.firstOrNull()?.toString()?.uppercase() ?: ""
    val lastLetter = lastname.firstOrNull()?.toString()?.uppercase() ?: ""

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .wrapContentSize()
            .padding(8.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(88.dp)
                .background(MaterialTheme.colorScheme.surface, shape = CircleShape)
        ) {
            Text(
                text = firstLetter + lastLetter,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 34.sp
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.TopEnd)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color.Transparent, shape = CircleShape)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(
                        color = if (providerId == Provider.Physio) GreenConfirmed else (MaterialTheme.colorScheme.surface),
                        shape = CircleShape
                    )
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            ) {
                if (providerId == Provider.Physio) {
                } else {
                    Icon(
                        imageVector = when (providerId) {
                            //Provider.Physio -> PhysioLogo
                            Provider.Google -> GoogleIcon
                            Provider.Facebook -> FacebookIcon
                            else -> Icons.AutoMirrored.Outlined.HelpOutline
                        },
                        contentDescription = when (providerId) {
                            Provider.Physio -> "Verified by Physio"
                            Provider.Google -> "Verified by Google"
                            Provider.Facebook -> "Verified by Facebook"
                            else -> "Unknown provider"
                        },
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

        }
    }
}

