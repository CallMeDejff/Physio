package com.example.physio.screens.sign_in

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.physio.R
import com.example.physio.activities.SignUpActivity
import com.example.physio.ui.PhysioTheme
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.dark_gray
import com.example.physio.ui.ghost_white
import com.example.physio.ui.gray
import com.example.physio.ui.light_gray
import com.example.physio.ui.typography

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun LoginScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val showProgress = viewModel.showProgress.collectAsState()

    LaunchedEffect(viewModel.loginMessage) {
        viewModel.loginMessage.collect { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                passwordState.value = TextFieldValue("")
                viewModel.clearLoginMessage()
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ConstraintLayout {
                val (header, loginForm) = createRefs()

                HeaderView(
                    modifier = Modifier
                        .height(280.dp)
                        .constrainAs(header) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)

                        }
                )

                Card(
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                    colors = CardDefaults.cardColors(containerColor = ghost_white),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 120.dp)
                        .constrainAs(loginForm) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    LoginForm(
                        emailState,
                        passwordState,
                        showProgress.value,
                        onLoginClick = {
                            viewModel.updateEmail(emailState.value.text)
                            viewModel.updatePassword(passwordState.value.text)
                            viewModel.onSignInClick(openAndPopUp)
                        },
                        onSignUpClick = {
                            viewModel.onSignUpClick(openAndPopUp)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LoginForm(
    emailState: MutableState<TextFieldValue>,
    passwordState: MutableState<TextFieldValue>,
    showProgress: Boolean,
    onLoginClick: () -> Unit,
    onSignUpClick: () -> Unit
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
            text = AnnotatedString("Zapomniałem hasła", SpanStyle(color = colorPrimary)),
            onClick = {
                //onForgotPasswordClick()
            },
            style = TextStyle(textAlign = TextAlign.End),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .align(Alignment.End),
        )
        TextButton(
            onClick = onLoginClick,
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
                    text = "Zaloguj",
                    color = Color.White,
                    style = typography.labelLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .padding(top = 6.dp)
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
                    text = "Logowanie FaceBook",
                    color = Color.White,
                    style = typography.labelLarge
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .padding(top = 6.dp)
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
                    text = "Logowanie Google",
                    color = Color.White,
                    style = typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        ClickableText(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Gray, fontFamily = FontFamily(Font(R.font.helvetica_neue_regular)))) {
                    append("Nie posiadasz konta? ")
                }
                withStyle(style = SpanStyle(color = colorPrimary, fontFamily = FontFamily(Font(R.font.helvetica_neue_medium)))) {
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


@Composable
fun LabeledTextField(
    label: String,
    valueState: MutableState<TextFieldValue>,
    placeholder: String,
    leadingIcon: ImageVector,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier
        .fillMaxWidth()
    ) {
        Text(
            text = label,
            style = typography.labelLarge.copy(color = gray),
            modifier = Modifier.padding(bottom = 10.dp, top = 10.dp)
        )
        OutlinedTextField(
            singleLine = true,
            value = valueState.value,
            onValueChange = { valueState.value = it },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = typography.bodyLarge.copy(color = light_gray)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = colorPrimary
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colorPrimary,
                unfocusedIndicatorColor = gray,
            )
        )
    }
}

@Composable
fun HeaderView(modifier: Modifier) {
    val image = painterResource(id = R.drawable.ic_launcher_background)
    Box(modifier = modifier) {
        Image(
            painter = image,
            contentDescription = "Header Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = colorPrimary,
                radius = size.minDimension / 1.5f,
                center = Offset(size.width / 1.2f, size.height / 2)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    PhysioTheme {
    }
}