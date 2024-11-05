package com.example.physio.screens.editUser

import android.util.Log
import com.example.physio.models.User
import com.example.physio.navigation.Graph
import com.example.physio.screens.profile.UserSharedViewModel
import com.example.physio.service.services.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val accountService: AccountService,
) : UserSharedViewModel() {

    val userName: StateFlow<String> = _userName
    val userLastname: StateFlow<String> = _userLastname
    val userType: StateFlow<Int> = _userType
    val userLicenseNumber: StateFlow<Int> = _userLicenseNumber
    val provider: StateFlow<String> = _provider

    fun fetchUserInformation() {
        fetchUser(accountService, EDIT_USER_VIEW_MODEL_TAG)
    }

    fun callUserUpdate(
        name: String,
        lastname: String,
        licenseNumber: Int,
        navigate: (String) -> Unit,
        auth: Boolean = false
    ) {
        launchCatching(
            tag = EDIT_USER_VIEW_MODEL_TAG,
            errorMessage = "Nie udało się zaktualizować danych użytkownika.",
            onError = { message -> _message.emit(message) },
            block = {
                val userId = accountService.currentUserId
                Log.d("UserUpdate", "Pobranie userId: $userId")

                val userType = if (licenseNumber == 0) 0 else 1

                val updatedUser = User(
                    uid = userId,
                    name = name,
                    lastname = lastname,
                    licenseNumber = licenseNumber,
                    userType = userType
                )

                Log.d(EDIT_USER_VIEW_MODEL_TAG, "callUserUpdate: running: $updatedUser")
                accountService.updateUser(updatedUser)
                Log.d(EDIT_USER_VIEW_MODEL_TAG, "callUserUpdate: success")

                if (auth) {
                    Log.d(EDIT_USER_VIEW_MODEL_TAG, "callUserUpdate: running: $updatedUser")
                    accountService.updateUser(updatedUser)
                    Log.d(EDIT_USER_VIEW_MODEL_TAG, "callUserUpdate: success")
                }

                navigate(Graph.PROFILE)
            }
        )
    }

    fun goBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        private const val EDIT_USER_VIEW_MODEL_TAG = "EditUserViewModel"
    }
}