package com.dawidkubica.physio.screens.sign_in

import android.content.Context
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.dawidkubica.physio.R
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.navigation.AuthScreen
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.service.AuthError
import com.dawidkubica.physio.service.services.AuthenticationService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    @ApplicationContext private val context: Context
) : PhysioAppViewModel() {

    private var email: String = ""
    private var password: String = ""

    private fun dataValidation(): Result<Unit> {
        return when {
            email.isEmpty() -> {
                _message.update { context.getString(R.string.login_missing_email) }
                Result.failure(Exception("Email address missing"))
            }
            password.isEmpty() -> {
                _message.update { context.getString(R.string.login_missing_password) }
                Result.failure(Exception("Password missing"))
            }
            else -> Result.success(Unit)
        }
    }

    fun onSignInWithFacebook(token: String, openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = LOGIN_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.facebook_login_failed),
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.update { true }

                authenticationService.signInWithFacebook(
                    token = token,
                    onSuccess = {
                        _isLoading.update { false }
                        openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                    },
                    context = context,
                    onFailure = { exception ->
                        val errorMessage = if (exception is AuthError) {
                            exception.message
                        } else {
                            context.getString(R.string.facebook_login_failed)
                        }
                        _message.update { errorMessage }
                        _isLoading.update { false }
                    }
                )
            }
        )
    }

    fun onSignInWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = LOGIN_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.google_login_failed),
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.update { true }
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    authenticationService.signInWithGoogle(
                        context = context,
                        token = googleIdTokenCredential.idToken,
                        onSuccess = {
                            _isLoading.update { false }
                            openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                        },
                        onFailure = { throwable ->
                            val errorMessage = if (throwable is AuthError) {
                                throwable.message
                            } else {
                                context.getString(R.string.google_login_failed)
                            }
                            _message.update { errorMessage }
                            _isLoading.update { false }
                        }
                    )
                } else {
                    _isLoading.update { false }
                }
            }
        )
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        val validationResult = dataValidation()
        if (validationResult.isSuccess) {
            _isLoading.update { true }
            launchCatching(
                tag = LOGIN_VIEW_MODEL_TAG,
                errorMessage = context.getString(R.string.login_failed),
                onError = { message -> _message.emit(message) },
                block = {
                    val signInResult = authenticationService.signInWithEmailVerification(email, password, context, true)
                    if (signInResult.isSuccess) {
                        _isLoading.update { false }
                        openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                    } else {
                        val authError = signInResult.exceptionOrNull() as? AuthError
                        _message.emit(authError?.message ?: context.getString(R.string.login_failed))
                        _isLoading.update { false }
                    }
                }
            )
        }
    }

    fun onForgotPasswordClick(navigate: (String) -> Unit) {
        navigate(AuthScreen.ForgotPassword.route)
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(AuthScreen.SignUp.route, AuthScreen.SignIn.route)
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    companion object {
        private const val LOGIN_VIEW_MODEL_TAG = "LoginViewModel"
    }
}

