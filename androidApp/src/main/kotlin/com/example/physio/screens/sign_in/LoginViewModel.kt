package com.example.physio.screens.sign_in

import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CustomCredential
import androidx.lifecycle.viewModelScope
import com.example.physio.navigation.AuthScreen
import com.example.physio.navigation.Graph
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageService
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : PhysioAppViewModel() {

    private var email: String = ""
    private var password: String = ""
    val user = accountService.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun setLoading(loading: Boolean) {
        _isLoading.update { loading }
    }

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

    fun onSignInWithGoogle(credential: Credential, openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = LOGIN_VIEW_MODEL_TAG,
            errorMessage = "Logowanie za pomocą konta Google nie powiodło się",
            onError = { message -> _message.emit(message) },
            block = {
                if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)
                    accountService.signInWithGoogle(googleIdTokenCredential.idToken)
                    openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                } else {
                    Log.e(
                        LOGIN_VIEW_MODEL_TAG,
                        "An error occured when logging in with Google account"
                    )
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
                errorMessage = "Ups! Logowanie nie powiodło się.",
                onError = { message -> _message.emit(message) },
                block = {
                    val signInResult = accountService.signIn(email, password)
                    if (signInResult.isSuccess) {
                        val currentUserId = accountService.currentUserId
                        Log.i(
                            LOGIN_VIEW_MODEL_TAG,
                            "onSignInClick: logged in user id: $currentUserId"
                        )

                        if (currentUserId != null) {
                            accountService.getUserInfo(currentUserId)
                            _message.update { "Użytkownik zalogowany" }
                            openAndPopUp(Graph.HOME, AuthScreen.SignIn.route)
                        } else {
                            Log.e(LOGIN_VIEW_MODEL_TAG, "onSignInClick: userId is null after login")
                            _message.update { "Nie udało się pobrać danych użytkownika" }
                        }
                    } else {
                        _message.update { "Logowanie nie powiodło się" }
                    }
                }
            )
        }
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
