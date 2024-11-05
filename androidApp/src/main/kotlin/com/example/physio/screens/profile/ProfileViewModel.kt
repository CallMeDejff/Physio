package com.example.physio.screens.profile

import com.example.physio.navigation.Graph
import com.example.physio.service.UserPreferences
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
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

    fun onLogoutClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching(
            tag = PROFILE_VIEW_MODEL_TAG,
            block = {
                authenticationService.signOut()
                userPreferences.clearData()
                openAndPopUp(Graph.AUTHENTICATION, Graph.PROFILE)
            })
    }

    fun callEmailChangeLogout(newEmail : String, openAndPopUp: (String, String) -> Unit) {
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

    fun onEditUserClick(navigate: (String) -> Unit) {
        navigate(com.example.physio.navigation.ProfileScreen.EditUser.route)
    }

    fun fetchUserInformation() {
        fetchUser(accountService, PROFILE_VIEW_MODEL_TAG)
    }

    companion object {
        private const val PROFILE_VIEW_MODEL_TAG = "ProfileViewModel"
    }
}