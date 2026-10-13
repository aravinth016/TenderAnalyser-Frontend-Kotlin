package com.simats.tenderanalyser.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 🚨 Added import for your ViewModel
import com.simats.tenderanalyser.BidderSettingsViewModel

// --- Bidder Theme Colors ---
private val PrimaryBlue = Color(0xFF1D4ED8)
private val SuccessGreen = Color(0xFF10B981)
private val TextDark = Color(0xFF1F2937)
private val TextGray = Color(0xFF6B7280)
private val BorderGray = Color(0xFFE5E7EB)
private val BgWhite = Color(0xFFFFFFFF)

// ==========================================
// 1. CHANGE PASSWORD FORM
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidderChangePasswordScreen(
    viewModel: BidderSettingsViewModel, // 🚨 Added ViewModel
    userId: String,                     // 🚨 Added userId
    onBackClick: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    // State for text fields
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State for visibility toggles
    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    // Dynamic Password Requirements
    val hasMinLength = newPassword.length >= 12
    val hasUppercase = newPassword.any { it.isUpperCase() }
    val hasNumeric = newPassword.any { it.isDigit() }
    val hasSpecialChar = newPassword.any { !it.isLetterOrDigit() }

    // Calculate strength (0 to 4)
    val strengthScore = listOf(hasMinLength, hasUppercase, hasNumeric, hasSpecialChar).count { it }

    // 🚨 Listen for backend errors and show them via Toast
    LaunchedEffect(viewModel.errorMessage) {
        viewModel.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError() // clear it so it doesn't fire repeatedly
        }
    }

    Scaffold(
        containerColor = BgWhite,
        topBar = {
            TopAppBar(
                title = { Text("Change Password", color = TextDark, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgWhite)
            )
        },
        bottomBar = {
            Surface(color = BgWhite, shadowElevation = 16.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        enabled = !viewModel.isLoading, // 🚨 Disable button while loading
                        onClick = {
                            if (currentPassword.isEmpty()) {
                                Toast.makeText(context, "Enter current password.", Toast.LENGTH_SHORT).show()
                            } else if (newPassword != confirmPassword) {
                                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                            } else if (strengthScore < 4) {
                                Toast.makeText(context, "Please meet all security requirements.", Toast.LENGTH_SHORT).show()
                            } else {
                                // 🚨 Talk to the ViewModel to handle the API call
                                viewModel.changePassword(
                                    userId = userId,
                                    currentPass = currentPassword,
                                    newPass = newPassword,
                                    onSuccess = onSuccess
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            disabledContainerColor = PrimaryBlue.copy(alpha = 0.6f) // Faded blue when loading
                        )
                    ) {
                        // 🚨 Show Spinner or Text based on loading state
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Update Password", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Set a strong password to protect your corporate tender account and sensitive business data.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- INPUT FIELDS ---
            BidderPasswordField(
                label = "CURRENT PASSWORD", value = currentPassword, placeholder = "Enter current password",
                isVisible = currentVisible, onVisibilityChange = { currentVisible = it }, onValueChange = { currentPassword = it }
            )
            Spacer(modifier = Modifier.height(16.dp))

            BidderPasswordField(
                label = "NEW PASSWORD", value = newPassword, placeholder = "Enter new password",
                isVisible = newVisible, onVisibilityChange = { newVisible = it }, onValueChange = { newPassword = it }
            )

            // --- STRENGTH INDICATOR ---
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("STRENGTH", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                if (strengthScore == 4) {
                    Text("STRONG", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 1..4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                color = if (i <= strengthScore) {
                                    if (strengthScore == 4) SuccessGreen else PrimaryBlue
                                } else BorderGray,
                                shape = RoundedCornerShape(2.dp)
                            )
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            BidderPasswordField(
                label = "CONFIRM NEW PASSWORD", value = confirmPassword, placeholder = "Confirm new password",
                isVisible = confirmVisible, onVisibilityChange = { confirmVisible = it }, onValueChange = { confirmPassword = it }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // --- SECURITY CHECKLIST ---
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF9FAFB), // Very light gray for checklist bg
                border = BorderStroke(1.dp, BorderGray)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("SECURITY CHECKLIST", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    BidderRequirementItem("Minimum 12 characters", hasMinLength)
                    BidderRequirementItem("At least one uppercase letter", hasUppercase)
                    BidderRequirementItem("Includes numbers (0-9)", hasNumeric)
                    BidderRequirementItem("One special character (@, #, $)", hasSpecialChar, isLast = true)
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ==========================================
// 2. SUCCESS SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidderPasswordSuccessScreen(onDoneClick: () -> Unit) {
    Scaffold(
        containerColor = BgWhite,
        topBar = {
            TopAppBar(
                title = { Text("Security", color = TextDark, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onDoneClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextDark) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BgWhite)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Big Green Checkmark
            Box(
                modifier = Modifier.size(100.dp).background(SuccessGreen.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(70.dp).background(SuccessGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Success", tint = Color.White, modifier = Modifier.size(40.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Password Updated\nSuccessfully!",
                color = TextDark,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Your account security has been updated. Please use your new password for future logins.",
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Abstract decorative graphic placeholder
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF3F4F6).copy(alpha=0.5f)).border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Done", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}

// --- Reusable UI Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BidderPasswordField(
    label: String, value: String, placeholder: String,
    isVisible: Boolean, onVisibilityChange: (Boolean) -> Unit, onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray, fontSize = 14.sp) },
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { onVisibilityChange(!isVisible) }) {
                    Icon(image, contentDescription = "Toggle Visibility", tint = TextGray)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextDark,
                unfocusedTextColor = TextDark,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = BorderGray,
                focusedContainerColor = BgWhite,
                unfocusedContainerColor = BgWhite,
                cursorColor = PrimaryBlue
            ),
            textStyle = TextStyle(fontSize = 15.sp),
            singleLine = true
        )
    }
}

@Composable
private fun BidderRequirementItem(text: String, isMet: Boolean, isLast: Boolean = false) {
    Row(
        modifier = Modifier.padding(bottom = if (isLast) 0.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) SuccessGreen else TextGray.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = if (isMet) TextDark else TextGray,
            fontSize = 13.sp,
            fontWeight = if (isMet) FontWeight.Medium else FontWeight.Normal
        )
    }
}