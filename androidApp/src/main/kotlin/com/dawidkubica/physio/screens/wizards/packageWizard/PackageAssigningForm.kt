package com.dawidkubica.physio.screens.wizards.packageWizard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.dawidkubica.physio.screens.wizards.viewmodels.PackageCreatorViewModel
import com.dawidkubica.physio.ui.components.AutoCompleteDetailed
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun PackageAssigningForm(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: PackageCreatorViewModel,
) {
    val selectedPackage by viewModel.selectedPackages.collectAsState()
    val packagesList by viewModel.packagesList.collectAsState()
    val usersList by viewModel.usersList.collectAsState()
    val selectedUsers by viewModel.selectedUsers.collectAsState()

    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = buildAnnotatedString {
                append("Przypisz pakiet ćwiczeń ")
                withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                    append("pacjentowi.")
                }
            },
            style = typography.bodyLarge,
            textAlign = TextAlign.Center,

            )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Podaj tytuł pakietu",
            style = typography.labelLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.height(32.dp)
        )

        Box(
            modifier = Modifier
                .height(200.dp)
        ) {
            AutoCompleteDetailed(
                itemList = packagesList,
                selectedItems = selectedPackage,
                onToggleItem = { packageId -> viewModel.togglePackage(packageId) }
            )
        }

        Text(
            text = "Podaj email pacjenta",
            textAlign = TextAlign.Center,
            style = typography.labelLarge,
            modifier = Modifier.height(32.dp)
        )

        Box(
            modifier = Modifier
                .height(200.dp)
        ) {
            AutoCompleteDetailed(
                userList = usersList,
                selectedItems = selectedUsers,
                onToggleItem = { userId ->
                    viewModel.toggleUser(userId)
                }
            )
        }
    }
}