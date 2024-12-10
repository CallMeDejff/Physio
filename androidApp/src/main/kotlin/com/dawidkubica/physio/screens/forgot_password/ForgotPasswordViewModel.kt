package com.dawidkubica.physio.screens.forgot_password

import android.content.Context
import com.dawidkubica.physio.R
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    @ApplicationContext private val context: Context
) : PhysioAppViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    fun callForgotPassword(email: String, popBackStack: () -> Unit) {
        launchCatching(
            tag = FORGOT_PASSWORD_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.forgot_password_error),
            onError = { message -> _message.emit(message) },
            block = {
                authenticationService.resetPassword(email)
                _message.emit(context.getString(R.string.forgot_password_success))
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
