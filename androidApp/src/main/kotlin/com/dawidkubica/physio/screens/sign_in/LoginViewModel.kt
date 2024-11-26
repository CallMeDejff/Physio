package com.dawidkubica.physio.screens.sign_in

import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.navigation.AuthScreen
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.service.AuthError
import com.dawidkubica.physio.service.services.AuthenticationService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authenticationService: AuthenticationService
) : PhysioAppViewModel() {

    private var email: String = ""
    private var password: String = ""

    private fun dataValidation(): Result<Unit> {
        return when {
            email.isEmpty() -> {
                _message.update { "Podaj adres email" }
                Log.e(LOGIN_VIEW_MODEL_TAG, "dataValidation:failure - Email address missing")
                Result.failure(Exception("Email address missing"))
            }

            password.isEmpty() -> {
                _message.update { "Podaj hasło" }
                Log.e(LOGIN_VIEW_MODEL_TAG, "dataValidation:failure - Password missing")
                Result.failure(Exception("Password missing"))
            }

            else -> Result.success(Unit)
        }
    }

    fun onSignInWithFacebook(token: String, openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = LOGIN_VIEW_MODEL_TAG,
            errorMessage = "Ups! Logowanie kontem Facebook nie powiodło się.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.update { true }

                authenticationService.signInWithFacebook(
                    token,
                    onSuccess = {
                        _isLoading.update { false }
                        openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                    },
                    onFailure = { _isLoading.update { false } }
                )
            }
        )
    }

    fun onSignInWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = LOGIN_VIEW_MODEL_TAG,
            errorMessage = "Logowanie za pomocą konta Google nie powiodło się",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.update { true }
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    authenticationService.signInWithGoogle(googleIdTokenCredential.idToken,
                        onSuccess = {
                            _isLoading.update { false }
                            openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                        },
                        onFailure = { _isLoading.update { false } }
                    )
                } else {
                    Log.e(
                        LOGIN_VIEW_MODEL_TAG,
                        "An error occured when logging in with Google account"
                    )
                    _isLoading.update { false }
                }
            }
        )
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit, context: Context) {
        val validationResult = dataValidation()
        if (validationResult.isSuccess) {
            _isLoading.update { true }
            launchCatching(
                tag = LOGIN_VIEW_MODEL_TAG,
                errorMessage = "Ups! Logowanie nie powiodło się.",
                onError = { message -> _message.emit(message) },
                block = {
                    val signInResult = authenticationService.signIn(email, password, context)
                    if (signInResult.isSuccess) {
                        val currentUserId = authenticationService.currentUserId
                        Log.i(
                            LOGIN_VIEW_MODEL_TAG,
                            "onSignInClick: logged in user id: $currentUserId"
                        )
                        _isLoading.update { false }
                        openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                    } else {
                        val authError = signInResult.exceptionOrNull() as? AuthError
                        _message.emit(authError?.message ?: "Logowanie nie powiodło się")
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
