package com.smtz.assignment.walletqrpay.ui.Home.ScanQR

import android.util.Log
import androidx.activity.compose.*
import androidx.annotation.OptIn
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.smtz.assignment.walletqrpay.ui.Home.HomeScreen
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme

@Composable
fun ScanScreen(
    onQRScanned: (String) -> Unit,
    onClose: () -> Unit
) {
    val scanLauncher = rememberLauncherForActivityResult(
        contract = ScanContract(),
        onResult = { result ->
            if (result.contents != null) {
                onQRScanned(result.contents)
            } else {
                onClose()
            }
        }
    )

    // Launch scanner when enter screen
    LaunchedEffect(Unit) {
        val options = ScanOptions().apply {
            setPrompt("Scan a QR code to Pay")
            setBeepEnabled(true)
            setOrientationLocked(false)
        }
        scanLauncher.launch(options)
    }

}