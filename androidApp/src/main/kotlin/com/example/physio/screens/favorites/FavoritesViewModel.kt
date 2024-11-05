package com.example.physio.screens.favorites

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.physio.models.ExercisePackage
import com.example.physio.navigation.WizardScreen
import com.example.physio.core.PhysioAppViewModel
import com.example.physio.models.Category
import com.example.physio.models.Reminder
import com.example.physio.screens.profile.UserSharedViewModel
import com.example.physio.service.UserPreferences
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.ui.icons.Clinical_notes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val exercisePackageService: ExercisePackageService,
    private val accountService: AccountService,
    private val userPreferences: UserPreferences
) : UserSharedViewModel() {

    private val fetchedFavorites = MutableStateFlow<List<ExercisePackage>>(emptyList())
    private val fetchedAssigned = MutableStateFlow<List<ExercisePackage>>(emptyList())
    private val _fetchedCategories = MutableStateFlow<List<Category>>(emptyList())
    val fetchedCategories: StateFlow<List<Category>> = _fetchedCategories
    val userType: StateFlow<Int> = _userType
    val userName: StateFlow<String> = _userName
    private val _nextReminder = MutableStateFlow<Reminder?>(null)
    var nextReminder: StateFlow<Reminder?> = _nextReminder

    fun initializer() {
        fetchUserPackages()
        fetchUserType()
        fetchReminders()
    }

    private fun fetchUserPackages() {
        launchCatching(
            tag = FAVORITES_VIEW_MODEL_TAG,
            block = {
                val userPackages = exercisePackageService.getUserExercisePackages()
                _userFavoritePackagesList.value = userPackages.favoritePackages as List<ExercisePackage>
                _userAssignedPackagesList.value = userPackages.assignedPackages as List<ExercisePackage>
                fetchCategories()
            }
        )
    }

    fun onAddExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreatorWizard.route)
    }

    private fun fetchReminders() {
        launchCatching(
            tag = FAVORITES_VIEW_MODEL_TAG,
            block = {
                val reminders = accountService.getRemindersForUser()
                _reminders.value = reminders
                _nextReminder.value = getNextReminder(reminders)
            }
        )
    }

    private fun getNextReminder(reminders: List<Reminder>): Reminder? {
        val currentTime = Calendar.getInstance().timeInMillis

        val remindersWithTimeInMillis = reminders.mapNotNull { reminder ->
            val reminderTimeInMillis = getReminderTimeInMillis(reminder.dayOfWeek, reminder.time)
            reminderTimeInMillis?.takeIf { it > currentTime }?.let { reminder to it }
        }

        return remindersWithTimeInMillis.minByOrNull { it.second }?.first
    }

    private fun getReminderTimeInMillis(dayOfWeek: String, time: String): Long? {
        val calendar = Calendar.getInstance()
        val dayOfWeekInt = when (dayOfWeek) {
            "Poniedziałek" -> Calendar.MONDAY
            "Wtorek" -> Calendar.TUESDAY
            "Środa" -> Calendar.WEDNESDAY
            "Czwartek" -> Calendar.THURSDAY
            "Piątek" -> Calendar.FRIDAY
            "Sobota" -> Calendar.SATURDAY
            "Niedziela" -> Calendar.SUNDAY
            else -> return null
        }

        calendar.apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeekInt)
            set(Calendar.HOUR_OF_DAY, time.split(":")[0].toInt())
            set(Calendar.MINUTE, time.split(":")[1].toInt())
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.WEEK_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }

    private fun fetchCategories() {
        _fetchedCategories.value = listOf(
            Category(
                "Ulubione pakiety",
                Icons.Outlined.FavoriteBorder,
                "Treść dla kategorii 1",
                _userFavoritePackagesList.value
            ),
            Category(
                "Przypisane pakiety",
                Clinical_notes,
                "Treść dla kategorii 2",
                _userAssignedPackagesList.value
            ),
            Category("Kategoria 2", Clinical_notes, "Treść dla kategorii 2"),
        )
        Log.d(FAVORITES_VIEW_MODEL_TAG, "Fetched categories: ${_fetchedCategories.value}")
    }

    private fun fetchUserType() {
        _userType.value = userPreferences.getUserType()
        _userName.value = userPreferences.getUserName()
    }

    companion object {
        private const val FAVORITES_VIEW_MODEL_TAG = "FavoritesViewModel"
    }
}