package com.smtz.assignment.walletqrpay.ui.Transfer

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.smtz.assignment.walletqrpay.data.model.UserData
import com.smtz.assignment.walletqrpay.data.repository.TransferRepository
import com.smtz.assignment.walletqrpay.notification.NotificationData
import com.smtz.assignment.walletqrpay.notification.PushNotification
import com.smtz.assignment.walletqrpay.notification.RetrofitInstance
import com.smtz.assignment.walletqrpay.util.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransferViewModel(

    private val transferRepository: TransferRepository,
    private val userPreferences: UserPreferences,
    private val receiverId: String = "",
    private val isPhoneNumberTransfer: Boolean
) : ViewModel() {

    var senderId = mutableStateOf("")
    val receiverPhoneNumber = mutableStateOf("")
    val receiverFormattedPhoneNumber = mutableStateOf("")
    var senderData = mutableStateOf<UserData?>(null)
    var receiverData = mutableStateOf<UserData?>(null)
    var transferAmount = mutableStateOf("")
    var isLoading = mutableStateOf(true)
    var isTransferSuccess = mutableStateOf<Boolean?>(null)
    var errorMessage = mutableStateOf("")
    var isLoggedOut = mutableStateOf(false)

    init {
        fetchInitialUserInfo()

        if (!isPhoneNumberTransfer) {
            fetchReceiverInfoUsingId()     // fetch receiver data for qr scan with id
        }
    }


    private fun fetchInitialUserInfo() {
        viewModelScope.launch {
            isLoading.value = true

            if (userPreferences.getUserId().isNullOrEmpty()) {
                isLoading.value = false
                return@launch
            }

            senderId.value = userPreferences.getUserId()!!

            transferRepository.getUserInfo(senderId.value, onSuccess = {
                senderData.value = it
                isLoading.value = false
                errorMessage.value = ""

            }, onFailure = { errorMsg ->
                errorMessage.value = errorMsg
                isLoading.value = false
            })
        }
    }

    private fun fetchReceiverInfoUsingId() {
        viewModelScope.launch {
            isLoading.value = true

            if (receiverId.isEmpty()) {
                isLoading.value = false
                return@launch
            }

            transferRepository.getUserInfo(receiverId, onSuccess = {
                receiverData.value = it
                receiverPhoneNumber.value = it.phone
                isLoading.value = false
                errorMessage.value = ""

            }, onFailure = { errorMsg ->
                errorMessage.value = errorMsg
                isLoading.value = false
            })
        }
    }

    // works only after continue button clicks
    fun fetchReceiverInfoUsingPhoneNumberAndMakeTransaction(amount: Int) {
        viewModelScope.launch {
            isLoading.value = true

            if (receiverFormattedPhoneNumber.value.isEmpty()) {
                isLoading.value = false
                return@launch
            }

            transferRepository.fetchUserInfoUsingPhoneNumber(receiverFormattedPhoneNumber.value, onSuccess = {
                receiverData.value = it
                errorMessage.value = ""

                makeTransaction(amount = amount)

            }, onFailure = { errorMsg ->
                errorMessage.value = errorMsg
                isLoading.value = false
            })
        }
    }


    fun makeTransaction(amount: Int) {
        if (amount <= 0) {
            errorMessage.value = "Transfer amount must be greater than 0."
            return
        }

        isLoading.value = true
        isTransferSuccess.value = false

        if (senderData.value != null || receiverData.value != null) {

            transferRepository.performTransferTransaction(
                senderData = senderData.value!!,
                receiverData = receiverData.value!!,
                amount = amount,
                onSuccess = {
                    isTransferSuccess.value = true
                    isLoading.value = false
                    errorMessage.value = ""


                    // Can't get FCM server key for new projects
                    // get the FCM token of the receiver
//                    val receiverToken = receiverData.value!!.fcmToken
//
//                    sendPushToReceiver(
//                        receiverToken,
//                        title = "Points Received!",
//                        message = "You’ve received $amount points from ${senderData.value!!.userName}"
//                    )

                },
                onFailure = { errorMsg ->
                    isLoading.value = false
                    errorMessage.value = errorMsg
                    isTransferSuccess.value = false
                }
            )
        }
    }

    // Can't get FCM server key for new projects
    fun sendPushToReceiver(receiverToken: String, title: String, message: String) {
        val notification = PushNotification(
            data = NotificationData(title, message),
            to = receiverToken
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
                    Log.d("FCM", "Notification sent successfully!")
                } else {
                    Log.e("FCM", "Error sending notification: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("FCM", "Exception: ${e.message}")
            }
        }
    }




    // works only after continue button clicks
    fun validatePhone(): Boolean {

        receiverFormattedPhoneNumber.value = receiverPhoneNumber.value
        val phone = receiverFormattedPhoneNumber.value.trim()
        val phoneUtil = PhoneNumberUtil.getInstance()

        if (phone.isEmpty()) {
            errorMessage.value = "Phone Number cannot be empty."
            return false
        }

        // default region code
        val formattedNumber = phoneUtil.parse(phone, "MM")
        if (!phoneUtil.isValidNumber(formattedNumber)) {
            errorMessage.value = "Invalid Phone Number format."
            return false
        }

        val normalized = normalizePhoneNumber(phone, "MM")
        if (normalized != null) {
            receiverFormattedPhoneNumber.value = normalized
        }

        val currentUserPhoneNumber = senderData.value?.phone ?: ""

        if (receiverFormattedPhoneNumber.value == currentUserPhoneNumber) {
            errorMessage.value = "You cannot transfer points to yourself."
            return false
        }

        errorMessage.value = ""
        return true
    }

    private fun normalizePhoneNumber(input: String, defaultRegion: String = "MM"): String? {
        val phoneUtil = PhoneNumberUtil.getInstance()

        return try {
            // parse phone with default region (e.g. "MM" = Myanmar, "US" = United States)
            val numberProto = phoneUtil.parse(input, defaultRegion)

            // starts with countrycode
            phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (e: NumberParseException) {
            null
        }
    }
}