package com.smtz.assignment.walletqrpay.ui.Home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smtz.assignment.walletqrpay.data.model.UserData
import com.smtz.assignment.walletqrpay.data.repository.HomeRepository
import com.smtz.assignment.walletqrpay.util.UserPreferences
import kotlinx.coroutines.launch

class HomeViewModel(
    private val homeRepository: HomeRepository = HomeRepository(),
    private val userPreferences: UserPreferences
) : ViewModel() {

    var user = mutableStateOf<UserData?>(null)
    var isLoading = mutableStateOf(true)
    var errorMessage = mutableStateOf("")
    var isLoggedOut = mutableStateOf(false)

    init {
        fetchInitialUserInfo()
    }

    private fun fetchInitialUserInfo() {
        viewModelScope.launch {
            isLoading.value = true

            val savedUserId = userPreferences.getUserId()

            if (savedUserId.isNullOrEmpty()) {
                isLoading.value = false
                return@launch
            }

            homeRepository.observeUserInfoRealtime(savedUserId, onSuccess = {
                user.value = it
                isLoading.value = false
                errorMessage.value = ""

            }, onFailure = { errorMsg ->
                errorMessage.value = errorMsg
                isLoading.value = false
            })
        }

    }

    fun logoutUser(userPreferences: UserPreferences) {
        viewModelScope.launch {
            userPreferences.clearUser()
            isLoggedOut.value = true
        }
    }

}