package com.example.physio.screens.wizards.packageWizard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.physio.screens.wizards.viewmodels.PackageCreatorViewModel
import com.example.physio.ui.components.AutoComplete
import com.example.physio.ui.components.AutoCompleteDetailed
import com.example.physio.ui.icons.Clinical_notes
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.typography

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
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = buildAnnotatedString {
                append("Przypisz pakiet ćwiczeń ")
                withStyle(style = SpanStyle(color = colorPrimary)) {
                    append("pacjentowi.")
                }
            },
            style = typography.bodyLarge,
        )

        Box(
            modifier = Modifier
                .heightIn(150.dp, 250.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Podaj tytuł pakietu",
                    style = typography.labelLarge,
                    modifier = Modifier.height(32.dp)
                )
                AutoCompleteDetailed(
                    itemList = packagesList,
                    selectedItems = selectedPackage,
                    onToggleItem = { packageId -> viewModel.togglePackage(packageId) }
                )
            }
        }
        Box(
            modifier = Modifier
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Podaj email pacjenta",
                    style = typography.labelLarge,
                    modifier = Modifier.height(32.dp)
                )
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
}