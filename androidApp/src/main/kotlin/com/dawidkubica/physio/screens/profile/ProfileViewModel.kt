package com.dawidkubica.physio.screens.profile

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService,
    private val authenticationService: AuthenticationService,
    private var userPreferences: UserPreferences
) : UserSharedViewModel() {

    val userName: StateFlow<String> = _userName
    val userLastname: StateFlow<String> = _userLastname
    val userEmail: StateFlow<String> = _userEmail
    val userType: StateFlow<Int> = _userType
    val userLicenseNumber: StateFlow<Int> = _userLicenseNumber
    val userAssignedPackages: StateFlow<List<String>> = _userAssignedPackages
    val userFavoritePackages: StateFlow<List<String>> = _userFavoritePackages
    val accProvider: StateFlow<String> = _provider
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified
    val _licenseNumber = MutableStateFlow("")
    val licenseNumber: StateFlow<String> = _licenseNumber

    fun onLogoutClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = PROFILE_VIEW_MODEL_TAG,
            block = {
                authenticationService.signOut()
                userPreferences.clearData()
                openAndPopUp(Graph.AUTHENTICATION, Graph.PROFILE)
            })
    }

    fun callEmailChangeLogout(newEmail: String, openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = PROFILE_VIEW_MODEL_TAG,
            block = {
                authenticationService.updateEmail(newEmail)
                authenticationService.signOut()
                userPreferences.clearData()
                openAndPopUp(Graph.AUTHENTICATION, Graph.PROFILE)
            })
    }

    fun callEmailVerification() {
        launchCatching(
            tag = PROFILE_VIEW_MODEL_TAG,
            errorMessage = "Ups! Nie udało się wysłać maila weryfikacyjnego.",
            block = {
                authenticationService.verifyEmail()
                _message.emit("Mail weryfikacyjny został wysłany")
            }
        )
    }

    fun callLicenseCheck(licenseNumber: String = "31368") {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val url = "https://kif.info.pl/CRM/rejestr/search_crm.php"

                    val client = OkHttpClient()
                    val requestForCookie = okhttp3.Request.Builder()
                        .url(url)
                        .build()

                    client.newCall(requestForCookie).execute().use { response ->
                        if (!response.isSuccessful) throw IOException("Unexpected code $response")

                        val cookies = response.headers("Set-Cookie")
                        val cookieHeader = cookies.joinToString("; ")

                        val formBody = FormBody.Builder()
                            .add("imie", null.toString())
                            .add("nazwisko", null.toString())
                            .add("numer", licenseNumber)
                            .build()

                        val requestWithCookie = okhttp3.Request.Builder()
                            .url(url)
                            .addHeader("Cookie", cookieHeader)
                            .post(formBody)
                            .build()

                        client.newCall(requestWithCookie).execute().use { responseWithCookie ->
                            if (!responseWithCookie.isSuccessful) throw IOException("Unexpected code $responseWithCookie")

                            val responseBody = responseWithCookie.body?.string()
                            withContext(Dispatchers.Main) {
                                Log.d(PROFILE_VIEW_MODEL_TAG, "Response: $responseBody")
                                println(responseBody)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(PROFILE_VIEW_MODEL_TAG, "Error: ${e.message}", e)
            }
        }
    }

    fun callUserDelete(openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = PROFILE_VIEW_MODEL_TAG,
            errorMessage = "Ups! Nie udało się usunąć konta.",
            block = {
                authenticationService.deleteAccount()
                openAndPopUp(Graph.AUTHENTICATION, Graph.PROFILE)
            }
        )
    }

    fun onChangePasswordClick(navigate: (String) -> Unit) {
        navigate(com.dawidkubica.physio.navigation.ProfileScreen.ChangePasswordReauthorization.route)
    }

    fun onEditUserClick(navigate: (String) -> Unit) {
        navigate(com.dawidkubica.physio.navigation.ProfileScreen.EditUser.route)
    }

    fun fetchUserInformation() {
        fetchUser(accountService, PROFILE_VIEW_MODEL_TAG)
    }

    companion object {
        private const val PROFILE_VIEW_MODEL_TAG = "ProfileViewModel"
    }
}