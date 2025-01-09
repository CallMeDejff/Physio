package com.dawidkubica.physio.screens.sign_in

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dawidkubica.physio.R
import com.dawidkubica.physio.screens.sign_in.components.FacebookSignIn
import com.dawidkubica.physio.screens.sign_in.components.GoogleSignIn
import com.dawidkubica.physio.screens.sign_in.components.LabeledTextField
import com.dawidkubica.physio.ui.components.FullScreenLoader
import com.dawidkubica.physio.ui.theme.typography

@Composable
fun LoginForm(
    emailState: MutableState<TextFieldValue>,
    passwordState: MutableState<TextFieldValue>,
    isLoading: Boolean,
    openAndPopUp: (String, String) -> Unit,
    viewModel: LoginViewModel,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .padding(top = 20.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            text = stringResource(id = R.string.login_prompt),
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        LabeledTextField(
            label = stringResource(id = R.string.email_label),
            valueState = emailState,
            placeholder = stringResource(id = R.string.email_placeholder),
            leadingIcon = Icons.Outlined.Mail,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(20.dp))

        LabeledTextField(
            label = stringResource(id = R.string.password_label),
            valueState = passwordState,
            placeholder = stringResource(id = R.string.password_placeholder),
            leadingIcon = Icons.Outlined.Password,
            keyboardType = KeyboardType.Password,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        ClickableText(
            text = AnnotatedString(
                stringResource(id = R.string.forgot_password),
                SpanStyle(color = MaterialTheme.colorScheme.primary, fontSize = 18.sp)
            ),
            onClick = { onForgotPasswordClick() },
            style = TextStyle(textAlign = TextAlign.End),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .align(Alignment.End),
        )

        if (isLoading) {
            FullScreenLoader()
        } else {
            Box(
                modifier = Modifier
            ) {
                Column {
                    TextButton(
                        onClick = onLoginClick,
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            text = stringResource(id = R.string.login_button),
                            color = MaterialTheme.colorScheme.surface,
                            style = typography.labelLarge
                        )

                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = stringResource(id = R.string.login_with),
                        color = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FacebookSignIn(
                        onAuthComplete = { token ->
                            viewModel.onSignInWithFacebook(token, openAndPopUp)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    GoogleSignIn { credential ->
                        viewModel.onSignInWithGoogle(credential, openAndPopUp)
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        ClickableText(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
                        fontSize = 18.sp
                    )
                ) {
                    append(stringResource(id = R.string.no_account) + " ")
                }
                withStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily(Font(R.font.helvetica_neue_medium)),
                        fontSize = 18.sp
                    )
                ) {
                    append(stringResource(id = R.string.sign_up))
                }
            },
            onClick = { onSignUpClick() },
            style = TextStyle(
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(30.dp))
    }
}
