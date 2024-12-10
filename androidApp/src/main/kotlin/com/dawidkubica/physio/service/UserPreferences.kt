package com.dawidkubica.physio.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.dawidkubica.physio.models.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class UserPreferences @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    private val _themeModeFlow = MutableStateFlow(getThemeMode())
    val themeModeFlow: StateFlow<ThemeMode> = _themeModeFlow

    fun setThemeMode(mode: ThemeMode) {
        with(sharedPreferences.edit()) {
            putString("theme_mode", mode.name)
            apply()
        }
        _themeModeFlow.value = mode
    }

    fun getThemeMode(): ThemeMode {
        val modeName = sharedPreferences.getString("theme_mode", ThemeMode.SYSTEM.name)
        return ThemeMode.valueOf(modeName ?: ThemeMode.SYSTEM.name)
    }

    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME_KEY, "") ?: ""
    }

    fun getAccountProvider(): String {
        return sharedPreferences.getString(ACCOUNT_PROVIDER, "") ?: ""
    }

    fun getUserType(): Int {
        Log.d(
            "SharedPreferences",
            "getUserType() called: ${sharedPreferences.getInt(USER_TYPE, 0)}"
        )
        return sharedPreferences.getInt(USER_TYPE, 0)
    }

    fun setUser(
        uid: String,
        name: String,
        lastname: String,
        licenseNumber: Int,
        userType: Int,
        provider: String
    ) {
        with(sharedPreferences.edit()) {
            putString(USER_UID_KEY, uid)
            putString(USER_NAME_KEY, name)
            putString(USER_LASTNAME_KEY, lastname)
            putInt(USER_LICENSE_NUMBER_KEY, licenseNumber)
            putInt(USER_TYPE, userType)
            putString(ACCOUNT_PROVIDER, provider)
            apply()
        }
    }

    fun clearData() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }

    companion object {
        private const val USER_UID_KEY = "user_uid"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_LASTNAME_KEY = "user_lastname"
        private const val USER_LICENSE_NUMBER_KEY = "user_license_number"
        private const val USER_TYPE = "user_type"
        private const val ACCOUNT_PROVIDER = "account_provider"
    }
}
