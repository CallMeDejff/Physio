package com.dawidkubica.physio.screens.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.Reminder
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.ExercisePackageService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

abstract class UserSharedViewModel : PhysioAppViewModel() {

    protected val _userName = MutableStateFlow("")
    protected val _userLastname = MutableStateFlow("")
    protected val _userEmail = MutableStateFlow("")
    protected val _userType = MutableStateFlow(0)
    protected val _userLicenseNumber = MutableStateFlow(0)
    protected val _userAssignedPackages = MutableStateFlow<List<String>>(emptyList())
    protected val _userFavoritePackages = MutableStateFlow<List<String>>(emptyList())
    protected val _isEmailVerified = MutableStateFlow(false)
    protected val _provider = MutableStateFlow("")
    protected val _userAssignedPackagesList = MutableStateFlow<List<ExercisePackage>>(emptyList())
    protected val _discoverPackagesList = MutableStateFlow<List<ExercisePackage>>(emptyList())
    val discoverPackagesList: MutableStateFlow<List<ExercisePackage>> = _discoverPackagesList
    val _userFavoritePackagesList = MutableStateFlow<List<ExercisePackage>>(emptyList())
    val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>> = _reminders

    open fun fetchUserPackages(exercisePackageService: ExercisePackageService, tag: String) {
        launchCatching(
            tag = tag,
            block = {
                val userPackages = exercisePackageService.getUserExercisePackages()
                _userFavoritePackagesList.value =
                    userPackages.favoritePackages as List<ExercisePackage>
                _userAssignedPackagesList.value =
                    userPackages.assignedPackages as List<ExercisePackage>
            }
        )
    }

    fun fetchReminders(accountService: AccountService, tag: String) {
        viewModelScope.launch {
            val remindersList = accountService.getRemindersForUser()
            _reminders.value = remindersList
        }
    }

    fun fetchUser(accountService: AccountService, tag: String) {
        launchCatching(
            tag = tag,
            errorMessage = "Nie udało się pobrać danych użytkownika",
            block = {
                val user = accountService.getUserInfo()
                _userName.value = user?.name ?: ""
                _userLastname.value = user?.lastname ?: ""
                _userEmail.value = user?.email ?: ""
                _userType.value = user?.userType ?: 0
                _userLicenseNumber.value = user?.licenseNumber ?: 0
                _userAssignedPackages.value = user?.assignedPackages ?: emptyList()
                _userFavoritePackages.value = user?.favoritePackages ?: emptyList()
                _provider.value = user?.provider ?: ""
            }
        )
    }


}