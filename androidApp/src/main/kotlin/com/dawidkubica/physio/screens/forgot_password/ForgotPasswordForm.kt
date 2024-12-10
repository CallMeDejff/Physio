package com.dawidkubica.physio.screens.forgot_password

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun ForgotPasswordForm(
    emailState: MutableState<TextFieldValue>,
) {
    val context = LocalContext.current

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
                val title = context.getString(R.string.forgot_password_title)
                val highlightStart = title.indexOf("Physio")
                val highlightEnd = title.length

                val styleNormal = SpanStyle(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_regular))
                )
                val styleHighlight = SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_medium))
                )

                append(title)
                addStyle(styleNormal, 0, highlightStart)
                addStyle(styleHighlight, highlightStart, highlightEnd)
            },
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = context.getString(R.string.forgot_password_description),
            style = typography.labelMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = context.getString(R.string.email_label),
            valueState = emailState,
            placeholder = context.getString(R.string.email_placeholder),
            leadingIcon = Icons.Outlined.Mail,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}
