package com.example.physio.screens.sign_in

import android.util.Log
import com.example.physio.DASHBOARD_SCREEN
import com.example.physio.SIGN_IN_SCREEN
import com.example.physio.SIGN_UP_SCREEN
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.google.firebase.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService
): PhysioAppViewModel() {

    var email: String = ""
    var password: String = ""

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage = _loginMessage.asStateFlow()

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            Log.i("LoginViewModel", "onSignInClick: $email, $password")
            accountService.signIn(email, password)
            _showProgress.update { true }

            if (BuildConfig.DEBUG) {
                _loginMessage.update { accountService.currentUserId }
            }

            openAndPopUp(DASHBOARD_SCREEN, SIGN_IN_SCREEN)
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