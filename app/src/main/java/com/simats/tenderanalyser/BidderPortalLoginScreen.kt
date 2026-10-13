package com.simats.tenderanalyser

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidderPortalLoginScreen(
    onLoginClick: () -> Unit,
    onForgotClick: () -> Unit,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // State variables for inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Loading state for the API call
    var isLoading by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF1B49B5)
    val textGray = Color(0xFF64748B)

    // Using Scaffold to add a proper app bar
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bidder Portal", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Main Greeting
            Text(
                text = "Welcome Back,",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = primaryBlue,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Sign in to access Tender Analyser Bidder Portal Access",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(48.dp))

            // --- Form Fields ---

            // Email Field
            Column {
                Text(text = "Email Address", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = { Text("name@company.com", color = Color.LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.PersonOutline, contentDescription = null, tint = textGray) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            Column {
                // Row to place 'Forgot?' on the far right aligned with the label
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Password", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    TextButton(onClick = onForgotClick, contentPadding = PaddingValues(0.dp)) {
                        Text("Forgot?", color = primaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("........", color = Color.LightGray) }, // Placeholder dots
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF8FAFC),
                        focusedContainerColor = Color(0xFFF8FAFC)
                    ),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Filled.LockOpen, contentDescription = null, tint = textGray) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle Password Visibility", tint = textGray)
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🚨 LIVE LOGIN BUTTON
            Button(
                onClick = {
                    // 1. Check if fields are empty
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please enter both Email and Password", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    // 2. Call the Python Server securely
                    coroutineScope.launch {
                        try {
                            val request = LoginRequest(email, password)
                            val response = ApiClient.apiService.loginUser(request)

                            isLoading = false

                            if (response.success) {
                                // ==========================================
                                // 🚨 NEW: THE VAULT LOGIC 🚨
                                // Extracted exact user ID, role, and log-in state, saving to unified AppSession
                                // ==========================================
                                val sharedPrefs = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
                                val realUserId = response.user?.id?.toString() ?: "1"

                                sharedPrefs.edit().apply {
                                    putBoolean("IS_LOGGED_IN", true)
                                    putString("USER_ROLE", "bidder")
                                    putString("USER_ID", realUserId)
                                }.commit() // .commit() forces immediate disk write
                                // ==========================================

                                Toast.makeText(context, "Welcome back!", Toast.LENGTH_SHORT).show()
                                // 3. ONLY navigate if Python says success = true!
                                onLoginClick()
                            } else {
                                Toast.makeText(context, response.message ?: "Invalid Credentials", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            isLoading = false
                            Toast.makeText(context, "Server Error: Could not connect to database", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(8.dp), ambientColor = primaryBlue, spotColor = primaryBlue),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(8.dp),
                enabled = !isLoading // Disable button while loading to prevent spam clicks
            ) {
                // Show Spinner while loading, otherwise show normal text
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Log in", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Filled.ArrowForward, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }

            // A spacer to push the footer to the very bottom of the screen
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // --- Footer Links ---

            // Registration Line
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Don't have a bidder account?", fontSize = 11.sp, color = textGray)
                TextButton(onClick = onRegisterClick, contentPadding = PaddingValues(start = 4.dp)) {
                    Text("Register Now", color = primaryBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy and Support Links
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                val linkModifier = Modifier.padding(horizontal = 4.dp)
                val dot = Modifier.padding(horizontal = 4.dp)

                Text("Privacy Policy", color = primaryBlue, fontSize = 10.sp, modifier = linkModifier)
                Text("·", color = textGray, modifier = dot)
                Text("Terms of Service", color = primaryBlue, fontSize = 10.sp, modifier = linkModifier)
                Text("·", color = textGray, modifier = dot)
                Text("Help", color = primaryBlue, fontSize = 10.sp, modifier = linkModifier)
            }
        }
    }
}