package com.example.physio.activities

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.physio.AUTH_PORT
import com.example.physio.DATABASE_PORT
import com.example.physio.FIRESTORE_PORT
import com.example.physio.LOCALHOST
import com.example.physio.PhysioApp
import com.example.physio.STORAGE_PORT
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.database
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("RestrictedApi")
@AndroidEntryPoint
class PhysioActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //WindowCompat.setDecorFitsSystemWindows(window, false)
        //window.statusBarColor = android.graphics.Color.TRANSPARENT

        //enableEdgeToEdge(statusBarStyle = SystemBarStyle.light(Color.Transparent.hashCode(), Color.Transparent.hashCode()))

        configureFirebaseServices()

        setContent {
            PhysioApp()
        }
    }

    private fun configureFirebaseServices() {
        Firebase.auth.useEmulator(LOCALHOST, AUTH_PORT)
        Firebase.firestore.useEmulator(LOCALHOST, FIRESTORE_PORT)
        //Firebase.database.useEmulator(LOCALHOST, DATABASE_PORT)
        Firebase.storage.useEmulator(LOCALHOST, STORAGE_PORT)
    }
}
