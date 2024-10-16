package com.example.physio.screens.sign_in

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.physio.R
import com.example.physio.screens.sign_in.components.FacebookSignIn
import com.example.physio.screens.sign_in.components.GoogleSignIn
import com.example.physio.screens.sign_in.components.LabeledTextField
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.dark_gray
import com.example.physio.ui.theme.typography

@Composable
fun LoginForm(
    emailState: MutableState<TextFieldValue>,
    passwordState: MutableState<TextFieldValue>,
    isLoading: Boolean,
    openAndPopUp: (String, String) -> Unit,
    viewModel: LoginViewModel,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit,
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
        Spacer(modifier = Modifier.height(10.dp))
        ClickableText(
            text = AnnotatedString(
                "Zapomniałem hasła",
                SpanStyle(color = colorPrimary, fontSize = 18.sp)
            ),
            onClick = {
                //onForgotPasswordClick()
            },
            style = TextStyle(textAlign = TextAlign.End),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .align(Alignment.End),
        )

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
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
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                            text = "Zaloguj",
                            color = Color.White,
                            style = typography.labelLarge
                        )

                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "lub zaloguj się za pomocą:",
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FacebookSignIn()

                    Spacer(modifier = Modifier.height(10.dp))

                    GoogleSignIn { credential ->
                        viewModel.onSignInWithGoogle(credential, openAndPopUp)
                    }

                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ClickableText(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.Gray,
                        fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)),
                        fontSize = 18.sp
                    )
                ) {
                    append("Nie posiadasz konta? ")
                }
                withStyle(
                    style = SpanStyle(
                        color = colorPrimary,
                        fontFamily = FontFamily(Font(R.font.helvetica_neue_medium)),
                        fontSize = 18.sp
                    )
                ) {
                    append("Zarejestruj się")
                }
            },
            onClick = { onSignUpClick() },
            style = TextStyle(fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(30.dp))
    }
}