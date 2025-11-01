package com.smtz.assignment.walletqrpay.data.model

data class TransactionData (
    val transactionId: String = "",
    val senderId: String = "",
    val senderPhone: String = "",
    val receiverId: String = "",
    val receiverPhone: String = "",
    val amount: Int = 0,
    val transactionType: String = "",
    val timestamp: Long = 0
)