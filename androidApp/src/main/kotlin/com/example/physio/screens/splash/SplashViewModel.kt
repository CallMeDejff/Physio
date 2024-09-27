package com.example.physio.screens.splash

import android.util.Log
import com.example.physio.navigation.AuthScreen
import com.example.physio.navigation.Graph
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
  private val accountService: AccountService
) : PhysioAppViewModel() {

  fun onAppStart(openAndPopUp: (String, String) -> Unit) {
    if (accountService.hasUser())
    {
      //openAndPopUp(DASHBOARD_SCREEN, SPLASH_SCREEN)
      openAndPopUp(Graph.HOME, AuthScreen.Splash.route)
      Log.i("SplashViewModel", "accountService.hasUser = true")
    }
    else {
      openAndPopUp(AuthScreen.SignIn.route, AuthScreen.Splash.route)
      Log.i("SplashViewModel", "accountService.hasUser = false")
    }
  }
}
