package com.smtz.assignment.walletqrpay.ui.Transfer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smtz.assignment.walletqrpay.ui.Home.GenerateQR.QRCodeActivity
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme

class TransferActivity : ComponentActivity() {

    companion object {
        private const val EXTRA_RECEIVER_ID = "EXTRA RECEIVER ID"
        private const val EXTRA_PHONE_NUMBER_TRANSFER = "EXTRA PHONE NUMBER TRANSFER"

        fun newIntent(context: Context, receiverId: String ="", isPhoneNumberTransfer: Boolean) : Intent {

            val intent = Intent(context, TransferActivity::class.java)
            intent.putExtra(EXTRA_RECEIVER_ID, receiverId)
            intent.putExtra(EXTRA_PHONE_NUMBER_TRANSFER, isPhoneNumberTransfer)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val receiverId = intent.getStringExtra(EXTRA_RECEIVER_ID) ?: ""
        val isPhoneNumberTransfer = intent.getBooleanExtra(EXTRA_PHONE_NUMBER_TRANSFER, true)

        enableEdgeToEdge()
        setContent {
            WalletQRPayTheme {
                TransferScreen(
                    receiverId = receiverId,
                    isPhoneNumberTransfer = isPhoneNumberTransfer,
                    onFinishTransaction = {
                        finish()
                    }
                )
            }
        }
    }
}