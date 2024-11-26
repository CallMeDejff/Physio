package com.dawidkubica.physio.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.dawidkubica.physio.core.Constants.AUTH_PORT
import com.dawidkubica.physio.core.Constants.FIRESTORE_PORT
import com.dawidkubica.physio.core.Constants.LOCALHOST
import com.dawidkubica.physio.core.Constants.STORAGE_PORT
import com.dawidkubica.physio.core.PhysioApp
import com.dawidkubica.physio.service.services.StorageSampleDataService
import com.facebook.CallbackManager
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class PhysioActivity : ComponentActivity() {

    @Inject
    lateinit var storageSampleDataService: StorageSampleDataService

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("PhysioActivity", "Permission granted for notifications.")
            } else {
                Log.d("PhysioActivity", "Permission denied for notifications.")
            }
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        createNotificationChannel()

        //configureFirebaseServices()
        insertSampleData()

        setContent {
            val navController = rememberNavController()
            PhysioApp(navController = navController)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(
            "PhysioActivity",
            "onActivityResult called with requestCode: $requestCode, resultCode: $resultCode"
        )
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun createNotificationChannel() {
        val channelId = "REMINDER_CHANNEL"
        val channelName = "Harmonogram Physio"
        val channelDescription = "Powiadomienia o zaplanowanych ćwiczeniach."
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun configureFirebaseServices() {
        Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
        Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
        Firebase.storage.useEmulator(LOCALHOST, STORAGE_PORT)
    }

    private fun insertSampleData() {
        lifecycleScope.launch {
            storageSampleDataService.setSampleData()
        }
    }

    companion object {
        private const val REQUEST_CODE = 1001
    }
}
