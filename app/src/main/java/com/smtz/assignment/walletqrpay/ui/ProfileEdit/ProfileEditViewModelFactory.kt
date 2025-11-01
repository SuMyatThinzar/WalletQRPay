package com.smtz.assignment.walletqrpay.ui.ProfileEdit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smtz.assignment.walletqrpay.data.repository.ProfileEditRepository
import com.smtz.assignment.walletqrpay.util.UserPreferences

class ProfileEditViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileEditViewModel::class.java)) {
            val userPreferences = UserPreferences(context)
            val repository = ProfileEditRepository()
            return ProfileEditViewModel(repository, userPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}