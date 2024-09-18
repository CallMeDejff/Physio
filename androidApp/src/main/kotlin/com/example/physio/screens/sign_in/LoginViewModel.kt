package com.example.physio.screens.sign_in

import android.util.Log
import com.example.physio.DASHBOARD_SCREEN
import com.example.physio.SIGN_IN_SCREEN
import com.example.physio.SIGN_UP_SCREEN
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageService
import com.google.firebase.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
): PhysioAppViewModel() {

    private var email: String = ""
    private var password: String = ""

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage = _loginMessage.asStateFlow()

    private fun dataValidation(): Result<Unit> {
        return when {
            email.isEmpty() -> {
                _loginMessage.update { "Podaj adres email" }
                Log.e("LoginViewModel", "dataValidation:failure - Email address missing")
                Result.failure(Exception("Email address missing"))
            }
            password.isEmpty() -> {
                _loginMessage.update { "Podaj hasło" }
                Log.e("LoginViewModel", "dataValidation:failure - Password missing")
                Result.failure(Exception("Password missing"))
            }
            else -> {
                Result.success(Unit)
            }
        }
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        val validationResult = dataValidation()
        if (validationResult.isSuccess) {
            _showProgress.update { true }
            launchCatching {
                val signInResult = accountService.signIn(email, password)
                Log.i("LoginViewModel", "onSignInClick:$email")
                if (signInResult.isSuccess) {
                    storageService.getUserInfo(accountService.currentUserId)
                    _loginMessage.update { "Użytkownik zalogowany" }
                    _showProgress.update { false }
                    openAndPopUp(DASHBOARD_SCREEN, SIGN_IN_SCREEN)
                } else {
                    _loginMessage.update { "Logowanie nie powiodło się" }
                    _showProgress.update { false }
                }

                if (BuildConfig.DEBUG) {
                    _loginMessage.update { accountService.currentUserId }
                }
            }
            return
        }

    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp(SIGN_UP_SCREEN, SIGN_IN_SCREEN)
    }

    fun updateEmail(newEmail: String) {
        email = newEmail
    }

    fun updatePassword(newPassword: String) {
        password = newPassword
    }

    fun clearLoginMessage() {
        _loginMessage.update { null }
    }
}