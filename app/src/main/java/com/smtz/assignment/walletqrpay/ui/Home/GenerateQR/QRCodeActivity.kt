package com.smtz.assignment.walletqrpay.ui.Home.GenerateQR

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme

class QRCodeActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_QR_STRING = "Extra QR STRING"
        private const val EXTRA_USER_NAME = "Extra USER NAME"
        private const val EXTRA_PHONE_NUMBER = "Extra PHONE NUMBER"

        fun newIntent(context: Context, qrString: String, userName: String, phoneNumber: String) : Intent {

            val intent = Intent(context, QRCodeActivity::class.java)
            intent.putExtra(EXTRA_QR_STRING, qrString)
            intent.putExtra(EXTRA_USER_NAME, userName)
            intent.putExtra(EXTRA_PHONE_NUMBER, phoneNumber)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val qrString = intent.getStringExtra(EXTRA_QR_STRING) ?: ""
        val userName = intent.getStringExtra(EXTRA_USER_NAME) ?: ""
        val phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER) ?: ""

        enableEdgeToEdge()
        setContent {
            WalletQRPayTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    QRCodeScreen(
                        qrString = qrString,
                        userName = userName,
                        phoneNumber = phoneNumber,
                        onClose = { finish() }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WalletQRPayTheme {
        Surface(color = Color(0xFFEFF4F2)) {
            QRCodeScreen(
                qrString = "1234567890",
                userName = "John Doe",
                phoneNumber = "1234567890",
                onClose = {  }
            )
        }
    }
}