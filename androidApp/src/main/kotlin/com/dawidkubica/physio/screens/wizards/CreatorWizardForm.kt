package com.dawidkubica.physio.screens.wizards

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawidkubica.physio.R
import com.dawidkubica.physio.screens.wizards.components.AdditionalButtons
import com.dawidkubica.physio.screens.wizards.components.ButtonType
import com.dawidkubica.physio.screens.wizards.components.SelectionButton
import com.dawidkubica.physio.screens.wizards.viewmodels.CreatorWizardViewModel

@Composable
fun CreatorWizardForm(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    viewModel: CreatorWizardViewModel = hiltViewModel()
) {
    val (selectedButton, setSelectedButton) = remember { mutableStateOf<ButtonType?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp),
            text = buildAnnotatedString {
                val styleHighlight = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_bold))
                )
                append("Wybierz, czy chcesz się zająć ")
                val startHighlight1 = length
                append("pakietami ćwiczeń")
                addStyle(styleHighlight, startHighlight1, length)
                append(" czy ")
                val startHighlight2 = length
                append("ćwiczeniami")
                addStyle(styleHighlight, startHighlight2, length)
            },
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        ButtonType.entries.forEach { buttonType ->
            SelectionButton(
                selectedButton = selectedButton,
                setSelectedButton = setSelectedButton,
                buttonType = buttonType
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (selectedButton != null) {
            AdditionalButtons(selectedButton, navigate, viewModel)
        }
    }
}