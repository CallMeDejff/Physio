package com.dawidkubica.physio.screens.edit_user

import android.content.Context
import android.util.Log
import com.dawidkubica.physio.R
import com.dawidkubica.physio.models.User
import com.dawidkubica.physio.screens.profile.UserSharedViewModel
import com.dawidkubica.physio.screens.wizards.services.Validator
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.services.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val accountService: AccountService,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context
) : UserSharedViewModel(userPreferences) {

    val userName: StateFlow<String> = _userName
    val userLastname: StateFlow<String> = _userLastname
    val userType: StateFlow<Int> = _userType
    val userLicenseNumber: StateFlow<Int> = _userLicenseNumber
    val provider: StateFlow<String> = _provider

    private val nameError = MutableStateFlow<String?>(null)
    private val lastnameError = MutableStateFlow<String?>(null)
    private val licenseNumberError = MutableStateFlow<String?>(null)

    fun fetchUserInformation() {
        fetchUser(accountService, EDIT_USER_VIEW_MODEL_TAG)
    }

    private fun validateFields(
        name: String,
        lastname: String,
        licenseNumber: Int,
        onValidationResult: (Boolean) -> Unit
    ) {
        launchCatching(
            tag = EDIT_USER_VIEW_MODEL_TAG,
            block = {
                val isValid = Validator.validateFields(
                    name = name,
                    lastName = lastname,
                    licenseNumber = licenseNumber,
                    nameError = nameError,
                    lastNameError = lastnameError,
                    licenseNumberError = licenseNumberError,
                    showMessage = { message -> _message.update { message } }
                )
                onValidationResult(isValid)
            }
        )
    }

    fun callUserUpdate(
        name: String,
        lastname: String,
        licenseNumber: Int,
        popBackStack: () -> Unit,
        auth: Boolean = false
    ) {
        validateFields(
            name = name,
            lastname = lastname,
            licenseNumber = licenseNumber
        ) { isValid ->
            if (!isValid) {
                _isLoading.update { false }
                return@validateFields
            }

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
    }

    fun goBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        private const val EDIT_USER_VIEW_MODEL_TAG = "EditUserViewModel"
    }
}
