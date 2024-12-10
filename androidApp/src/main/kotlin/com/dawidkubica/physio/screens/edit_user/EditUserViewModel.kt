package com.dawidkubica.physio.screens.edit_user

import android.content.Context
import android.util.Log
import com.dawidkubica.physio.R
import com.dawidkubica.physio.models.User
import com.dawidkubica.physio.screens.profile.UserSharedViewModel
import com.dawidkubica.physio.service.services.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val accountService: AccountService,
    @ApplicationContext private val context: Context
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
        popBackStack: () -> Unit,
        auth: Boolean = false
    ) {
        launchCatching(
            tag = EDIT_USER_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.error_update_user_data),
            onError = { message -> _message.emit(message) },
            block = {
                val userId = accountService.currentUserId
                Log.d(EDIT_USER_VIEW_MODEL_TAG, "Fetching data for userId: $userId")

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

                popBackStack()
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
