package com.dawidkubica.physio.screens.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.ui.theme.PurpleGrey80
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

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
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clip(RoundedCornerShape(16.dp))
            .background(color = PurpleGrey80, shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .align(Alignment.Center),
        ) {

            ProfilePicture(displayName = userName, lastname = userLastname)

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "$userName $userLastname",
                style = typography.headlineLarge,
                color = colorPrimary
            )

            Text(
                text = userEmail,
                style = typography.headlineMedium,
            )


            if (userType == 1) {
                UserInfoText(label = "Numer licencji:", value = userLicenseNumber)
            }

            UserInfoText(
                icon = if (emailVerified) Icons.Outlined.Done else Icons.Outlined.DoNotDisturbOn,
                label = "Konto zweryfikowane: "
            )
        }
    }
}