package com.dawidkubica.physio.screens.sign_up

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
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
            text = stringResource(id = R.string.sign_up_prompt),
            textAlign = TextAlign.Center,
            fontSize = 22.sp,
        )
        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = stringResource(id = R.string.email_label),
            valueState = emailState,
            placeholder = stringResource(id = R.string.email_placeholder),
            leadingIcon = Icons.Outlined.Mail,
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = stringResource(id = R.string.password_label),
            valueState = passwordState,
            placeholder = stringResource(id = R.string.password_placeholder),
            leadingIcon = Icons.Outlined.Password,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = stringResource(id = R.string.repeat_password_label),
            valueState = repeatedPasswordState,
            placeholder = stringResource(id = R.string.repeat_password_placeholder),
            leadingIcon = Icons.Outlined.Password,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )
        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = stringResource(id = R.string.name_label),
            valueState = nameState,
            placeholder = stringResource(id = R.string.name_placeholder),
            leadingIcon = Icons.Outlined.AccountBox,
            keyboardType = KeyboardType.Text,
            isPassword = false
        )
        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = stringResource(id = R.string.lastname_label),
            valueState = lastnameState,
            placeholder = stringResource(id = R.string.lastname_placeholder),
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
                colors = CheckboxDefaults.colors(MaterialTheme.colorScheme.primary)
            )

            Text(
                text = stringResource(id = R.string.license_checkbox),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                style = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    fontSize = 16.sp,
                    fontFamily = FontFamily(Font(R.font.helvetica_neue_regular))
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (isLicenseChecked.value) {
            LabeledTextField(
                label = stringResource(id = R.string.license_label),
                valueState = licenseNumberState,
                placeholder = stringResource(id = R.string.license_placeholder),
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
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            if (showProgress) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.size(20.dp)
                )
            } else {
                Text(
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                    text = stringResource(id = R.string.create_account_button),
                    color = Color.White,
                    style = typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(120.dp))
    }
}
