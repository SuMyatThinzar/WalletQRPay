package com.smtz.assignment.walletqrpay.ui.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smtz.assignment.walletqrpay.data.repository.HomeRepository
import com.smtz.assignment.walletqrpay.util.UserPreferences

class HomeViewModelFactory(
    private val repository: HomeRepository,
    private val userPreferences: UserPreferences
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repository, userPreferences) as T
        }

        throw IllegalArgumentException()
    }
}