package com.smtz.assignment.walletqrpay.ui.Home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.*
import androidx.compose.ui.text.font.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smtz.assignment.walletqrpay.R
import com.smtz.assignment.walletqrpay.R.drawable.*
import com.smtz.assignment.walletqrpay.data.model.TransactionData
import com.smtz.assignment.walletqrpay.data.model.UserData
import com.smtz.assignment.walletqrpay.data.repository.HomeRepository
import com.smtz.assignment.walletqrpay.ui.Home.GenerateQR.QRCodeActivity
import com.smtz.assignment.walletqrpay.ui.Home.ScanQR.ScanScreen
import com.smtz.assignment.walletqrpay.ui.Loading.LoadingScreen
import com.smtz.assignment.walletqrpay.ui.Transfer.TransferActivity
import com.smtz.assignment.walletqrpay.ui.theme.*
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme
import com.smtz.assignment.walletqrpay.util.UserPreferences
import com.smtz.assignment.walletqrpay.util.timeMillisToActualDate

@Composable
fun HomeScreen() {

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(HomeRepository(), UserPreferences(LocalContext.current.applicationContext)))

    val context = LocalContext.current
    val userData by homeViewModel.user
    val isLoading by homeViewModel.isLoading
    val errorMessage by homeViewModel.errorMessage

    var showScanner by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {

        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets(0, 0, 0, 0) //  Disable default padding for status bar
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item {
                    HeaderWithBalanceSection(userData)
                }

                item {
                    ActionButtons(
                        onScanClick = { showScanner = true },
                        onSendClick = {
                            val intent = TransferActivity.newIntent(context, isPhoneNumberTransfer = true)  // receiverId is empty
                            context.startActivity(intent)
                        },
                        onReceiveClick = {
                            val intent = QRCodeActivity.newIntent(context, "${userData?.userId}", "${userData?.userName}", "${userData?.phone}")
                            context.startActivity(intent)
                        })
                }

                item {
                    TransactionRecordsTitleSection()
                }

                // Recyclerview and empty view
                userData?.let { user ->
                    val transactions = user.transactions

                    if (transactions.isEmpty()) {
                        item {
                            EmptyTransactionRecordView()
                        }
                    } else {
                        items(transactions) { transaction ->
                            TransactionRecordItem(user, transaction)
                        }
                    }
                }
            }

        }


        if (showScanner) {
            ScanScreen(
                onQRScanned = { receiverId ->
                    showScanner = false
                    Log.d("Scanner", "Scanned string: $receiverId")

                    val intent = TransferActivity.newIntent(context, receiverId, isPhoneNumberTransfer = false)
                    context.startActivity(intent)

                },
                onClose = { showScanner = false }
            )
        }

        if (errorMessage.isNotEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }

        if (isLoading) {
            LoadingScreen()
        }

    }
}


@Composable
fun HeaderWithBalanceSection(userData: UserData?) {
    // Both Header and Card height is 270.dp. Header only take 220.dp. Card clips to bottom with height 100.dp. 270 - 220 = 50dp is overlay.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {

        // Header Background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    DarkBluePrimary,
                    RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = Dimens.MarginxLarge, top = Dimens.MarginxxLarge, end = Dimens.MarginxLarge, bottom = Dimens.MarginLarge),
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(Dimens.MarginxxxLarge))

                // App Title
                Text(
                    text = "Wallet QRPay",
                    fontSize = Dimens.TextxLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(Dimens.MarginxxLarge))

                // Circle Avatar + Name & Phone
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.height(50.dp) // same height for circle & text
                ) {
                    // Dummy Profile icon
                    Image(
                        painter = painterResource(id = ic_avatar), // your drawable
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(Dimens.MarginxMedium))

                    // Name & Phone Number
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = userData?.userName ?: "User Name",
                            fontSize = Dimens.TextxSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = userData?.phone ?: "+959 123 456 789",
                            fontSize = Dimens.TextxSmall,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }



        // Floating Balance Card
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .width(300.dp)
                .height(100.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Total Balances", fontSize = Dimens.TextSmall, color = MaterialTheme.colorScheme.primary)
                Text("${userData?.points ?: 0} Points", fontSize = Dimens.TextLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}



@Composable
fun ActionButtons(
    onScanClick: () -> Unit,
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = painterResource(ic_scan),
            label = "Scan",
            onClick = onScanClick
        )
        ActionButton(
            icon = painterResource(ic_transfer),
            label = "Send",
            onClick = onSendClick
        )
        ActionButton(
            icon = painterResource(ic_qr),
            label = "Receive",
            onClick = onReceiveClick
        )
    }
}

@Composable
fun ActionButton(
    icon: Painter,
    label: String,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .size(90.dp)
            .padding(vertical = 12.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}


@Composable
fun TransactionRecordsTitleSection() {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        text = "Transaction Records",
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.White
    )
}


@Composable
fun TransactionRecordItem(userData: UserData, transactionData: TransactionData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    // Sent to +9593454135
                    val transactionType = if (userData.userId == transactionData.senderId) { "Sent to ${transactionData.receiverPhone}" } else { "Received from ${transactionData.senderPhone}" }

                    Text(text = transactionType, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.alignByBaseline())
                    Text(text = "${transactionData.amount} Points", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }

                Text(timeMillisToActualDate(transactionData.timestamp), color = Color.Gray, fontSize = Dimens.TextSmall)


            }
        }
    }
}


@Composable
fun EmptyTransactionRecordView() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No transactions yet",
            color = Color.Gray.copy(alpha = 0.8f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}



@Preview(
    showBackground = true,
    showSystemUi = true,
    name = "Home Screen Preview"
)
@Composable
fun HomeScreenPreview() {
    WalletQRPayTheme {
        Surface(color = Color(0xFFEFF4F2)) {
            HomeScreen()
        }
    }
}