package com.dawidkubica.physio.screens.change_password

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawidkubica.physio.R
import com.dawidkubica.physio.screens.sign_in.components.LabeledTextField
import com.dawidkubica.physio.ui.theme.colorPrimary
import com.dawidkubica.physio.ui.theme.dark_gray

@Composable
fun ReauthenticateForm(
    emailState: MutableState<TextFieldValue>,
    passwordState: MutableState<TextFieldValue>,
    navigate: (String) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 60.dp)
            .wrapContentHeight(Alignment.Top)
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp),
            text = buildAnnotatedString {
                val text = "Zaloguj się do swojego konta."
                val styleNormal = SpanStyle(
                    color = dark_gray,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_regular))
                )
                val styleHighlight = SpanStyle(
                    color = colorPrimary,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_medium))
                )
                append("Zaloguj się do ")
                addStyle(styleNormal, 0, "Zaloguj się do ".length)

                append("swojego konta.")
                addStyle(styleHighlight, "Zaloguj się do ".length, text.length)
            },
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))
        LabeledTextField(
            label = "Adres Email",
            valueState = emailState,
            placeholder = "Adres Email",
            leadingIcon = Icons.Outlined.Mail,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(20.dp))
        LabeledTextField(
            label = "Hasło",
            valueState = passwordState,
            placeholder = "Hasło",
            leadingIcon = Icons.Outlined.Password,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )
    }
    Spacer(modifier = Modifier.height(10.dp))
}