package com.example.physio.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.physio.PhysioApp
import com.example.physio.core.Constants.AUTH_PORT
import com.example.physio.core.Constants.FIRESTORE_PORT
import com.example.physio.core.Constants.LOCALHOST
import com.example.physio.core.Constants.STORAGE_PORT
import com.example.physio.service.services.StorageSampleDataService
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureFirebaseServices()
        insertSampleData()

        setContent {
            val navController = rememberNavController()
            PhysioApp(navController = navController)
        }
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
}
