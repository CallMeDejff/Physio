package com.dawidkubica.physio.screens.profile

import com.dawidkubica.physio.models.ThemeMode
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
    val accProvider: StateFlow<String> = _provider
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified
    val _licenseNumber = MutableStateFlow("")
    val licenseNumber: StateFlow<String> = _licenseNumber
    val themeMode: StateFlow<ThemeMode> = userPreferences.themeModeFlow
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        _isLoading.update { true }
        fetchUserInformation()
        _isLoading.update { false }
    }

    fun setThemeMode(newTheme: ThemeMode) {
        userPreferences.setThemeMode(newTheme)
    }

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
        launchCatching(
            tag = PROFILE_VIEW_MODEL_TAG,
            block = {
                _isRefreshing.update { true }
                try {
                    fetchUser(accountService, PROFILE_VIEW_MODEL_TAG)
                } finally {
                    _isRefreshing.update { false }
                }
            }
        )
    }

    companion object {
        private const val PROFILE_VIEW_MODEL_TAG = "ProfileViewModel"
    }
}