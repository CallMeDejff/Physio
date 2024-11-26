package com.dawidkubica.physio.screens.forgot_password

import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
) : PhysioAppViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    fun callForgotPassword(email: String, popBackStack: () -> Unit) {
        launchCatching(
            tag = FORGOT_PASSWORD_VIEW_MODEL_TAG,
            errorMessage = "Nie udało się wysłać maila z linkiem do zmiany hasła.",
            onError = { message -> _message.emit(message) },
            block = {
                authenticationService.resetPassword(email)
                _message.emit("Link do zmiany hasła został wysłany na podany adres email.")
                popBackStack()
            }
        )
    }

    fun goBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        private const val FORGOT_PASSWORD_VIEW_MODEL_TAG = "ForgotPasswordViewModel"
    }
}