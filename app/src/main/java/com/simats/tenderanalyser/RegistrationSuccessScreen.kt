package com.simats.tenderanalyser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegistrationSuccessScreen(
    onLoginClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(120.dp))

            // Green Checkmark Circle
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF10B981)), // Emerald Green
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Registration\nSuccessful!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A), textAlign = TextAlign.Center, lineHeight = 36.sp)

            Spacer(modifier = Modifier.height(16.dp))

            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color(0xFF64748B))) { append("Thank you for registering with ") }
                withStyle(style = SpanStyle(color = Color(0xFF1B49B5), fontWeight = FontWeight.Bold)) { append("Tender Intelligence Hub") }
                withStyle(style = SpanStyle(color = Color(0xFF64748B))) { append(". Your documents are now being reviewed by our administrative team.") }
            }
            Text(text = annotatedString, fontSize = 15.sp, textAlign = TextAlign.Center, lineHeight = 22.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // Info Box
            Surface(
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color(0xFF1B49B5), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Verification typically takes 24-48 hours. You will be notified once your profile is Tender-Ready.",
                        fontSize = 13.sp, color = Color(0xFF64748B), lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Log In Button
            Button(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B49B5))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Log in", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("ACCESS YOUR ACCOUNT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}