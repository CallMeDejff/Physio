package com.dawidkubica.physio.screens.sign_in.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.dawidkubica.physio.R
import com.dawidkubica.physio.ui.icons.GoogleIcon
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import kotlinx.coroutines.launch

@Composable
fun GoogleSignIn(
    modifier: Modifier = Modifier,
    onGetCredentialResponse: (Credential) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val credentialManager = CredentialManager.create(context)

    val token = stringResource(R.string.default_web_client_id)

    Button(
        onClick = {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(token)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            coroutineScope.launch {
                try {
                    Log.d("OneTapGoogleSignIn", "Starting One Tap")
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )
                    onGetCredentialResponse(result.credential)
                } catch (e: GetCredentialException) {
                    Log.d("OneTapGoogleSignIn", "An error occured:$e")
                }
            }
        },
        modifier = Modifier
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                shape = RoundedCornerShape(16.dp)
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Icon(
            imageVector = GoogleIcon,
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            contentDescription = "Google Sign In button",
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp)
                .size(24.dp),
        )
    }
}