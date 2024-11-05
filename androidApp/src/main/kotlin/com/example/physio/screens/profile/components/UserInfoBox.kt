package com.example.physio.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.physio.ui.theme.PurpleGrey80
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.typography

@Composable
fun UserInfoBox(
    userName: String,
    userLastname: String,
    userEmail: String,
    userType: Int,
    userLicenseNumber: String,
    userAssignedPackages: Int,
    userFavoritePackages: Int,
    emailVerified: Boolean,
    screenHeight: Dp
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .heightIn(max = screenHeight * 2 / 5)
            .clip(RoundedCornerShape(16.dp))
            .background(color = PurpleGrey80, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.align(Alignment.TopStart)
        ) {
            Row(
                modifier = Modifier
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfilePicture(displayName = userName)

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "$userName $userLastname",
                    style = typography.headlineMedium,
                    color = colorPrimary
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                if (userType == 1) {
                    UserInfoText(label = "Numer licencji:", value = userLicenseNumber)
                }
                UserInfoText(label = "Email:", value = userEmail)
                UserInfoText(
                    label = "Liczba przypisanych pakietów:",
                    value = userAssignedPackages.toString()
                )
                UserInfoText(
                    label = "Liczba ulubionych pakietów:",
                    value = userFavoritePackages.toString()
                )
                UserInfoText(
                    icon = if (emailVerified) Icons.Outlined.Done else Icons.Outlined.DoNotDisturbOn,
                    label = "Konto zweryfikowane"
                )
            }
        }
    }
}