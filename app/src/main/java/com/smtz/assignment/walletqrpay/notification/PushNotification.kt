package com.smtz.assignment.walletqrpay.notification

data class PushNotification(
    val data: NotificationData,
    val to: String
)