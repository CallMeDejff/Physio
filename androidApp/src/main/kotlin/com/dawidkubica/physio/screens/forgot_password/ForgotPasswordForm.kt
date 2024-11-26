package com.dawidkubica.physio.screens.forgot_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ForgotPasswordForm(
    emailState: MutableState<TextFieldValue>,
) {

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
                val text = "Resetowanie hasła do konta Physio."
                val styleNormal = SpanStyle(
                    color = dark_gray,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_regular))
                )
                val styleHighlight = SpanStyle(
                    color = colorPrimary,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_medium))
                )
                append("Resetowanie hasła do konta ")
                addStyle(styleNormal, 0, "Resetowanie hasła do konta ".length)

                append("Physio.")
                addStyle(styleHighlight, "Resetowanie hasła do konta ".length, text.length)
            },
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Jeżeli posiadasz konto w aplikacji Physio, na wskazany adres email zostanie wysłany link do zresetowania hasła razem z dalszymi instrukcjami.",
            style = typography.labelMedium,
            textAlign = TextAlign.Center,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = "Adres Email",
            valueState = emailState,
            placeholder = "Adres Email",
            leadingIcon = Icons.Outlined.Mail,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}