package com.dawidkubica.physio.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.R
import com.dawidkubica.physio.models.Provider
import com.dawidkubica.physio.models.ThemeMode
import com.dawidkubica.physio.screens.edit_user.components.AlertEmailChange
import com.dawidkubica.physio.screens.profile.components.AlertEmailVerification
import com.dawidkubica.physio.screens.profile.components.UserInfoBox
import com.dawidkubica.physio.screens.wizards.components.ActionButton
import com.dawidkubica.physio.ui.components.FullScreenLoader
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ProfileScreen(
    openAndPopUp: (String, String) -> Unit,
    navigate: (String) -> Unit,
    viewModel: ProfileViewModel
) {
    val context = LocalContext.current
    val showEmailChangeDialog = remember { mutableStateOf(false) }
    val showEmailVerificationDialog = remember { mutableStateOf(false) }
    val showAccountDeleteDialog = remember { mutableStateOf(false) }
    val isLoading by viewModel.isLoading.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val userLastname by viewModel.userLastname.collectAsState()
    val userEmail by viewModel.userEmail.collectAsState()
    val emailVerified by viewModel.isEmailVerified.collectAsState()
    val userType by viewModel.userType.collectAsState()
    val accProvider by viewModel.accProvider.collectAsState()
    val userLicenseNumber by viewModel.userLicenseNumber.collectAsState()
    val currentThemeMode by viewModel.themeMode.collectAsState()

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    if (showEmailChangeDialog.value) {
        AlertEmailChange(
            showDialog = showEmailChangeDialog,
            statement = context.getString(R.string.email_change_dialog_statement),
            onConfirm = { newEmail ->
                viewModel.callEmailChangeLogout(newEmail, openAndPopUp)
                showEmailChangeDialog.value = false
            },
            onDismiss = {
                showEmailChangeDialog.value = false
            }
        )
    }

    if (showEmailVerificationDialog.value) {
        AlertEmailVerification(
            showDialog = showEmailVerificationDialog,
            statement = context.getString(R.string.email_verification_dialog_statement, userEmail),
            confirmStat = context.getString(R.string.verify_email_confirm),
            dismissStat = context.getString(R.string.verify_email_cancel),
            onConfirm = {
                viewModel.callEmailVerification()
                showEmailVerificationDialog.value = false
            },
            onDismiss = {
                showEmailVerificationDialog.value = false
            }
        )
    }

    if (showAccountDeleteDialog.value) {
        AlertEmailVerification(
            showDialog = showAccountDeleteDialog,
            statement = context.getString(R.string.delete_account_dialog_statement),
            confirmStat = context.getString(R.string.delete_account_confirm),
            dismissStat = context.getString(R.string.delete_account_cancel),
            onConfirm = {
                viewModel.callUserDelete(openAndPopUp)
                showAccountDeleteDialog.value = false
            },
            onDismiss = {
                showAccountDeleteDialog.value = false
            }
        )
    }

    if (isLoading) {
        FullScreenLoader()
    } else {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 25.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = context.getString(R.string.profile_settings_panel),
                        style = typography.headlineLarge
                    )

                    Text(
                        text = context.getString(R.string.profile_customize),
                        style = typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    UserInfoBox(
                        userName = userName,
                        userLastname = userLastname,
                        userEmail = userEmail,
                        userType = userType,
                        userLicenseNumber = userLicenseNumber.toString(),
                        emailVerified = emailVerified,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ColorSchemeSelector(
                        viewModel = viewModel,
                        currentThemeMode = currentThemeMode
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ActionButton(
                        Icons.Outlined.Edit,
                        context.getString(R.string.edit_user_data)
                    ) { viewModel.onEditUserClick(navigate) }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (accProvider == Provider.Physio.providerId) {
                        ActionButton(
                            Icons.AutoMirrored.Outlined.Message,
                            context.getString(R.string.change_email)
                        ) { showEmailChangeDialog.value = true }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (!emailVerified) {
                        ActionButton(
                            Icons.Outlined.VerifiedUser,
                            context.getString(R.string.verify_email)
                        ) { showEmailVerificationDialog.value = true }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (accProvider == Provider.Physio.providerId) {
                        ActionButton(
                            icon = Icons.Outlined.Password,
                            label = context.getString(R.string.change_password)
                        ) { viewModel.onChangePasswordClick(navigate) }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    ActionButton(
                        Icons.AutoMirrored.Outlined.Logout,
                        context.getString(R.string.logout)
                    ) { viewModel.onLogoutClick(openAndPopUp) }

                    Spacer(modifier = Modifier.height(16.dp))

                    ActionButton(
                        Icons.Outlined.DeleteForever,
                        context.getString(R.string.delete_account)
                    ) { showAccountDeleteDialog.value = true }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


@Composable
fun ColorSchemeSelector(
    viewModel: ProfileViewModel,
    currentThemeMode: ThemeMode
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconToggleButton(
            checked = currentThemeMode == ThemeMode.LIGHT,
            onCheckedChange = {
                if (it) viewModel.setThemeMode(ThemeMode.LIGHT)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.WbSunny,
                contentDescription = "Zawsze jasny",
                tint = if (currentThemeMode == ThemeMode.LIGHT) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        IconToggleButton(
            checked = currentThemeMode == ThemeMode.DARK,
            onCheckedChange = {
                if (it) viewModel.setThemeMode(ThemeMode.DARK)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.NightsStay,
                contentDescription = "Zawsze ciemny",
                tint = if (currentThemeMode == ThemeMode.DARK) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }

        IconToggleButton(
            checked = currentThemeMode == ThemeMode.SYSTEM,
            onCheckedChange = {
                if (it) viewModel.setThemeMode(ThemeMode.SYSTEM)
            }
        ) {
            Icon(
                imageVector = Icons.Outlined.Sync,
                contentDescription = "Automatyczny (systemowy)",
                tint = if (currentThemeMode == ThemeMode.SYSTEM) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}



