package com.example.physio.screens.sign_up

import android.util.Log
import com.example.physio.DASHBOARD_SCREEN
import com.example.physio.SIGN_UP_SCREEN
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.User
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
): PhysioAppViewModel() {

    var name: String = ""
    var lastname: String = ""
    var email: String = ""
    var plainPassword: String = ""
    var repeatedPassword: String = ""
    var password: String = ""
    var licenseNumber: Int = 0
    var userType: String = ""

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

    fun updateUserType(userType: String) {
        this.userType = userType
    }

    private fun dataValidation(): Result<Unit> {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return when {
            email.isEmpty() -> {
                _signupMessage.update { "Podaj adres email" }
                Log.e("SignUpViewModel", "dataValidation:failure - Email address missing")
                Result.failure(Exception("Email address missing"))
            }
            !email.matches(emailPattern.toRegex()) -> {
                _signupMessage.update { "Sprawdź czy na pewno podałeś prawdziwy adres email" }
                Log.e("SignUpViewModel", "dataValidation:failure - Email address pattern mismatch")
                Result.failure(Exception("Email address pattern mismatch"))
            }

            name.isEmpty() -> {
                _signupMessage.update { "Podaj imię" }
                Log.e("SignUpViewModel", "dataValidation:failure - Name missing")
                Result.failure(Exception("Name missing"))
            }

            lastname.isEmpty() -> {
                _signupMessage.update { "Podaj nazwisko" }
                Log.e("SignUpViewModel", "dataValidation:failure - Lastname missing")
                Result.failure(Exception("Lastname missing"))
            }

            password.equals(repeatedPassword).not() -> {
                _signupMessage.update { "Czy na pewno oba hasła są takie same?" }
                updateRepeatedPassword("")
                updatePassword("")
                Log.e("SignUpViewModel", "dataValidation:failure - passwords mismatch:$password:$repeatedPassword")
                Result.failure(Exception("passwords mismatch"))
            }

            password.isEmpty() -> {
                _signupMessage.update { "Podaj hasło" }
                Log.e("SignUpViewModel", "dataValidation:failure - Password missing")
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
            Log.d("SignUpViewModel", "callUserSignUp:success")
        } else {
            _showProgress.update { false }
            updateRepeatedPassword("")
            updatePassword("")
        }
    }

    private fun callUserSignUp(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            val resultSignUp = accountService.signUp(email, password)
            if (resultSignUp.isSuccess) {
                Log.d("SignUpViewModel", "resultSignUp:success")
                val userId = accountService.currentUserId
                val newUser = User(userId, name, lastname, licenseNumber)
                storageService.createUser(newUser)
                _signupMessage.update { "Użytkownik utworzony" }
                openAndPopUp(DASHBOARD_SCREEN, SIGN_UP_SCREEN)
            } else {
                Log.d("SignUpViewModel", "resultSignUp:failed")
                _signupMessage.update { "Rejestracja nie powiodła się" }
                updateRepeatedPassword("")
                updatePassword("")
            }
            _showProgress.update { false }
        }
    }

    fun clearSignupMessage() {
        _signupMessage.update { null }
    }
}