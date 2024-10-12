package com.example.physio.service

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class UserPreferences @Inject constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("user_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val USER_UID_KEY = "user_uid"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_LASTNAME_KEY = "user_lastname"
        private const val USER_LICENSE_NUMBER_KEY = "user_license_number"
        private const val USER_TYPE = "user_type"

    }

    fun getUserUid(): String {
        return sharedPreferences.getString(USER_UID_KEY, "") ?: ""
    }

    fun getUserName(): String {
        return sharedPreferences.getString(USER_NAME_KEY, "") ?: ""
    }

    fun getUserLastname(): String {
        return sharedPreferences.getString(USER_LASTNAME_KEY, "") ?: ""
    }

    fun getUserLicenseNumber(): Int {
        return sharedPreferences.getInt(USER_LICENSE_NUMBER_KEY, 0)
    }

    fun getUserType(): Int {
        return sharedPreferences.getInt(USER_TYPE, 0)
    }

    fun setUser(uid: String, name: String, lastname: String, licenseNumber: Int, userType: Int) {
        with(sharedPreferences.edit()) {
            putString(USER_UID_KEY, uid)
            putString(USER_NAME_KEY, name)
            putString(USER_LASTNAME_KEY, lastname)
            putInt(USER_LICENSE_NUMBER_KEY, licenseNumber)
            putInt(USER_TYPE, userType)
            apply()
        }
    }

    fun clearData() {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
    }
}
