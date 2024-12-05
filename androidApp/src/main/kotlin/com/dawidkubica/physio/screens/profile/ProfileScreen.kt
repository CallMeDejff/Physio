package com.dawidkubica.physio.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.models.Provider
import com.dawidkubica.physio.screens.edit_user.components.AlertEmailChange
import com.dawidkubica.physio.screens.profile.components.AlertEmailVerification
import com.dawidkubica.physio.screens.profile.components.UserInfoBox
import com.dawidkubica.physio.screens.wizards.components.ActionButton
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ProfileScreen(
    openAndPopUp: (String, String) -> Unit,
    navigate: (String) -> Unit,
    viewModel: ProfileViewModel
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
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
    val userAssignedPackages by viewModel.userAssignedPackages.collectAsState()
    val userFavoritePackages by viewModel.userFavoritePackages.collectAsState()

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                viewModel.clearMessage()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchUserInformation()
    }

    if (showEmailChangeDialog.value) {
        AlertEmailChange(
            showDialog = showEmailChangeDialog,
            statement = "Czy na pewno chcesz zapisać zmiany? Jeżeli zmieniasz adres email zostaniesz wylogowany i poproszony o weryfikację nowego adresu w osobnej wiadomości email przed ponownym zalogowaniem.",
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
            statement = "Na podany adres email: {$userEmail} zostanie wysłana wiadomość z linkiem do weryfikacji. Potwierdź czy na pewno chcesz wykonać tą operację",
            confirmStat = "Zweryfikuj adres email",
            dismissStat = "Anuluj",
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
            statement = "Czy jesteś pewien, że chcesz usunąć konto w aplikacji Physio? Tej operacji nie można cofnąć.",
            confirmStat = "Tak, chcę usunąć konto",
            dismissStat = "Anuluj",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
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
                Column (
                    modifier = Modifier
                        .padding(top = 10.dp)
                ){
                    Text(
                        text = " Panel ustawień",
                        style = typography.headlineLarge
                    )

                    Text(
                        text = " Dostosuj wszystko pod siebie",
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
                        userAssignedPackages = userAssignedPackages.size,
                        userFavoritePackages = userFavoritePackages.size,
                        emailVerified = emailVerified,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ActionButton(
                        Icons.Outlined.Edit,
                        "Edytuj dane użytkownika"
                    ) { viewModel.onEditUserClick(navigate) }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (accProvider == Provider.Physio.providerId) {
                        ActionButton(
                            Icons.AutoMirrored.Outlined.Message,
                            "Zmień adres email"
                        ) { showEmailChangeDialog.value = true }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (!emailVerified) {
                        ActionButton(
                            Icons.Outlined.VerifiedUser,
                            "Zweryfikuj adres email"
                        ) { showEmailVerificationDialog.value = true }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (accProvider == Provider.Physio.providerId) {
                        ActionButton(
                            icon = Icons.Outlined.Password,
                            label = "Zmień hasło"
                        ) { viewModel.onChangePasswordClick(navigate) }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    ActionButton(
                        Icons.AutoMirrored.Outlined.Logout,
                        "Wyloguj"
                    ) { viewModel.onLogoutClick(openAndPopUp) }

                    Spacer(modifier = Modifier.height(16.dp))

                    ActionButton(
                        Icons.Outlined.DeleteForever,
                        "Usuń konto"
                    ) { showAccountDeleteDialog.value = true }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}



