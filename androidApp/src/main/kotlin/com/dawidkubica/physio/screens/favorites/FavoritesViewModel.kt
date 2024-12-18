package com.dawidkubica.physio.screens.favorites

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dawidkubica.physio.R
import com.dawidkubica.physio.models.Category
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.Reminder
import com.dawidkubica.physio.navigation.WizardScreen
import com.dawidkubica.physio.screens.profile.UserSharedViewModel
import com.dawidkubica.physio.service.UserPreferences
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.ExercisePackageService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val exercisePackageService: ExercisePackageService,
    private val accountService: AccountService,
    private val userPreferences: UserPreferences,
    @ApplicationContext private val context: Context
) : UserSharedViewModel() {

    private val _fetchedCategories = MutableStateFlow<List<Category>>(emptyList())
    val fetchedCategories: StateFlow<List<Category>> = _fetchedCategories
    val userType: StateFlow<Int> = _userType
    val userName: StateFlow<String> = _userName
    private val _nextReminder = MutableStateFlow<Reminder?>(null)
    var nextReminder: StateFlow<Reminder?> = _nextReminder
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init {
        _isLoading.update { true }
        fetchUserPackages()
        fetchUserType()
        fetchReminders()
        fetchDiscoverPackages()
        _isLoading.update { false }
    }

    fun refreshContent() {
        launchCatching(
            tag = FAVORITES_VIEW_MODEL_TAG,
            block = {
                _isRefreshing.update { true }
                try {
                    fetchUserPackages()
                    fetchDiscoverPackages()
                    fetchUserType()
                    fetchReminders()
                } finally {
                    _isRefreshing.update { false }
                }
            })
    }

    private fun fetchUserPackages() {
        launchCatching(
            tag = FAVORITES_VIEW_MODEL_TAG,
            block = {
                Log.d(FAVORITES_VIEW_MODEL_TAG, "Fetching user packages")
                val userPackages = exercisePackageService.getUserExercisePackages()
                _userFavoritePackagesList.value =
                    userPackages.favoritePackages as List<ExercisePackage>
                _userAssignedPackagesList.value =
                    userPackages.assignedPackages as List<ExercisePackage>
                fetchCategories()
            }
        )
    }

    private fun fetchDiscoverPackages() {
        launchCatching(
            tag = FAVORITES_VIEW_MODEL_TAG,
            block = {
                Log.d(FAVORITES_VIEW_MODEL_TAG, "Fetching discover packages")
                val packages = exercisePackageService.getDiscoverSectionPackages()
                _discoverPackagesList.value = packages
            })
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
                Log.d(FAVORITES_VIEW_MODEL_TAG, "Fetching reminders: ${reminders}")
            }
        )
    }

    private fun getNextReminder(reminders: List<Reminder>): Reminder? {
        val currentTimeMillis = System.currentTimeMillis()
        val currentCalendar = Calendar.getInstance()
        currentCalendar.timeInMillis = currentTimeMillis

        val remindersWithTimeInMillis = reminders.mapNotNull { reminder ->
            val dayOfWeekInt = mapDayOfWeekToCalendar(reminder.dayOfWeek)
            val reminderCalendar = Calendar.getInstance()

            reminderCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeekInt)
            val (hour, minute) = reminder.time.split(":").map { it.toInt() }
            reminderCalendar.set(Calendar.HOUR_OF_DAY, hour)
            reminderCalendar.set(Calendar.MINUTE, minute)
            reminderCalendar.set(Calendar.SECOND, 0)
            reminderCalendar.set(Calendar.MILLISECOND, 0)

            if (reminderCalendar.timeInMillis < currentTimeMillis) {
                reminderCalendar.add(Calendar.WEEK_OF_YEAR, 1)
            }

            //Log.d("ReminderDebug", "Reminder: ${reminder.topic}, Time: ${reminderCalendar.time}")
            reminder to reminderCalendar.timeInMillis
        }

        val nextReminder = remindersWithTimeInMillis.minByOrNull { it.second }?.first
        //Log.d("ReminderDebug", "Next Reminder: $nextReminder")
        return nextReminder
    }

    private fun mapDayOfWeekToCalendar(dayOfWeek: String): Int {
        val daysOfWeek = context.resources.getStringArray(R.array.days_of_week)

        return when (dayOfWeek.uppercase()) {
            daysOfWeek[0].uppercase() -> Calendar.MONDAY
            daysOfWeek[1].uppercase() -> Calendar.TUESDAY
            daysOfWeek[2].uppercase() -> Calendar.WEDNESDAY
            daysOfWeek[3].uppercase() -> Calendar.THURSDAY
            daysOfWeek[4].uppercase() -> Calendar.FRIDAY
            daysOfWeek[5].uppercase() -> Calendar.SATURDAY
            daysOfWeek[6].uppercase() -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }

    private fun fetchCategories() {

        _fetchedCategories.value = listOf(
            Category(
                context.getString(R.string.category_favorites),
                _userFavoritePackagesList.value
            ),
            Category(
                context.getString(R.string.category_assigned),
                _userAssignedPackagesList.value
            ),
            Category(
                context.getString(R.string.category_premium),
                isPremium = true
            ),
        )
    }

    private fun fetchUserType() {
        _userType.value = userPreferences.getUserType()
        _userName.value = userPreferences.getUserName()
    }

    companion object {
        private const val FAVORITES_VIEW_MODEL_TAG = "FavoritesViewModel"
    }
}