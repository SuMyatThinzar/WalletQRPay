package com.smtz.assignment.walletqrpay.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun timeMillisToActualDate(timeMillis: Long): String {
    val date = Date(timeMillis)
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(date)
}