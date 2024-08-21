package com.example.physio.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physio.models.ApiResponse
import com.example.physio.retrofitutil.ApiClient
import com.example.physio.retrofitutil.ApiEncryption
import com.example.physio.retrofitutil.ApiInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.security.GeneralSecurityException

class SignUpViewModel : ViewModel() {

    private val apiEncryption = ApiEncryption()
    private val key = "physio"

    var name: String = ""
    var lastname: String = ""
    var email: String = ""
    var plainPassword: String = ""
    var repeatedPassword: String = ""
    var password: String = ""
    var licenseNumber: String = ""
    var userType: String = ""

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    private val _signupMessage = MutableStateFlow<String?>(null)
    val signupMessage = _signupMessage.asStateFlow()

    @Throws(GeneralSecurityException::class)
    fun performSignUp(context: Context) {
        _showProgress.update { true }

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        when {
            repeatedPassword != plainPassword -> {
                _signupMessage.update { "Sprawdż czy oba hasła nie są różne" }
                _showProgress.update { false }
                return
            }
            !email.matches(emailPattern.toRegex()) -> {
                _signupMessage.update { "Sprawdź czy masz poprawny adres email" }
                _showProgress.update { false }
                return
            }
        }

        try {
            val encryptedPassword = apiEncryption.encrypt(plainPassword, key)
            password = encryptedPassword

            if (licenseNumber.isEmpty()) {
                userType = "1"
                val licenseNumber = "0"
                callUserSignUp(name, lastname, email, password, userType, licenseNumber)
            } else {
                userType = "2"
                callUserSignUp(name, lastname, email, password, userType, licenseNumber)
            }
        } catch (e: GeneralSecurityException) {
            _signupMessage.update { "Ups! Wystąpił problem przy enkrypcji hasła" }
            _showProgress.update { false }
        }
    }

    private fun callUserSignUp(
        name: String,
        lastname: String,
        email: String,
        password: String,
        userType: String,
        licenseNumber: String
    ) {
        viewModelScope.launch {
            val call: Call<ApiResponse?>? = ApiClient.apiClient?.create(ApiInterface::class.java)
                ?.performUserSignIn(name, lastname, email, password, userType, licenseNumber)

            call?.enqueue(object : Callback<ApiResponse?> {
                override fun onResponse(call: Call<ApiResponse?>, response: Response<ApiResponse?>) {
                    _showProgress.update { false }
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        val message = when (apiResponse?.statusCode) {
                            201 -> "Konto utworzone!"
                            501 -> "Sprawdź czy wszystkie pola zostały uzupełnione"
                            502 -> "Użytkownik istnieje"
                            else -> "Ups! Coś poszło nie tak, spróbuj później"
                        }
                        _signupMessage.update { message }
                        Log.e("SignUpViewModel", "Response: ${apiResponse?.status}")
                    } else {
                        _signupMessage.update { "Ups! Coś poszło nie tak, spróbuj później" }
                        Log.e("SignUpViewModel", "Response: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse?>, t: Throwable) {
                    _showProgress.update { false }
                    _signupMessage.update { "Sprawdz połączenie" }
                    Log.e("SignUpViewModel", "Failure: ${t.message}")
                }
            })
        }
    }

    fun clearSignupMessage() {
        _signupMessage.update { null }
    }
}