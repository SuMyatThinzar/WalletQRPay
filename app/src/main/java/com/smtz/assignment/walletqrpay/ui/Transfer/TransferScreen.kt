package com.smtz.assignment.walletqrpay.ui.Transfer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smtz.assignment.walletqrpay.data.model.UserData
import com.smtz.assignment.walletqrpay.data.repository.TransferRepository
import com.smtz.assignment.walletqrpay.ui.Loading.LoadingScreen
import com.smtz.assignment.walletqrpay.ui.theme.Dimens
import com.smtz.assignment.walletqrpay.ui.theme.WalletQRPayTheme
import com.smtz.assignment.walletqrpay.util.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(
    receiverId: String = "",
    isPhoneNumberTransfer: Boolean,
    onFinishTransaction: () -> Unit = {}
) {

    val transferViewModel: TransferViewModel = viewModel(factory = TransferViewModelFactory(TransferRepository(), UserPreferences(LocalContext.current.applicationContext), receiverId, isPhoneNumberTransfer))

    val context = LocalContext.current
    val senderData by transferViewModel.senderData
    val receiverData by transferViewModel.receiverData
    val receiverPhoneNumber by transferViewModel.receiverPhoneNumber
    val transferAmount by transferViewModel.transferAmount
    val isLoading by transferViewModel.isLoading
    val errorMessage by transferViewModel.errorMessage
    val isTransferSuccess by transferViewModel.isTransferSuccess

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Transfer Points",
                        fontSize = Dimens.TextLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? android.app.Activity)?.finish()   // onbackpressed
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(start = Dimens.MarginLarge, end = Dimens.MarginLarge, top = Dimens.MarginLarge, bottom = Dimens.MarginxxLarge)
            ) {
                Button(
                    onClick = {
                        if (transferViewModel.validatePhone()) {
                            senderData?.let {
                                if (isPhoneNumberTransfer) {
                                    transferViewModel.fetchReceiverInfoUsingPhoneNumberAndMakeTransaction(amount = transferViewModel.transferAmount.value.toIntOrNull() ?: 0)
                                } else {
                                    transferViewModel.makeTransaction(amount = transferViewModel.transferAmount.value.toIntOrNull() ?: 0)
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Continue", fontSize = Dimens.TextRegular)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Total Balance Section
            Column {
                Text(text = "Total Balance", fontWeight = FontWeight.Normal, color = Color.White)
                Spacer(modifier = Modifier.height(Dimens.MarginMedium))
                Text(text = "${senderData?.points ?: 0} points", fontWeight = FontWeight.Bold, color = Color.White)
            }

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            // Receiver Info
            ReceiverPhoneNumberSection(transferViewModel, isPhoneNumberTransfer, receiverData?.phone ?: "")

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            // Enter Amount
            EnterAmountSection(transferAmount, senderData, transferViewModel)

            Spacer(modifier = Modifier.height(100.dp))
        }
    }


    if (isTransferSuccess == true) {
        onFinishTransaction()
    }

    if (errorMessage.isNotEmpty()) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    if (isLoading || isTransferSuccess == false) {      // isTransferSuccess is set to null before makeTransaction() to prevent suddenly stop loading
        LoadingScreen()
    }
}

@Composable
fun ReceiverPhoneNumberSection(transferViewModel: TransferViewModel, isPhoneNumberTransfer: Boolean, receiverPhone: String) {
    Column {
        Text(
            text = "Receiver Phone Number",
            fontSize = Dimens.TextRegular,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(Dimens.MarginSmall))

        if (isPhoneNumberTransfer) {

            OutlinedTextFieldSection(
                label = "Enter receiver phone number",
                value = transferViewModel.receiverPhoneNumber.value,
                onValueChange = { inputValue ->
                    // allow only numbers and plus sign
                    val filtered = inputValue.filter { it.isDigit() || it == '+' }
                    transferViewModel.receiverPhoneNumber.value = filtered
                },
                keyboardType = KeyboardType.Phone
            )

        } else {    // through QR Scan (receiverData is already fetched at first)
            Text(
                text = receiverPhone,
                fontSize = Dimens.TextRegular,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }

}

@Composable
fun EnterAmountSection(transferAmount: String, senderData: UserData?, transferViewModel: TransferViewModel) {
    Column {
        Text(text = "Enter amount to transfer", fontSize = Dimens.TextRegular, fontWeight = FontWeight.Normal, color = Color.White)

        OutlinedTextFieldSection(
            label = "Amount (in points)",
            value = transferAmount,
            onValueChange = { inputValue ->

                val filtered = inputValue.filter { it.isDigit() }  // to filter "," , "." , etc

                val availablePoints = senderData?.points ?: 0
                val amount = filtered.toIntOrNull() ?: 0

                if (amount <= availablePoints) {
                    transferViewModel.transferAmount.value = filtered
                } else {
                    transferViewModel.transferAmount.value = availablePoints.toString()
                }
            },
            keyboardType = KeyboardType.Number
        )


        Text(
            text = "Available: ${senderData?.points ?: 0} points",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransferScreenPreview() {
    WalletQRPayTheme {
        TransferScreen(isPhoneNumberTransfer = true)
    }
}