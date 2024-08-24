package com.example.physio.screens.sign_up

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Password
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physio.R
import com.example.physio.screens.sign_in.HeaderView
import com.example.physio.screens.sign_in.LabeledTextField
import com.example.physio.ui.PhysioTheme
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.dark_gray
import com.example.physio.ui.ghost_white
import com.example.physio.ui.typography

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val lastnameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val repeatedPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val licenseNumberState = remember { mutableStateOf(TextFieldValue()) }
    val showProgress = viewModel.showProgress.collectAsState()


    LaunchedEffect(viewModel.signupMessage) {
        viewModel.signupMessage.collect { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                passwordState.value = TextFieldValue("")
                viewModel.clearSignupMessage()
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ConstraintLayout {
                val (header, signupForm) = createRefs()

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
                    colors = CardDefaults.cardColors(
                        containerColor = ghost_white
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 120.dp)
                        .constrainAs(signupForm) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)

                        }
                ) {
                    SignUpForm(
                        emailState,
                        passwordState,
                        repeatedPasswordState,
                        nameState,
                        lastnameState,
                        licenseNumberState,
                        showProgress.value,
                        onSignUpClick = {
                            viewModel.updateEmail(emailState.value.text)
                            viewModel.updatePassword(passwordState.value.text)
                            viewModel.updateRepeatedPassword(repeatedPasswordState.value.text)
                            viewModel.updateName(nameState.value.text)
                            viewModel.updateLastname(lastnameState.value.text)
                            viewModel.updateLicenseNumber(licenseNumberState.value.text.toIntOrNull())
                            viewModel.onSignUpClick(openAndPopUp)
                        }
                    )
                }
            }
        }
    }
}

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
    val context = LocalContext.current
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
                }
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

@Preview(showBackground = true)
@Composable
fun PreviewSignupScreen() {
    PhysioTheme {
        SignUpScreen({ _, _ -> })
    }
}