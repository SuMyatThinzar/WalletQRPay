package com.smtz.assignment.walletqrpay.ui.Home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import com.smtz.assignment.walletqrpay.R.drawable.*
import com.smtz.assignment.walletqrpay.ui.Home.GenerateQR.QRCodeActivity
import com.smtz.assignment.walletqrpay.ui.Home.ScanQR.ScanScreen
import com.smtz.assignment.walletqrpay.ui.theme.*
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalletQRPayTheme {
                HomeScreen()
            }
        }
    }
}
