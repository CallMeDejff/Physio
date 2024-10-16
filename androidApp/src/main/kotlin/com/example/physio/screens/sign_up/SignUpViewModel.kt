package com.example.physio.screens.sign_up

import android.util.Log
import com.example.physio.models.User
import com.example.physio.navigation.AuthScreen
import com.example.physio.navigation.Graph
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : PhysioAppViewModel() {

    var name: String = ""
    var lastname: String = ""
    var email: String = ""
    var repeatedPassword: String = ""
    var password: String = ""
    var licenseNumber: Int = 0
    var userType: Int = 0

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    private val _signupMessage = MutableStateFlow<String?>(null)
    val signupMessage = _signupMessage.asStateFlow()

    fun updateName(name: String) {
        this.name = name
    }

    fun updateLastname(lastname: String) {
        this.lastname = lastname
    }

    fun updateEmail(email: String) {
        this.email = email
    }

    fun updateRepeatedPassword(repeatedPassword: String) {
        this.repeatedPassword = repeatedPassword
    }

    fun updatePassword(password: String) {
        this.password = password
    }

    fun updateLicenseNumber(licenseNumber: Int?) {
        if (licenseNumber != null) {
            this.licenseNumber = licenseNumber
        }
    }

    private fun dataValidation(): Result<Unit> {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            email.isEmpty() -> {
                _signupMessage.update { "Podaj adres email" }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - Email address missing")
                Result.failure(Exception("Email address missing"))
            }

            !email.matches(emailPattern.toRegex()) -> {
                _signupMessage.update { "Sprawdź czy na pewno podałeś prawdziwy adres email" }
                Log.e(
                    SIGNUP_VIEWMODEL_TAG,
                    "dataValidation:failure - Email address pattern mismatch"
                )
                Result.failure(Exception("Email address pattern mismatch"))
            }

            name.isEmpty() -> {
                _signupMessage.update { "Podaj imię" }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - Name missing")
                Result.failure(Exception("Name missing"))
            }

            lastname.isEmpty() -> {
                _signupMessage.update { "Podaj nazwisko" }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - Lastname missing")
                Result.failure(Exception("Lastname missing"))
            }

            password.equals(repeatedPassword).not() -> {
                _signupMessage.update { "Czy na pewno oba hasła są takie same?" }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - passwords mismatch")
                Result.failure(Exception("passwords mismatch"))
            }

            password.isEmpty() -> {
                _signupMessage.update { "Podaj hasło" }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - Password missing")
                Result.failure(Exception("Password missing"))
            }

            else -> {
                Result.success(Unit)
            }
        }
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        val validationResult = dataValidation()
        if (validationResult.isSuccess) {
            _showProgress.update { true }
            callUserSignUp(openAndPopUp)
            Log.d(SIGNUP_VIEWMODEL_TAG, "callUserSignUp:success")
        } else {
            _showProgress.update { false }
            updateRepeatedPassword("")
            updatePassword("")
        }
    }

    private fun callUserSignUp(openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = SIGNUP_VIEWMODEL_TAG,
            errorMessage = "Ups! Tworzenie konta nie powiodło się.",
            onError = { message -> _message.emit(message) },
            block = {
                val resultSignUp = accountService.signUp(email, password)
                if (resultSignUp.isSuccess) {
                    Log.d(SIGNUP_VIEWMODEL_TAG, "resultSignUp:success")
                    val userId = accountService.currentUserId

                    val userType = if (licenseNumber == 0) {
                        0
                    } else {
                        1
                    }
                    val newUser = User(userId, name, lastname, email, licenseNumber, userType)
                    Log.d(SIGNUP_VIEWMODEL_TAG, "callCreateNewUser:$newUser")

                    accountService.createUser(newUser)
                    Log.d(SIGNUP_VIEWMODEL_TAG, "callCreateNewUser:success")
                    openAndPopUp(Graph.HOME, AuthScreen.SignUp.route)
                } else {
                    Log.d(SIGNUP_VIEWMODEL_TAG, "resultSignUp:failed")
                    updateRepeatedPassword("")
                    updatePassword("")
                }
                _showProgress.update { false }
            })
    }

    fun clearSignupMessage() {
        _signupMessage.update { null }
    }

    companion object {
        const val SIGNUP_VIEWMODEL_TAG = "SignUpViewModel"
    }
}