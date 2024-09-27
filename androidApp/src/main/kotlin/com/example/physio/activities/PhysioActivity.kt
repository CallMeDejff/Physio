package com.example.physio.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.physio.AUTH_PORT
import com.example.physio.FIRESTORE_PORT
import com.example.physio.LOCALHOST
import com.example.physio.STORAGE_PORT
import com.example.physio.navigation.PhysioApp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class PhysioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //configureFirebaseServices()

        setContent {
            val navController = rememberNavController()
            PhysioApp(navController = navController)
        }
    }

    private fun configureFirebaseServices() {
        Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
        Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
        //Firebase.database.useEmulator(LOCALHOST, DATABASE_PORT)
        Firebase.storage.useEmulator(LOCALHOST, STORAGE_PORT)
    }
}
