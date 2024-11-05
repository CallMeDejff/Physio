package com.example.physio.screens.reminders

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.physio.core.PhysioAppViewModel
import com.example.physio.models.ExercisePackage
import com.example.physio.models.Reminder
import com.example.physio.screens.profile.UserSharedViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val exercisePackageService: ExercisePackageService,
    private val accountService: AccountService,
    private val listService: ListService
) : UserSharedViewModel() {

    private val workManager = WorkManager.getInstance()
    private val _listedPackaged = MutableStateFlow<Set<String>>(emptySet())
    val listedPackages: StateFlow<Set<String>> = _listedPackaged

    fun initializer() {
        launchCatching(
            tag = REMINDER_VIEWMODEL_TAG,
            block = {
                _isLoading.update { true }
                listService.getPackagesList()
                fetchUserPackages(exercisePackageService, REMINDER_VIEWMODEL_TAG)
                fetchReminders(accountService, REMINDER_VIEWMODEL_TAG)
                _isLoading.update { false }
            }
        )
    }

    fun scheduleReminder(dayOfWeek: String, time: String, topic: String) {
        val reminder = Reminder(dayOfWeek, time, topic)
        viewModelScope.launch {
            val reminderId = accountService.addReminderForUser(reminder)
            reminderId?.let {
                scheduleNotification(reminder, it)
                fetchReminders(accountService, REMINDER_VIEWMODEL_TAG)
            }
        }
    }

    private fun scheduleNotification(reminder: Reminder, reminderId: String) {
        val dayOfWeekInt = mapDayOfWeekToCalendar(reminder.dayOfWeek)
        val timeParts = reminder.time.split(":")
        val hour = timeParts[0].toInt()
        val minute = timeParts[1].toInt()

        scheduleWeeklyReminder(dayOfWeekInt, hour, minute, reminderId, reminder.topic)
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            Log.d(REMINDER_VIEWMODEL_TAG, "Deleting reminder with ID: $reminderId")
            accountService.deleteReminderForUser(reminderId)
            workManager.cancelAllWorkByTag(reminderId)
            fetchReminders(accountService, REMINDER_VIEWMODEL_TAG)
        }
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

        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(7, TimeUnit.DAYS)
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
        return target.timeInMillis - now.timeInMillis
    }

    fun onGoBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        private const val REMINDER_VIEWMODEL_TAG = "ReminderViewModel"
    }
}
