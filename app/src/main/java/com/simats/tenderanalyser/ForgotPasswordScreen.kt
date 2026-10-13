package com.simats.tenderanalyser

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen() {
    var email by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password", fontWeight = FontWeight.Bold, color = TextDark, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { /* Will connect back button later */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark)
                    }
                },
                actions = { Spacer(modifier = Modifier.width(48.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Forgot Password", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Enter your registered official email address, and we will send you instructions to reset your password.", fontSize = 15.sp, color = TextGray, lineHeight = 22.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Text("REGISTERED EMAIL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("name@company.com", color = TextGray) },
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = "Email", tint = TextGray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White, unfocusedBorderColor = BorderLight, focusedBorderColor = PrimaryBlue),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { /* Send Link */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Send Reset Link", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}