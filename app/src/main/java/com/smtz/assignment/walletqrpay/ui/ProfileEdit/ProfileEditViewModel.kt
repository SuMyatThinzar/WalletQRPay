package com.smtz.assignment.walletqrpay.ui.ProfileEdit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.smtz.assignment.walletqrpay.data.model.UserData
import com.smtz.assignment.walletqrpay.data.repository.ProfileEditRepository
import com.smtz.assignment.walletqrpay.util.UserPreferences
import kotlinx.coroutines.launch

class ProfileEditViewModel (
    private val profileEditRepository: ProfileEditRepository = ProfileEditRepository(),
    private val userPreferences: UserPreferences
) : ViewModel() {

    val userData = mutableStateOf<UserData?>(null)
    var isLoading = mutableStateOf(false)
    var errorMessage = mutableStateOf("")
    var userName = mutableStateOf("")
    var isSuccessUpdate = mutableStateOf(false)
    var isUserLoggedOut = mutableStateOf(false)

    fun updateProfile() {
        if (userName.value.trim().isEmpty()) {
            errorMessage.value = "User name can't be empty"
            return
        }

        isLoading.value = true
        profileEditRepository.updateProfile(
            userData.value?.userId ?: "",
            userName.value,
            onSuccess = {
                isLoading.value = false
                errorMessage.value = ""
                isSuccessUpdate.value = true
            },
            onFailure = {
                isLoading.value = false
                errorMessage.value = it
            }
        )

    }

    fun logOutUser() {
        viewModelScope.launch {
            userPreferences.clearUser()
            isUserLoggedOut.value = true
        }
    }


}