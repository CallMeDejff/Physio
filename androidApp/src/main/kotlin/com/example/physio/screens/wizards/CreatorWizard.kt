package com.example.physio.screens.wizards

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physio.R
import com.example.physio.screens.sign_in.components.HeaderView
import com.example.physio.ui.PhysioTheme
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.colorSecondary
import com.example.physio.ui.ghost_white
import com.example.physio.ui.typography

@Composable
fun CreatorWizardScreen(
    navigate: (String) -> Unit,
    popBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreatorWizardViewModel
) {
    val context = LocalContext.current

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            message?.let {
                Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (header, wizardForm, exitButton) = createRefs()

        HeaderView(
            modifier = Modifier
                .height(320.dp)
                .fillMaxWidth()
                .constrainAs(header) {
                    top.linkTo(parent.top)
                },
            200, 0.7f
        )

        Card(
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = ghost_white),
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(wizardForm) {
                    top.linkTo(header.bottom)
                    bottom.linkTo(exitButton.top)
                }
        ) {
            CreatorWizardForm(
                navigate,
                popBackStack
            )
        }

        Button(
            onClick = { viewModel.onExitWizardClick(navigate) },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth()
                .constrainAs(exitButton) {
                    bottom.linkTo(parent.bottom)
                },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
        ) {
            Text(
                text = "Wyjdź z kreatora",
                color = Color.White,
                style = typography.labelLarge,
                modifier = Modifier.padding(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(1.dp))
    }
}

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
                    color = colorPrimary,
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


@Composable
fun SelectionButton(
    selectedButton: ButtonType?,
    setSelectedButton: (ButtonType) -> Unit,
    buttonType: ButtonType
) {
    TextButton(
        onClick = { setSelectedButton(buttonType) },
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 2.dp,
                color = if (selectedButton == buttonType) colorPrimary else colorSecondary,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (selectedButton == buttonType) colorPrimary else colorSecondary)
    ) {
        Text(
            text = buttonType.displayName,
            color = Color.White,
            style = typography.labelLarge,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
fun AdditionalButtons(
    selectedButton: ButtonType,
    navigate: (String) -> Unit,
    viewModel: CreatorWizardViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        when (selectedButton) {
            ButtonType.EXERCISE -> {
                ActionButton(
                    Icons.Default.AddCircleOutline,
                    "Dodaj ćwiczenie"
                ) { viewModel.onNewExerciseClick(navigate) }
                Spacer(modifier = Modifier.height(10.dp))
                ActionButton(
                    Icons.Default.EditNote,
                    "Edytuj ćwiczenie"
                ) { viewModel.onEditExerciseClick(navigate) }
            }

            ButtonType.PACKAGE -> {
                ActionButton(
                    Icons.Default.AddCircleOutline,
                    "Dodaj pakiet ćwiczeń"
                ) { viewModel.onNewPackageClick(navigate) }
                Spacer(modifier = Modifier.height(10.dp))
                ActionButton(
                    Icons.Default.EditNote,
                    "Edytuj pakiet ćwiczeń"
                ) { viewModel.onEditPackageWizardClick(navigate) }
            }
        }
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = colorPrimary, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = colorPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, color = colorPrimary, style = typography.labelLarge)
    }
}

enum class ButtonType(val displayName: String) {
    EXERCISE("Ćwiczenia"),
    PACKAGE("Pakiety ćwiczeń")
}

@Preview(showBackground = true)
@Composable
fun PreviewWizardScreen() {
    PhysioTheme {
    }
}
