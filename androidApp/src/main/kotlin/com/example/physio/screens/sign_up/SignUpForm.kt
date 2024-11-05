package com.example.physio.screens.sign_up

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.physio.R
import com.example.physio.screens.sign_in.components.LabeledTextField
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.dark_gray
import com.example.physio.ui.theme.typography

@Composable
fun SignUpForm(
    emailState: MutableState<TextFieldValue>,
    passwordState: MutableState<TextFieldValue>,
    repeatedPasswordState: MutableState<TextFieldValue>,
    nameState: MutableState<TextFieldValue>,
    lastnameState: MutableState<TextFieldValue>,
    licenseNumberState: MutableState<TextFieldValue>,
    showProgress: Boolean,
    onSignUpClick: () -> Unit,
) {
    val isLicenseChecked = remember { mutableStateOf(false) }

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
                val text = "Utwórz nowe konto."
                val styleNormal = SpanStyle(
                    color = dark_gray,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_regular))
                )
                val styleHighlight = SpanStyle(
                    color = colorPrimary,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_medium))
                )
                append("Utwórz nowe ")
                addStyle(styleNormal, 0, "Utwórz nowe ".length)

                append("konto.")
                addStyle(styleHighlight, "Utwórz nowe ".length, text.length)
            },
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledTextField(
            label = "Adres Email",
            valueState = emailState,
            placeholder = "Adres Email",
            leadingIcon = Icons.Outlined.Mail,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledTextField(
            label = "Hasło",
            valueState = passwordState,
            placeholder = "Hasło",
            leadingIcon = Icons.Outlined.Password,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledTextField(
            label = "Powtórz hasło",
            valueState = repeatedPasswordState,
            placeholder = "Powtórz hasło",
            leadingIcon = Icons.Outlined.Password,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledTextField(
            label = "Imię",
            valueState = nameState,
            placeholder = "Imię",
            leadingIcon = Icons.Outlined.AccountBox,
            keyboardType = KeyboardType.Text,
            isPassword = false
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledTextField(
            label = "Nazwisko",
            valueState = lastnameState,
            placeholder = "Nazwisko",
            leadingIcon = Icons.Outlined.Person,
            keyboardType = KeyboardType.Text,
            isPassword = false
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isLicenseChecked.value,
                onCheckedChange = { isChecked ->
                    isLicenseChecked.value = isChecked
                },
                colors = CheckboxDefaults.colors(colorPrimary)
            )

            Text(
                text = "Jestem fizjoterapeutą zarejestrowanym w KIF",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_regular))
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLicenseChecked.value) {
            LabeledTextField(
                label = "Numer licencji",
                valueState = licenseNumberState,
                placeholder = "Numer licencji",
                leadingIcon = Icons.Outlined.Badge,
                keyboardType = KeyboardType.Number,
                isPassword = false
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onSignUpClick,
            modifier = Modifier
                .padding(top = 30.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
        ) {
            if (showProgress) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
            } else {
                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    text = "Utwórz konto",
                    color = Color.White,
                    style = typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(120.dp))

    }
}