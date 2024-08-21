package com.example.physio.screens.profile

import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val accountService: AccountService
) : PhysioAppViewModel() {

    fun onLogoutClick(){
        launchCatching {
            accountService.signOut()
        }
    }
}