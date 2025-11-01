package com.smtz.assignment.walletqrpay.ui.Main.Auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smtz.assignment.walletqrpay.data.repository.AuthRepository
import com.smtz.assignment.walletqrpay.util.UserPreferences

class AuthViewModelFactory(
    private val repository: AuthRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository, userPreferences) as T
        }

        throw IllegalArgumentException()
    }
}