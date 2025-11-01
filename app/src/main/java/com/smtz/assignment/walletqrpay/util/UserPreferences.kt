package com.smtz.assignment.walletqrpay.util

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.*

val Context.userDataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    suspend fun saveUserId(userId: String) {
        context.userDataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    suspend fun getUserId(): String? {
        val prefs = context.userDataStore.data.map { it[USER_ID_KEY] }.first()
        return prefs
    }

    suspend fun clearUser() {
        context.userDataStore.edit { it.clear() }
    }
}