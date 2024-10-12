package com.example.physio.screens.sign_in.components

import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.physio.R
import com.example.physio.screens.sign_in.LoginViewModel
import com.example.physio.ui.colorPrimary
import com.example.physio.ui.typography
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun GoogleSignIn(
    viewModel: LoginViewModel,
    onSuccessfulProviderLogin: () -> Unit
) {
    val context = LocalContext.current

    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            viewModel.launchCatching(
                block = {
                    viewModel.updateUser(result.user)
                    onSuccessfulProviderLogin()
                },
                errorMessage = "Nie udało się zalogować kontem Google",
                tag = "GoogleSignIn"
            )
        },
        onAuthError = {
            viewModel.launchCatching(
                block = {
                    viewModel.clearUser()
                },
                errorMessage = "Nie udało się zalogować kontem Google",
                tag = "GoogleSignInError"
            )
        }
    )

    val token = stringResource(R.string.default_web_client_id)

    Button(
        onClick = {
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(token)
                    .requestEmail()
                    .build()
            val googleSignInClient =
                com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso)
            launcher.launch(googleSignInClient.signInIntent)
            //viewModel.setLoading(true)
        },
        modifier = Modifier
            .padding(top = 6.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = colorPrimary)
    ) {
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            text = "Logowanie Google",
            color = Color.White,
            style = typography.labelLarge
        )
    }
}

@Composable
fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(StartActivityForResult()) { result ->
        val task =
            com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                try {
                    val authResult = Firebase.auth.signInWithCredential(credential).await()
                    onAuthComplete(authResult)
                } catch (e: Exception) {
                    onAuthError(e as ApiException)
                } finally {
                    //viewModel.setLoading(false)
                }
            }
        } catch (e: ApiException) {
            //viewModel.setLoading(false)
            onAuthError(e)
        }
    }
}