package com.smtz.assignment.walletqrpay.ui.Main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.smtz.assignment.walletqrpay.ui.Home.HomeActivity
import com.smtz.assignment.walletqrpay.ui.Main.Auth.AuthScreen
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme
import com.smtz.assignment.walletqrpay.util.UserPreferences
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycleScope.launch {
            val savedUserId = UserPreferences(applicationContext).getUserId()
            if (!savedUserId.isNullOrEmpty()) {
                startActivity(Intent(this@MainActivity, HomeActivity::class.java))
                finish()
            } else {
                setContent {
                    WalletQRPayTheme {
                        AuthScreen()
                    }
                }
            }
        }
    }
}

//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    WalletQRPayTheme {
//        AuthScreen()
//    }
//}