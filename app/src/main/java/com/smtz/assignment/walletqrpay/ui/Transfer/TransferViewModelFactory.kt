package com.smtz.assignment.walletqrpay.ui.Transfer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.smtz.assignment.walletqrpay.data.repository.TransferRepository
import com.smtz.assignment.walletqrpay.util.UserPreferences


class TransferViewModelFactory(
    private val repository: TransferRepository,
    private val userPreferences: UserPreferences,
    private val receiverId: String,
    private val isPhoneNumberTransfer: Boolean
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransferViewModel::class.java)) {
            return TransferViewModel(
                repository,
                userPreferences,
                receiverId,
                isPhoneNumberTransfer
            ) as T
        }

        throw IllegalArgumentException()
    }
}