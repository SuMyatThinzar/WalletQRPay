package com.smtz.assignment.walletqrpay.ui.Transfer

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
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
                    .padding(16.dp)
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
                Text(text = "Total Balance", fontSize = Dimens.TextMedium, fontWeight = FontWeight.Normal, color = Color.White)
                Spacer(modifier = Modifier.height(Dimens.MarginMedium))
                Text(text = "${senderData?.points ?: 0} points", fontSize = Dimens.TextMedium, fontWeight = FontWeight.Medium, color = Color.White)
            }

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            // Receiver Info
            ReceiverPhoneNumberSection(transferViewModel, isPhoneNumberTransfer, receiverData?.phone ?: "")

            Divider(color = Color.Gray.copy(alpha = 0.2f))

            // Enter Amount
            Column {
                Text(text = "Enter amount to transfer", fontSize = Dimens.TextRegular, fontWeight = FontWeight.Normal, color = Color.White)

                OutlinedTextField(
                    value = transferAmount,
                    onValueChange = { inputValue ->

                        val availablePoints = senderData?.points ?: 0
                        val amount = inputValue.toIntOrNull() ?: 0

                        if (amount <= availablePoints) {
                            transferViewModel.transferAmount.value = inputValue
                        }
                    },
                    
                    label = { Text("Amount (in points)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White
                    )
                )

                Text(
                    text = "Available: ${senderData?.points ?: 0} points",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

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

@OptIn(ExperimentalMaterial3Api::class)
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
            OutlinedTextField(
                value = transferViewModel.receiverPhoneNumber.value,
                onValueChange = { transferViewModel.receiverPhoneNumber.value = it },
                label = { Text("Enter receiver phone number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
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


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransferScreenPreview() {
    WalletQRPayTheme {
        TransferScreen(isPhoneNumberTransfer = true)
    }
}