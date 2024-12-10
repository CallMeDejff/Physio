package com.dawidkubica.physio.screens.profile.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.R
import com.dawidkubica.physio.ui.theme.GreenConfirmed
import com.dawidkubica.physio.ui.theme.RedConfirmed
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun UserInfoBox(
    userName: String,
    userLastname: String,
    userEmail: String,
    userType: Int,
    userLicenseNumber: String,
    emailVerified: Boolean,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(align = Alignment.Top)
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.tertiary, shape = RoundedCornerShape(16.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
            .animateContentSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.align(Alignment.Center)
        ) {
            ProfilePicture(
                displayName = userName,
                lastname = userLastname,
                isEmailVerified = emailVerified
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "$userName $userLastname",
                style = typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(4.dp))

            UserInfoText(label = "", value = userEmail)

            Spacer(modifier = Modifier.size(4.dp))

            PremiumStatusBox(isPremium = true)

            if (isExpanded) {

                Spacer(modifier = Modifier.size(4.dp))

                if (userType == 1) {
                    UserInfoText(
                        label = context.getString(R.string.license_number),
                        value = userLicenseNumber
                    )
                }

                UserInfoText(
                    icon = if (emailVerified) Icons.Outlined.Done else Icons.Outlined.DoNotDisturbOn,
                    label = context.getString(R.string.verified_account)
                )
            }
        }
    }
}


@Composable
fun PremiumStatusBox(
    isPremium: Boolean
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isPremium) GreenConfirmed else RedConfirmed,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(start = 22.dp, end = 22.dp, top = 4.dp, bottom = 4.dp)
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