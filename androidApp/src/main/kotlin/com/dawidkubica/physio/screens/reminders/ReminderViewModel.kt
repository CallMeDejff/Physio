package com.dawidkubica.physio.screens.reminders

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dawidkubica.physio.R
import com.dawidkubica.physio.models.Reminder
import com.dawidkubica.physio.navigation.CalendarScreen
import com.dawidkubica.physio.screens.profile.UserSharedViewModel
import com.dawidkubica.physio.screens.reminders.components.ReminderWorker
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.ExercisePackageService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val exercisePackageService: ExercisePackageService,
    private val accountService: AccountService,
    @ApplicationContext private val context: Context
) : UserSharedViewModel() {

    private val workManager = WorkManager.getInstance()
    private val _listedPackages = MutableStateFlow<Set<String>>(emptySet())
    val listedPackages: StateFlow<Set<String>> = _listedPackages

    private val fullDaysOfWeek: List<String> by lazy {
        context.resources.getStringArray(R.array.days_of_week_full).toList()
    }

    private val shortDaysOfWeek: List<String> by lazy {
        context.resources.getStringArray(R.array.days_of_week).toList()
    }

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            fetchUserPackages(exercisePackageService, REMINDER_VIEWMODEL_TAG)
            fetchReminders(accountService, REMINDER_VIEWMODEL_TAG)

            combine(
                _userAssignedPackagesList,
                _userFavoritePackagesList
            ) { assigned, favorite ->
                val packagesSet = mutableSetOf<String>()
                assigned.forEach { item -> packagesSet.add(item.name) }
                favorite.forEach { item -> packagesSet.add(item.name) }
                packagesSet
            }.collect { packages ->
                _listedPackages.value = packages
            }
        }
    }

    fun scheduleReminder(popBackStack: () -> Unit, dayOfWeek: String, time: String, topic: String) {
        _isLoading.update { true }
        val shortDay = mapFullDayToShort(dayOfWeek)

        if (isReminderAlreadyScheduled(shortDay, topic)) {
            _message.update { context.getString(R.string.reminder_already_exists) }
            _isLoading.update { false }
            return
        }

        val reminder = Reminder(dayOfWeek = shortDay, time = time, topic = topic)

        Log.d(REMINDER_VIEWMODEL_TAG, "scheduleReminder: Scheduling reminder: $reminder")

        viewModelScope.launch {
            val reminderId = accountService.addReminderForUser(reminder)
            reminderId?.let {
                scheduleNotification(reminder, it)
                fetchReminders(accountService, REMINDER_VIEWMODEL_TAG)
            }
            _isLoading.update { false }
            _message.update { context.getString(R.string.reminder_added) }
            popBackStack()
        }
    }

    private fun isReminderAlreadyScheduled(dayOfWeek: String, topic: String): Boolean {
        val existingReminder = _reminders.value?.any { reminder ->
            reminder.dayOfWeek == dayOfWeek && reminder.topic == topic
        }
        return existingReminder == true
    }

    private fun scheduleNotification(reminder: Reminder, reminderId: String) {
        val dayOfWeekInt = mapShortDayToCalendar(reminder.dayOfWeek)
        val timeParts = reminder.time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        scheduleWeeklyReminder(dayOfWeekInt, hour, minute, reminderId, reminder.topic)
    }

    fun onDeleteReminderClick(reminderId: String) {
        viewModelScope.launch {
            accountService.deleteReminderForUser(reminderId)
            workManager.cancelAllWorkByTag(reminderId)
            fetchReminders(accountService, REMINDER_VIEWMODEL_TAG)
        }
    }

    private fun mapShortDayToCalendar(shortDay: String): Int {
        return when (shortDay) {
            shortDaysOfWeek[0] -> Calendar.MONDAY
            shortDaysOfWeek[1] -> Calendar.TUESDAY
            shortDaysOfWeek[2] -> Calendar.WEDNESDAY
            shortDaysOfWeek[3] -> Calendar.THURSDAY
            shortDaysOfWeek[4] -> Calendar.FRIDAY
            shortDaysOfWeek[5] -> Calendar.SATURDAY
            shortDaysOfWeek[6] -> Calendar.SUNDAY
            else -> Calendar.MONDAY
        }
    }

    private fun mapFullDayToShort(fullDay: String): String {
        val index = fullDaysOfWeek.indexOf(fullDay)
        return if (index != -1) shortDaysOfWeek[index] else shortDaysOfWeek[0]
    }

    private fun scheduleWeeklyReminder(
        dayOfWeek: Int,
        hour: Int,
        minute: Int,
        reminderId: String,
        topic: String
    ) {
        val data = Data.Builder()
            .putString("topic", topic)
            .build()

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(false)
            .setRequiresCharging(false)
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .build()

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
            .setConstraints(constraints)
            .setInitialDelay(calculateInitialDelay(dayOfWeek, hour, minute), TimeUnit.MILLISECONDS)
            .addTag(reminderId)
            .setInputData(data)
            .build()
        workManager.enqueue(workRequest)
    }

    private fun calculateInitialDelay(dayOfWeek: Int, hour: Int, minute: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, dayOfWeek)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) add(Calendar.DAY_OF_MONTH, 7)
        }
        Log.d(
            REMINDER_VIEWMODEL_TAG,
            "calculateInitialDelay: Calculating initial delay: ${target.timeInMillis - now.timeInMillis}"
        )
        return target.timeInMillis - now.timeInMillis
    }

    fun onAddReminderClick(navigate: (String) -> Unit) {
        navigate(CalendarScreen.Calendar.route)
    }

    fun onGoBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        private const val REMINDER_VIEWMODEL_TAG = "ReminderViewModel"
    }
}

