package com.smtz.assignment.walletqrpay.ui.ProfileEdit

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smtz.assignment.walletqrpay.data.model.UserData
import com.smtz.assignment.walletqrpay.ui.Loading.LoadingScreen
import com.smtz.assignment.walletqrpay.ui.Main.MainActivity
import com.smtz.assignment.walletqrpay.ui.Transfer.OutlinedTextFieldSection
import com.smtz.assignment.walletqrpay.ui.Transfer.TransferActivity
import com.smtz.assignment.walletqrpay.ui.theme.DarkBluePrimary
import com.smtz.assignment.walletqrpay.ui.theme.Dimens
import com.smtz.assignment.walletqrpay.util.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    userData: UserData?
) {

    val context = LocalContext.current

    val profileEditViewModel: ProfileEditViewModel = viewModel(factory = ProfileEditViewModelFactory(context))

    profileEditViewModel.userData.value = userData

    val isLoading by profileEditViewModel.isLoading
    val errorMessage by profileEditViewModel.errorMessage
    val userName by profileEditViewModel.userName
    val isSuccessUpdate by profileEditViewModel.isSuccessUpdate
    val isUserLoggedOut by profileEditViewModel.isUserLoggedOut

    profileEditViewModel.userName.value = userData?.userName ?: ""

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Profile Detail",
                        fontSize = Dimens.TextLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? android.app.Activity)?.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                actions = {
                    // Log Out button at the right side
                    Button(
                        onClick = {
                            profileEditViewModel.logOutUser()
                        },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(36.dp), // small height for topbar
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkBluePrimary, // dark blue or your theme color
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Log Out",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
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
                        profileEditViewModel.updateProfile()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(text = "Update", fontSize = Dimens.TextRegular)
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
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {

            // User Name Section
            Text(
                text = "Name",
                fontSize = Dimens.TextxSmall,
                color = Color.White
            )
            OutlinedTextFieldSection(
                label = "",
                value = userName,
                onValueChange = { profileEditViewModel.userName.value = it },
                editable = true
            )
            Spacer(modifier = Modifier.height(Dimens.MarginLarge))

            // Balance Section
            Text(
                text = "Balance",
                fontSize = Dimens.TextxSmall,
                color = Color.White
            )
            OutlinedTextFieldSection(
                label = "",
                value = "${userData?.points ?: 0} points",
                onValueChange = { },
                editable = false
            )
            Spacer(modifier = Modifier.height(Dimens.MarginLarge))

            // Phone Number Section
            Text(
                text = "Phone",
                fontSize = Dimens.TextxSmall,
                color = Color.White
            )
            OutlinedTextFieldSection(
                label = "",
                value = "${userData?.phone}",
                onValueChange = { },
                editable = false
            )
            Spacer(modifier = Modifier.height(Dimens.MarginLarge))

        }
    }

    if (isUserLoggedOut) {
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

    if (isSuccessUpdate) {
        Toast.makeText(context, "Update Successful", Toast.LENGTH_SHORT).show()
        (context as? android.app.Activity)?.finish()
    }

    if (errorMessage.isNotEmpty()) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
    }

    if (isLoading) {
        LoadingScreen()
    }
}
