package com.example.physio.screens.editUser

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.physio.models.Provider
import com.example.physio.screens.sign_in.components.HeaderView
import com.example.physio.ui.theme.colorPrimary
import com.example.physio.ui.theme.ghost_white
import com.example.physio.ui.theme.typography

@Composable
fun EditUserScreen(
    popBackStack: () -> Unit,
    navigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditUserViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    val userName by viewModel.userName.collectAsState()
    val userLastname by viewModel.userLastname.collectAsState()
    val userLicenseNumber by viewModel.userLicenseNumber.collectAsState()

    val nameState = remember { mutableStateOf(TextFieldValue(userName)) }
    val lastnameState = remember { mutableStateOf(TextFieldValue(userLastname)) }
    val licenseNumberState =
        remember { mutableStateOf(TextFieldValue(userLicenseNumber.toString())) }

    LaunchedEffect(userName, userLastname, userLicenseNumber) {
        viewModel.fetchUserInformation()
        nameState.value = TextFieldValue(userName)
        lastnameState.value = TextFieldValue(userLastname)
        licenseNumberState.value = TextFieldValue(userLicenseNumber.toString())
    }

    LaunchedEffect(viewModel.message) {
        viewModel.message.collect { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                viewModel.clearMessage()
            }
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colorPrimary)
        }
    } else {
        ConstraintLayout {
            val (header, editUserForm, navigationButtons) = createRefs()

            HeaderView(
                modifier = Modifier
                    .height(320.dp)
                    .fillMaxWidth()
                    .constrainAs(header) {
                        top.linkTo(parent.top)
                    },
                200, 0.7f
            )

            Card(
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                colors = CardDefaults.cardColors(containerColor = ghost_white),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 120.dp)
                    .constrainAs(editUserForm) {
                        top.linkTo(header.bottom)
                        bottom.linkTo(navigationButtons.top)
                    }
            ) {
                EditUserForm(
                    nameState = nameState,
                    lastnameState = lastnameState,
                    licenseNumberState = licenseNumberState,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 8.dp)
                    .constrainAs(navigationButtons) {
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Button(
                    onClick = {
                        viewModel.callUserUpdate(
                            name = nameState.value.text,
                            lastname = lastnameState.value.text,
                            licenseNumber = licenseNumberState.value.text.toInt(),
                            navigate = navigate
                        )
                    },
                    modifier = Modifier
                        .weight(2f),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                        text = "Zapisz zmiany",
                        color = Color.White,
                        style = typography.labelLarge
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))

                Button(
                    onClick = { viewModel.goBackClick(popBackStack) },
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(16.dp),

                    colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
                ) {
                    Text(
                        text = "Cofnij",
                        color = Color.White,
                        style = typography.labelLarge,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(1.dp))
        }
    }
}
