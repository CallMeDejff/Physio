package com.dawidkubica.physio.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawidkubica.physio.ui.theme.GreenConfirmed
import com.dawidkubica.physio.ui.theme.PurpleGrey80
import com.dawidkubica.physio.ui.theme.RedConfirmed
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ProfilePicture(
    displayName: String, lastname: String = "",
    isEmailVerified: Boolean,
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
                .background(Color.White, shape = CircleShape)
        ) {
            Text(
                text = firstLetter + lastLetter,
                color = PurpleGrey80,
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
                        color = PurpleGrey80,
                        shape = CircleShape
                    )
            )

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(if (isEmailVerified) GreenConfirmed else RedConfirmed, shape = CircleShape)
                    .border(
                        width = 2.dp,
                        color = Color.White,
                        shape = CircleShape
                    )
            )
        }
    }
}

