package com.smtz.assignment.walletqrpay.data.model

data class UserData(
    val userId: String = "",
    val userName: String = "",
    val phone: String = "",
    val password: String = "",
    val points: Int = 0,
//    val fcmToken: String = "",
    val transactions: List<TransactionData> = emptyList()
)