package com.yg.mileage.auth

// Added imports for Microsoft OAuth sign-in
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.OAuthProvider
import com.yg.mileage.R
import com.yg.mileage.ui.theme.MyMileage
import com.yg.mileage.ui.theme.RobotoFlex

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SignInScreen(
    onEmailSignInClick: (String, String) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phoneLoginMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MyMileage) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_round),
                contentDescription = "MyMileage Logo",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Log in", fontFamily = RobotoFlex, fontWeight = FontWeight.Black, fontSize = 24.sp)
                    Text("Sign in to continue.", fontSize = 16.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { onEmailSignInClick(email, password) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MyMileage)
                    ) {
                        Text("Log in")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 1.dp, color = MyMileage)
                    Text("Or sign in with,", fontWeight = FontWeight.Bold, color = MyMileage)
                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        IconButton(
                            onClick = onGoogleSignInClick,
                            colors = IconButtonDefaults.iconButtonColors(containerColor = MyMileage),
                            shape = MaterialShapes.Square.toShape()
                        ) {
                            Image(
                                painterResource(id = R.drawable.google_2025_g_logo),
                                contentDescription = "Google"
                            )
                        }
                        IconButton(
                            onClick = { phoneLoginMessage = "Phone Login Under Development" },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(0.dp),
                        ) {
                            Image(
                                imageVector = Icons.Rounded.Call,
                                contentDescription = "Phone",
                            )
                        }
                        // Microsoft sign-in button in SignInScreen.kt
                        IconButton(
                            onClick = {
                                val activity = context as? Activity
                                if (activity == null) {
                                    phoneLoginMessage = "Unable to start Microsoft sign-in (no Activity)"
                                } else {
                                    val auth = FirebaseAuth.getInstance()

                                    // Check for pending auth result first
                                    val pending = auth.pendingAuthResult
                                    if (pending != null) {
                                        pending
                                            .addOnSuccessListener { result ->
                                                val user = result.user
                                                phoneLoginMessage = "Signed in: ${user?.email ?: user?.displayName ?: "(no-email)"}"
                                                Log.d("SignInScreen", "Microsoft sign-in successful: ${user?.uid}")
                                            }
                                            .addOnFailureListener { exception ->
                                                val errorMessage = handleMicrosoftAuthError(exception)
                                                phoneLoginMessage = errorMessage
                                                Log.e("SignInScreen", "Microsoft sign-in failed", exception)
                                            }
                                    } else {
                                        // Build the provider
                                        val provider = OAuthProvider.newBuilder("microsoft.com").apply {
                                            scopes = listOf("User.Read")
                                            addCustomParameter("prompt", "select_account")
                                            // Try "common" if "consumers" doesn't work
                                            addCustomParameter("tenant", "consumers")
                                        }.build()

                                        // Start the sign-in flow
                                        auth.startActivityForSignInWithProvider(activity, provider)
                                            .addOnSuccessListener { authResult ->
                                                val user = authResult.user
                                                phoneLoginMessage = "Signed in: ${user?.email ?: user?.displayName ?: "(no-email)"}"
                                                Log.d("SignInScreen", "Microsoft sign-in successful: ${user?.uid}")
                                            }
                                            .addOnFailureListener { exception ->
                                                val errorMessage = handleMicrosoftAuthError(exception)
                                                phoneLoginMessage = errorMessage
                                                Log.e("SignInScreen", "Microsoft sign-in failed", exception)
                                            }
                                    }
                                }
                            },
                            colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(0.dp),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.microsoft_logo),
                                contentDescription = "Microsoft",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(2.dp)
                            )
                        }
                    }
                    phoneLoginMessage?.let {
                        Text(
                            text = it,
                            color = MyMileage,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { /* Handle forgot password */ },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Forgot Password?",
                        )
                    }
                }
            }
        }
    }
}

// Helper function to handle Microsoft authentication errors
private fun handleMicrosoftAuthError(exception: Exception): String {
    return if (exception is FirebaseAuthException) {
        when (exception.errorCode) {
            "ERROR_INVALID_CREDENTIAL" -> {
                Log.e("SignInScreen", "Invalid credential details: ${exception.message}")
                "Invalid Microsoft credentials. Please check Firebase and Azure AD configuration."
            }
            "ERROR_OPERATION_NOT_ALLOWED" -> "Microsoft sign-in is not enabled in Firebase Console."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with this email using a different sign-in method."
            "ERROR_WEB_CONTEXT_CANCELED" -> "Sign-in cancelled by user."
            else -> "Microsoft sign-in failed: ${exception.errorCode} - ${exception.message}"
        }
    } else {
        "Microsoft sign-in failed: ${exception.localizedMessage ?: exception.message}"
    }
}

@Preview(showBackground = true)
@Composable
fun SignInScreenPreview() {
    SignInScreen(onEmailSignInClick = { _, _ -> }, onGoogleSignInClick = {})
}
