package com.dawidkubica.physio.screens.sign_up

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawidkubica.physio.R
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.screens.sign_in.components.HeaderView

@Composable
fun SignUpScreen(
    openAndPopUp: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val nameState = remember { mutableStateOf(TextFieldValue()) }
    val lastnameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val repeatedPasswordState = remember { mutableStateOf(TextFieldValue()) }
    val licenseNumberState = remember { mutableStateOf(TextFieldValue()) }
    val showProgress = viewModel.showProgress.collectAsState()
    val showEmailVerificationDialog = viewModel.showEmailVerificationDialog.collectAsState()

    BackHandler {
        openAndPopUp(Graph.AUTHENTICATION, Graph.AUTHENTICATION)
    }

    if (showEmailVerificationDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openAndPopUp(Graph.AUTHENTICATION, Graph.AUTHENTICATION)
                viewModel.dismissEmailVerificationDialog()
            },
            title = { Text(text = stringResource(R.string.verification_required_title)) },
            text = {
                Text(text = stringResource(R.string.verification_required_message))
            },
            confirmButton = {
                Button(onClick = {
                    openAndPopUp(Graph.AUTHENTICATION, Graph.AUTHENTICATION)
                    viewModel.dismissEmailVerificationDialog()
                }) {
                    Text(text = stringResource(R.string.ok))
                }
            }
        )
    }

    LaunchedEffect(viewModel.signupMessage) {
        viewModel.signupMessage.collect { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                passwordState.value = TextFieldValue("")
                repeatedPasswordState.value = TextFieldValue("")
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
                        },
                    160, 0.8f
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
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