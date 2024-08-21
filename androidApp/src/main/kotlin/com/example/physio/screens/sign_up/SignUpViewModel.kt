package com.example.physio.screens.sign_up

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

    fun updatePlainPassword(plainPassword: String) {
        this.plainPassword = plainPassword
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

    fun onSignUpClick(
        openAndPopUp: (String, String) -> Unit,
    ) {
        _showProgress.update { true }
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        when {
            repeatedPassword != plainPassword -> {
                _signupMessage.update { "Hasła różnią się od siebie" }
                _showProgress.update { false }
                return
            }

            !email.matches(emailPattern.toRegex()) -> {
                _signupMessage.update { "Sprawdź czy podałeś poprawny adres email" }
                _showProgress.update { false }
                return
            }
        }
        callUserSignUp(openAndPopUp)
    }


    private fun callUserSignUp(
        openAndPopUp: (String, String) -> Unit,
    ) {
        launchCatching {
            accountService.signUp(email, password)
            val userId = accountService.currentUserId
            val newUser = User(userId, name, lastname, licenseNumber)
            storageService.createUser(newUser)

            _signupMessage.update { "Użytkownik utworzony" }
            openAndPopUp(DASHBOARD_SCREEN, SIGN_UP_SCREEN)
        }
    }

    fun clearSignupMessage() {
        _signupMessage.update { null }
    }
}