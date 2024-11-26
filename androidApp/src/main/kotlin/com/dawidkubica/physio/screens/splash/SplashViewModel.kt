package com.dawidkubica.physio.screens.splash

import android.util.Log
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.navigation.AuthScreen
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.service.services.AuthenticationService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authenticationService: AuthenticationService
) : PhysioAppViewModel() {

    suspend fun onAppStart(openAndPopUp: (String, String) -> Unit) {
        if (authenticationService.hasUser()) {
            openAndPopUp(Graph.HOME, AuthScreen.Splash.route)
            Log.i("SplashViewModel", "accountService.hasUser = true")
        } else {
            openAndPopUp(AuthScreen.SignIn.route, AuthScreen.Splash.route)
            Log.i("SplashViewModel", "accountService.hasUser = false")
        }
    }
}
