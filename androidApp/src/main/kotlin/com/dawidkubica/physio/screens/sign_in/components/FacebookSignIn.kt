package com.dawidkubica.physio.screens.sign_in.components

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton


@Composable
fun FacebookSignIn(
    onAuthComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val callbackManager = CallbackManager.Factory.create()

    Box(modifier = modifier.fillMaxWidth()) {
        Button(
            onClick = {
                Log.d("FacebookSignIn", "Starting Facebook login")
            },
            modifier = modifier
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
                imageVector = Icons.Default.Facebook,
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                contentDescription = "Facebook Sign In button",
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 8.dp)
                    .size(28.dp),
            )
        }

        AndroidView(
            factory = { ctx ->
                LoginButton(ctx).apply {
                    setPermissions("email", "public_profile")
                    registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                        override fun onCancel() {
                            Log.d("FacebookSignIn", "Facebook login cancelled by the user.")
                        }

                        override fun onError(error: FacebookException) {
                            Log.e(
                                "FacebookSignIn",
                                "Error during Facebook login: ${error.message}",
                                error
                            )
                        }

                        override fun onSuccess(result: LoginResult) {
                            val token = result.accessToken.token
                            Log.d("FacebookSignIn", "Facebook login success. Access token: $token")
                            onAuthComplete(token)
                        }
                    })
                }
            },
            modifier = Modifier
                .matchParentSize()
                .alpha(0.0f)
        )
    }
}




