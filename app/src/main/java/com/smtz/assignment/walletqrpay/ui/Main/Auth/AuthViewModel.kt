package com.smtz.assignment.walletqrpay.ui.Main.Auth

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.google.i18n.phonenumbers.*
import com.smtz.assignment.walletqrpay.data.repository.AuthRepository
import com.smtz.assignment.walletqrpay.util.UserPreferences
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val userPreferences: UserPreferences
) : ViewModel() {

    val isLogin = mutableStateOf(true)
    val inputPhoneNumber = mutableStateOf("")      // only used in UI input
    val formattedPhoneNumber = mutableStateOf("")  // only used in formatting and validation after button clicked
    val password = mutableStateOf("")
    val userName = mutableStateOf("")
    val isLoading = mutableStateOf(false)
    val isLoginSuccess = mutableStateOf(false)
    val errorMessage = mutableStateOf("")

    init {
        checkSavedUser()
    }

    private fun checkSavedUser() {
        viewModelScope.launch {
            val userId = userPreferences.getUserId()
            isLoginSuccess.value = !userId.isNullOrEmpty()
        }
    }


    // Change between login or signup
    fun toggleLoginSignup() {
        isLogin.value = !isLogin.value
        errorMessage.value = ""
        inputPhoneNumber.value = ""
        formattedPhoneNumber.value = ""
        password.value = ""
    }

    fun validatePassword(): Boolean {
        val pass = password.value.trim()
        if (pass.isEmpty()) {
            errorMessage.value = "Password cannot be empty."
            return false
        } else if (pass.length < 6) {
            errorMessage.value = "Password must be at least 6 characters long."
            return false
        }
        errorMessage.value = ""
        return true
    }

    fun validatePhone(): Boolean {
        formattedPhoneNumber.value = inputPhoneNumber.value
        val phone = formattedPhoneNumber.value.trim()
        val phoneUtil = PhoneNumberUtil.getInstance()

        if (phone.isEmpty()) {
            errorMessage.value = "Phone Number cannot be empty."
            return false
        }

        if (!isLogin.value) {   // only for Sign Up
            val name = userName.value.trim()
            if (name.isEmpty()) {
                errorMessage.value = "User Name cannot be empty."
                return false
            }
        }

        // default region code
        val formattedNumber = phoneUtil.parse(phone, "MM")
        if (!phoneUtil.isValidNumber(formattedNumber)) {
            errorMessage.value = "Invalid Phone Number format."
            return false
        }

        val normalized = normalizePhoneNumber(phone, "MM")
        if (normalized != null) {
            formattedPhoneNumber.value = normalized
        }

        errorMessage.value = ""
        return true
    }

    private fun normalizePhoneNumber(input: String, defaultRegion: String = "MM"): String? {
        val phoneUtil = PhoneNumberUtil.getInstance()

        return try {
            // parse phone with default region (eg. "MM" = Myanmar)
            val numberProto = phoneUtil.parse(input, defaultRegion)

            // starts with countrycode
            phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (e: NumberParseException) {
            null
        }
    }


    /// Network Call

    fun loginUser() {
        isLoading.value = true

        repository.login(
            formattedPhoneNumber.value,
            password.value,
            onSuccess = { userId ->
                isLoading.value = false
                isLoginSuccess.value = true
                errorMessage.value = ""

                // save userid to shared preferences
                viewModelScope.launch {
                    userPreferences.saveUserId(userId)
                }
            },
            onFailure = { error ->
                isLoading.value = false
                errorMessage.value = error
                isLoginSuccess.value = false
            }
        )
    }

    fun signupUser() {
        isLoading.value = true

        repository.signup(
            userName.value,
            formattedPhoneNumber.value,
            password.value,
            onSuccess = { userId ->
                isLoading.value = false
                isLoginSuccess.value = true
                errorMessage.value = ""

                // save userid to shared preferences
                viewModelScope.launch {
                    userPreferences.saveUserId(userId)
                }
            },
            onFailure = { error ->
                isLoading.value = false
                errorMessage.value = error
                isLoginSuccess.value = false
            }
        )
    }

}
