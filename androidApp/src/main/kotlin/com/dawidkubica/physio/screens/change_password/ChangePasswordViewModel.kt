package com.dawidkubica.physio.screens.change_password

import android.content.Context
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.service.AuthError
import com.dawidkubica.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
) : PhysioAppViewModel() {

    private val newPassword = MutableStateFlow("")
    val _newPassword: StateFlow<String> = newPassword
    private val repeatedPassword = MutableStateFlow("")
    val _repeatedPassword: StateFlow<String> = repeatedPassword

    fun callReathentication(
        email: String,
        password: String,
        context: Context,
        navigate: (String) -> Unit
    ) {
        launchCatching(
            tag = CHANGE_PASSWORD_VIEW_MODEL_TAG,
            errorMessage = "Nie udało się zalogować ponownie.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.update { true }
                val authResult = authenticationService.signIn(email, password, context)
                if (authResult.isSuccess) {
                    navigate(com.dawidkubica.physio.navigation.ProfileScreen.ChangePassword.route)
                    _isLoading.update { false }
                } else {
                    val authError = authResult.exceptionOrNull() as? AuthError
                    _message.emit(authError?.message ?: "Logowanie nie powiodło się")
                    _isLoading.update { false }
                }
            }
        )
    }


    fun callChangePassword(
        newPassword: String,
        repeatedPassword: String,
        popBackStack: () -> Unit
    ) {
        launchCatching(
            tag = CHANGE_PASSWORD_VIEW_MODEL_TAG,
            errorMessage = "Nie udało się zaktualizować hasła.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.update { true }
                if (newPassword != repeatedPassword) {
                    _message.emit("Hasła nie są takie same.")
                    _isLoading.update { false }
                    return@launchCatching
                } else {
                    val callStatus = authenticationService.changePassword(newPassword)
                    if (callStatus.isSuccess) {
                        _message.emit("Hasło zostało zmienione.")
                        _isLoading.update { false }
                        popBackStack()
                    } else {
                        _message.emit("Nie udało się zmienić hasła.")
                        _isLoading.update { false }
                        popBackStack()
                    }
                }
            }
        )
    }

    fun goBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        private const val CHANGE_PASSWORD_VIEW_MODEL_TAG = "ChangePasswordViewModel"
    }
}
