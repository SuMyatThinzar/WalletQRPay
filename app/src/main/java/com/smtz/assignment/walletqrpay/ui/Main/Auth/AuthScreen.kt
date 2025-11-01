package com.smtz.assignment.walletqrpay.ui.Main.Auth

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smtz.assignment.walletqrpay.R
import com.smtz.assignment.walletqrpay.data.repository.AuthRepository
import com.smtz.assignment.walletqrpay.ui.Home.*
import com.smtz.assignment.walletqrpay.ui.Loading.LoadingScreen
import com.smtz.assignment.walletqrpay.ui.theme.*
import com.smtz.assignment.walletqrpay.util.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen() {

    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(AuthRepository(), UserPreferences(LocalContext.current.applicationContext)))

    val context = LocalContext.current
    val isLogin by authViewModel.isLogin
    val isLoading by authViewModel.isLoading
    val isLoginSuccess by authViewModel.isLoginSuccess
    val inputPhoneNumber by authViewModel.inputPhoneNumber         // only used in UI input
//    val formattedPhoneNumber by authViewModel.formattedPhoneNumber // only used in formatting and validation after button clicked
    val errorMessage by authViewModel.errorMessage
    val password by authViewModel.password
    val userName by authViewModel.userName
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isLoginSuccess) {
        if (isLoginSuccess) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish() // remove Auth from back stack
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isLogin) "Login" else "Sign Up",
                fontSize = Dimens.TextHeading,
                color = Color.White
            )

            // User Name (only for Sign up)
            if (!isLogin) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { authViewModel.userName.value = it },
                    label = { Text("User Name") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White,
                        cursorColor = Color.White,
                        focusedLabelColor = Color.White,
                        unfocusedLabelColor = Color.White
                    )
                )
            }

            // Phone Number
            OutlinedTextField(
                value = inputPhoneNumber,
                onValueChange = { authViewModel.inputPhoneNumber.value = it },
                label = { Text("Phone Number") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                )
            )

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { authViewModel.password.value = it },
                label = { Text("Password") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password
                ),
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color.White
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) painterResource(id = R.drawable.ic_visibility) else painterResource(id = R.drawable.ic_visibility_off)
                    val description = if (passwordVisible) "Hide password" else "Show password"
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = image, contentDescription = description)
                    }
                }
            )

            // Error Text
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontSize = Dimens.TextSmall
                )
            }

            // Login/Sign Up Button
            Button(
                onClick = {
                    if (authViewModel.validatePhone() && authViewModel.validatePassword()) {
                        if (isLogin) {
                            authViewModel.loginUser()
                        } else {
                            authViewModel.signupUser()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = if (isLogin) "Login" else "Sign Up")
            }

            // Toggle button between Login and Sign Up
            TextButton(onClick = { authViewModel.toggleLoginSignup() }) {
                Text(
                    text = if (isLogin) "Don't have an account? Sign Up" else "Already have an account? Login",
                    color = Color.White
                )
            }

        }

        // Loading View with overlay fullscreen
        if (isLoading) {
            LoadingScreen()
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAuthScreen() {
    WalletQRPayTheme {
        AuthScreen()
    }
}