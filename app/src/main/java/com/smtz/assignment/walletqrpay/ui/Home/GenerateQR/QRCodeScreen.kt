package com.smtz.assignment.walletqrpay.ui.Home.GenerateQR

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.smtz.assignment.walletqrpay.ui.theme.Dimens
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme
import net.glxn.qrgen.android.QRCode

@Composable
fun QRCodeScreen(
    qrString: String,
    userName: String,
    phoneNumber: String,
    onClose: () -> Unit
) {
    val qrBitmap by remember(qrString) {
        mutableStateOf(
            QRCode.from(qrString)
                .withSize(500, 500)  // generate 500x500 px bitmap
                .bitmap()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                userName,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontSize = Dimens.TextLarge,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                phoneNumber,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontSize = Dimens.TextRegular,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(Dimens.MarginxxLarge))

            // Show QR code image
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(250.dp)
            )

        }

        // Close button
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .padding(top = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "QRScreen Preview"
)
@Composable
fun QRScreenPreview() {
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