package com.example.physio.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.physio.activities.MainActivity
import com.example.physio.models.LoginResponse
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

class LoginViewModel : ViewModel() {

    private val apiEncryption = ApiEncryption()
    private val key = "physio"

    var email: String = ""
    var password: String = ""

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    private val _loginMessage = MutableStateFlow<String?>(null)
    val loginMessage = _loginMessage.asStateFlow()

    fun performLogin(context: Context) {
        _showProgress.update { true }
        viewModelScope.launch {
            try {
                val encryptedPassword = apiEncryption.encrypt(password, key)
                val apiService = ApiClient.apiClient?.create(ApiInterface::class.java)

                val call: Call<LoginResponse?>? = apiService?.performUserLogin(email, encryptedPassword)

                call?.enqueue(object : Callback<LoginResponse?> {
                    override fun onResponse(call: Call<LoginResponse?>, response: Response<LoginResponse?>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if (responseBody?.statusCode == 200) {
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                                _loginMessage.update { "Login Successful" }
                            } else if (responseBody?.statusCode == 501) {
                                _loginMessage.update { "Nieprawidłowy email lub hasło" }
                            }
                        } else {
                            _loginMessage.update { "Ups! Coś poszło nie tak..." }
                        }
                        _showProgress.update { false }
                    }

                    override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                        _loginMessage.update { "Sprawdź połączenie z internetem" }
                        _showProgress.update { false }
                    }
                })
            } catch (e: GeneralSecurityException) {
                _loginMessage.update { "Ups! Wystąpił problem przy enkrypcji hasła" }
                _showProgress.update { false }
            }
        }
    }

    fun clearLoginMessage() {
        _loginMessage.update { null }
    }
}