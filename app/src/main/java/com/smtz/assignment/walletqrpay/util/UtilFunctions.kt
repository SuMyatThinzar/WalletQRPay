package com.smtz.assignment.walletqrpay.util

import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun timeMillisToActualDate(timeMillis: Long): String {
    val date = Date(timeMillis)
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    return sdf.format(date)
}

fun toJsonString(obj: Any): String {
    return Gson().toJson(obj)
}

fun fromJsonString(json: String, clazz: Class<*>): Any? {
    return Gson().fromJson(json, clazz)
}
