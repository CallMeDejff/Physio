package com.example.physio.screens.wizards.packageWizard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.physio.screens.wizards.viewmodels.PackageCreatorViewModel
import com.example.physio.ui.components.AutoComplete
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.typography

@Composable
fun PackageEditorForm(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: PackageCreatorViewModel,
) {
    val selectedPackage by viewModel.selectedPackages.collectAsState()
    val packagesList by viewModel.packagesList.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = buildAnnotatedString {
                append("Edytor istniejącego ")
                withStyle(style = SpanStyle(color = colorPrimary)) {
                    append("pakietu.")
                }
            },
            style = typography.bodyLarge,
        )

        Box(
            modifier = Modifier
                .heightIn(200.dp, 400.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Podaj tytuł pakietu",
                    style = typography.labelLarge,
                    modifier = Modifier.height(32.dp)
                )
                AutoComplete(
                    itemList = packagesList,
                    selectedItems = selectedPackage,
                    onToggleItem = { packageId -> viewModel.togglePackage(packageId) }
                )
            }
        }
    }
}