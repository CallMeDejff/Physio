package com.dawidkubica.physio.screens.sign_in

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.dawidkubica.physio.screens.sign_in.components.HeaderView
import com.dawidkubica.physio.ui.theme.PhysioTheme
import com.dawidkubica.physio.ui.theme.ghost_white

@Composable
fun LoginScreen(
    openAndPopUp: (String, String) -> Unit,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val emailState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    val isLoading = viewModel.isLoading.collectAsState()

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                passwordState.value = TextFieldValue("")
                viewModel.clearMessage()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            ConstraintLayout(
                modifier = Modifier.fillMaxSize()
            ) {
                val (header, loginForm) = createRefs()

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
                        .padding(top = 120.dp)
                        .constrainAs(loginForm) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                ) {
                        LoginForm(
                            emailState = emailState,
                            passwordState = passwordState,
                            isLoading = isLoading.value,
                            openAndPopUp = openAndPopUp,
                            onLoginClick = {
                                viewModel.updateEmail(emailState.value.text)
                                viewModel.updatePassword(passwordState.value.text)
                                viewModel.onSignInClick(openAndPopUp, context)
                            },
                            onSignUpClick = { viewModel.onSignUpClick(openAndPopUp) },
                            onForgotPasswordClick = { viewModel.onForgotPasswordClick(navigate = navigate) },
                            viewModel = viewModel,
                        )

                }
            }
        }
    }
}