package com.dawidkubica.physio.screens.sign_up

import android.content.Context
import android.util.Log
import com.dawidkubica.physio.R
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.models.Provider
import com.dawidkubica.physio.models.User
import com.dawidkubica.physio.navigation.AuthScreen
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.service.AuthError
import com.dawidkubica.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authenticationService: AuthenticationService,
    @ApplicationContext private val context: Context
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
                _signupMessage.update { context.getString(R.string.signup_missing_email) }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - Email address missing")
                Result.failure(Exception("Email address missing"))
            }

            !email.matches(emailPattern.toRegex()) -> {
                _signupMessage.update { context.getString(R.string.signup_invalid_email) }
                Log.e(
                    SIGNUP_VIEWMODEL_TAG,
                    "dataValidation:failure - Email address pattern mismatch"
                )
                Result.failure(Exception("Email address pattern mismatch"))
            }

            name.isEmpty() -> {
                _signupMessage.update { context.getString(R.string.signup_missing_name) }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - Name missing")
                Result.failure(Exception("Name missing"))
            }

            lastname.isEmpty() -> {
                _signupMessage.update { context.getString(R.string.signup_missing_lastname) }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - Lastname missing")
                Result.failure(Exception("Lastname missing"))
            }

            password.equals(repeatedPassword).not() -> {
                _signupMessage.update { context.getString(R.string.signup_password_mismatch) }
                Log.e(SIGNUP_VIEWMODEL_TAG, "dataValidation:failure - passwords mismatch")
                Result.failure(Exception("passwords mismatch"))
            }

            password.isEmpty() -> {
                _signupMessage.update { context.getString(R.string.signup_missing_password) }
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
            errorMessage = context.getString(R.string.signup_error),
            onError = { message -> _signupMessage.emit(message) },
            block = {
                val resultSignUp = authenticationService.signUp(email, password, context)
                if (resultSignUp.isSuccess) {
                    val userId = authenticationService.currentUserId
                    val userType = if (licenseNumber == 0) 0 else 1

                    val newUser = User(
                        uid = userId,
                        name = name,
                        lastname = lastname,
                        email = email,
                        licenseNumber = licenseNumber,
                        userType = userType,
                        provider = Provider.Physio.providerId
                    )

                    authenticationService.createUser(newUser)
                    openAndPopUp(Graph.HOME, AuthScreen.SignUp.route)
                } else {
                    val authError = resultSignUp.exceptionOrNull() as? AuthError
                    _signupMessage.emit(authError?.message ?: context.getString(R.string.signup_error))
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
