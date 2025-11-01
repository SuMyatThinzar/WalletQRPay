package com.smtz.assignment.walletqrpay.ui.ProfileEdit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smtz.assignment.walletqrpay.data.model.UserData
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme
import com.smtz.assignment.walletqrpay.util.fromJsonString

class ProfileEditActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_USER_DATA = "EXTRA_USER_DATA"

        fun newIntent(context: Context, userData: String ="") : Intent {
            val intent = Intent(context, ProfileEditActivity::class.java)
            intent.putExtra(EXTRA_USER_DATA, userData)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userDataJsonString = intent.getStringExtra(EXTRA_USER_DATA) ?: ""
        val userData = fromJsonString(userDataJsonString, UserData::class.java) as? UserData

        enableEdgeToEdge()
        setContent {
            WalletQRPayTheme {
                ProfileEditScreen(
                    userData = userData
                )
            }
        }
    }
}
