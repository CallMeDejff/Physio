package com.dawidkubica.physio.screens.favorites

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import com.dawidkubica.physio.models.Category
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.Reminder
import com.dawidkubica.physio.navigation.WizardScreen
import com.dawidkubica.physio.screens.profile.UserSharedViewModel
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.ExercisePackageService
import com.dawidkubica.physio.ui.icons.Clinical_notes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val exercisePackageService: ExercisePackageService,
    private val accountService: AccountService,
    private val userPreferences: UserPreferences
) : UserSharedViewModel() {

    private val _fetchedCategories = MutableStateFlow<List<Category>>(emptyList())
    val fetchedCategories: StateFlow<List<Category>> = _fetchedCategories
    val userType: StateFlow<Int> = _userType
    val userName: StateFlow<String> = _userName
    private val _nextReminder = MutableStateFlow<Reminder?>(null)
    var nextReminder: StateFlow<Reminder?> = _nextReminder

    init {
        fetchUserPackages()
        fetchUserType()
        fetchReminders()
    }

    private fun fetchUserPackages() {
        launchCatching(
            tag = FAVORITES_VIEW_MODEL_TAG,
            block = {
                val userPackages = exercisePackageService.getUserExercisePackages()
                _userFavoritePackagesList.value =
                    userPackages.favoritePackages as List<ExercisePackage>
                _userAssignedPackagesList.value =
                    userPackages.assignedPackages as List<ExercisePackage>
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
        val currentTimeMillis = System.currentTimeMillis()
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTimeMillis

        val remindersWithTimeInMillis = reminders.mapNotNull { reminder ->
            val reminderTimeInMillis = getReminderTimeInMillis(reminder.dayOfWeek, reminder.time)

            reminderTimeInMillis.let {
                val reminderCalendar = Calendar.getInstance()
                val dayOfWeekInt = mapDayOfWeekToCalendar(reminder.dayOfWeek)
                reminderCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeekInt)

                val (hour, minute) = reminder.time.split(":").map { it.toInt() }
                reminderCalendar.set(Calendar.HOUR_OF_DAY, hour)
                reminderCalendar.set(Calendar.MINUTE, minute)
                reminderCalendar.set(Calendar.SECOND, 0)
                reminderCalendar.set(Calendar.MILLISECOND, 0)

                if (reminderCalendar.timeInMillis < currentTimeMillis) {
                    reminderCalendar.add(Calendar.WEEK_OF_YEAR, 1)
                }

                reminder to reminderCalendar.timeInMillis
            }
        }

        val futureReminders = remindersWithTimeInMillis
            .filter { it.second != null && it.second > currentTimeMillis }

        return futureReminders.minByOrNull { it.second }?.first
    }


    private fun getReminderTimeInMillis(dayOfWeek: String, time: String): Long {
        val calendar = Calendar.getInstance()
        val (hour, minute) = time.split(":").map { it.toInt() }

        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dayOfWeekInt = mapDayOfWeekToCalendar(dayOfWeek)
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeekInt)

        return calendar.timeInMillis
    }


    private fun mapDayOfWeekToCalendar(dayOfWeek: String): Int {
        return when (dayOfWeek) {
            "Poniedziałek" -> Calendar.MONDAY
            "Wtorek" -> Calendar.TUESDAY
            "Środa" -> Calendar.WEDNESDAY
            "Czwartek" -> Calendar.THURSDAY
            "Piątek" -> Calendar.FRIDAY
            "Sobota" -> Calendar.SATURDAY
            "Niedziela" -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }


    private fun fetchCategories() {
        _fetchedCategories.value = listOf(
            Category(
                "Ulubione",
                Icons.Outlined.FavoriteBorder,
                "Treść dla kategorii 1",
                _userFavoritePackagesList.value
            ),
            Category(
                "Przypisane",
                Clinical_notes,
                "Treść dla kategorii 2",
                _userAssignedPackagesList.value
            ),
            Category(
                "Premium",
                Clinical_notes,
                "Treść dla kategorii 2"
            ),
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