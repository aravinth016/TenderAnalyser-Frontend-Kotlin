package com.simats.tenderanalyser.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.AdminChangePasswordRequest
import com.simats.tenderanalyser.ApiClient
import kotlinx.coroutines.launch

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityChangePasswordScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    // 🚨 ADDED: Scopes and Admin ID retrieval for the API Call
    val scope = rememberCoroutineScope()
    val sharedPref = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
    val adminId = sharedPref.getString("ADMIN_ID", "") ?: ""

    // State for the text fields
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // State for password visibility toggles
    var currentVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    // 🚨 ADDED: Loading state for the API Call
    var isSaving by remember { mutableStateOf(false) }

    // Dynamic Password Requirements Logic
    val hasMinLength = newPassword.length >= 8
    val hasUppercase = newPassword.any { it.isUpperCase() }
    val hasSpecialChar = newPassword.any { !it.isLetterOrDigit() }
    val hasNumeric = newPassword.any { it.isDigit() }

    // Calculate strength score (0 to 4)
    val strengthScore = listOf(hasMinLength, hasUppercase, hasSpecialChar, hasNumeric).count { it }

    val strengthText = when (strengthScore) {
        0 -> "WEAK"
        1 -> "WEAK"
        2 -> "FAIR"
        3 -> "MODERATE"
        4 -> "STRONG"
        else -> "WEAK"
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("CHANGE PASSWORD", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            if (newPassword != confirmPassword) {
                                Toast.makeText(context, "Passwords do not match!", Toast.LENGTH_SHORT).show()
                            } else if (strengthScore < 4) {
                                Toast.makeText(context, "Please meet all password requirements.", Toast.LENGTH_SHORT).show()
                            } else {
                                // 🚨 ADDED: Actual API Call to Python Backend
                                scope.launch {
                                    isSaving = true
                                    try {
                                        val request = AdminChangePasswordRequest(
                                            admin_id = adminId,
                                            currentPassword = currentPassword,
                                            newPassword = newPassword
                                        )
                                        val response = ApiClient.apiService.changeAuthorityPassword(request)

                                        if (response.success) {
                                            Toast.makeText(context, "Password Updated Successfully!", Toast.LENGTH_SHORT).show()
                                            onBackClick()
                                        } else {
                                            Toast.makeText(context, response.message ?: "Failed to update", Toast.LENGTH_LONG).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Network Error. Check Server.", Toast.LENGTH_SHORT).show()
                                    } finally {
                                        isSaving = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        enabled = !isSaving // 🚨 Disable button while loading
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text("UPDATE PASSWORD", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {

            Spacer(modifier = Modifier.height(24.dp))

            // --- INPUT FIELDS ---
            PasswordField(
                label = "CURRENT PASSWORD",
                value = currentPassword,
                placeholder = "Enter current password",
                isVisible = currentVisible,
                onVisibilityChange = { currentVisible = it },
                onValueChange = { currentPassword = it }
            )
            Spacer(modifier = Modifier.height(20.dp))

            PasswordField(
                label = "NEW PASSWORD",
                value = newPassword,
                placeholder = "Enter new password",
                isVisible = newVisible,
                onVisibilityChange = { newVisible = it },
                onValueChange = { newPassword = it }
            )
            Spacer(modifier = Modifier.height(20.dp))

            PasswordField(
                label = "CONFIRM NEW PASSWORD",
                value = confirmPassword,
                placeholder = "Confirm new password",
                isVisible = confirmVisible,
                onVisibilityChange = { confirmVisible = it },
                onValueChange = { confirmPassword = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- STRENGTH INDICATOR ---
            Text("STRENGTH: $strengthText", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                for (i in 1..4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .background(
                                color = if (i <= strengthScore) Gold else OutlineColor,
                                shape = RoundedCornerShape(3.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- REQUIREMENTS CHECKLIST ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, OutlineColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    RequirementItem(text = "At least 8 characters", isMet = hasMinLength)
                    RequirementItem(text = "One uppercase letter", isMet = hasUppercase)
                    RequirementItem(text = "One special character (@, $, !, etc.)", isMet = hasSpecialChar)
                    RequirementItem(text = "One numeric digit", isMet = hasNumeric, isLast = true)
                }
            }

            Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom bar
        }
    }
}

// --- Reusable Components ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordField(
    label: String,
    value: String,
    placeholder: String,
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = OutlineColor, fontSize = 13.sp) },
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
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Gold,
                unfocusedBorderColor = OutlineColor,
                focusedContainerColor = DarkBg,
                unfocusedContainerColor = DarkBg,
                cursorColor = Gold
            ),
            textStyle = TextStyle(fontSize = 14.sp),
            singleLine = true
        )
    }
}

@Composable
private fun RequirementItem(text: String, isMet: Boolean, isLast: Boolean = false) {
    Row(
        modifier = Modifier.padding(bottom = if (isLast) 0.dp else 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircleOutline else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) Gold else TextGray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = if (isMet) Gold else TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}