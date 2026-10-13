package com.simats.tenderanalyser.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

// ==========================================
// 1. ACCOUNT DETAILS (READ-ONLY VIEW)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityAccountDetailsScreen(onBackClick: () -> Unit, onEditClick: () -> Unit) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Account Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = onEditClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EDIT ACCOUNT", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {

            // --- AVATAR SECTION ---
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(modifier = Modifier.size(90.dp).border(2.dp, Gold, CircleShape).padding(4.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                    }
                    Box(modifier = Modifier.size(22.dp).background(Gold, CircleShape).border(2.dp, DarkBg, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Rajesh Kumar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(color = Gold, shape = RoundedCornerShape(4.dp)) {
                    Text("VERIFIED ADMIN", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TENDER OFFICER DETAILS ---
            AccountSectionHeader("TENDER OFFICER DETAILS")
            AccountReadOnlyField("FULL NAME", "Rajesh Kumar")
            AccountReadOnlyField("OFFICIAL DESIGNATION", "Chief Project Manager")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccountReadOnlyField("ADMIN ID", "ADM-2024-8842", modifier = Modifier.weight(1f))
                AccountReadOnlyField("ACCESS LEVEL", "Master Admin", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- DEPARTMENTAL INFORMATION ---
            AccountSectionHeader("DEPARTMENTAL INFORMATION")
            AccountReadOnlyField("AUTHORITY NAME", "Central Public Works Department (CPWD)")
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccountReadOnlyField("OFFICE CODE", "CPWD-CZ-LKO", modifier = Modifier.weight(1f))
                AccountReadOnlyField("PRIMARY SECTOR", "Public Infrastructure", modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- TENDER PROVIDENCE & SECURITY ---
            AccountSectionHeader("TENDER PROVIDENCE & SECURITY")
            AccountReadOnlyField("DSC PUBLIC KEY", "Verified (Expires Oct 2025)", trailingIcon = {
                Icon(Icons.Default.Security, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
            })
            AccountReadOnlyField("VERIFICATION ID", "TX-202-S")
            Spacer(modifier = Modifier.height(16.dp))

            // --- CONTACT INFORMATION ---
            AccountSectionHeader("CONTACT INFORMATION")
            AccountReadOnlyField("OFFICIAL EMAIL", "rajesh.kumar@gov.in")
            AccountReadOnlyField("PRIMARY CONTACT", "+91 98765 43210")

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ==========================================
// 2. EDIT ACCOUNT DETAILS (EDIT MODE)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityEditAccountScreen(onBackClick: () -> Unit, onSaveSuccess: () -> Unit) {
    val context = LocalContext.current

    // States
    var fullName by remember { mutableStateOf("Rajesh Kumar") }
    var designation by remember { mutableStateOf("Chief Project Manager") }
    var authorityName by remember { mutableStateOf("Central Public Works Department (CPWD)") }
    var email by remember { mutableStateOf("rajesh.kumar@gov.in") }
    var phone by remember { mutableStateOf("+91 98765 43210") }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Edit Account Details", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Account Details Updated!", Toast.LENGTH_SHORT).show()
                            onSaveSuccess()
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {

            // --- AVATAR EDIT SECTION ---
            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.clickable { /* Trigger Photo Picker */ }) {
                    Box(modifier = Modifier.size(90.dp).border(2.dp, Gold, CircleShape).padding(4.dp).clip(CircleShape).background(Color.DarkGray), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                    }
                    Box(modifier = Modifier.size(24.dp).background(Gold, CircleShape).border(2.dp, DarkBg, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("CHANGE PHOTO", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- TENDER OFFICER DETAILS ---
            AccountSectionHeader("TENDER OFFICER DETAILS")
            AccountEditableField("FULL NAME", fullName) { fullName = it }
            AccountEditableField("OFFICIAL DESIGNATION", designation) { designation = it }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccountEditableField("ADMIN ID", "ADM-2024-8842", modifier = Modifier.weight(1f), isEnabled = false) { }
                AccountEditableField("ACCESS LEVEL", "Master Admin", modifier = Modifier.weight(1f), isEnabled = false) { }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- DEPARTMENTAL INFORMATION ---
            AccountSectionHeader("DEPARTMENTAL INFORMATION")
            AccountEditableField("AUTHORITY NAME", authorityName, trailingIcon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Gold) }) { authorityName = it }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AccountEditableField("OFFICE CODE", "CPWD-CZ-LKO", modifier = Modifier.weight(1f), isEnabled = false) { }
                AccountEditableField("PRIMARY SECTOR", "Public Infrastructure", modifier = Modifier.weight(1f), isEnabled = false) { }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- CONTACT INFORMATION ---
            AccountSectionHeader("CONTACT INFORMATION")
            AccountEditableField("OFFICIAL EMAIL", email) { email = it }
            AccountEditableField("PRIMARY CONTACT", phone) { phone = it }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// --- Reusable UI Components ---

@Composable
fun AccountSectionHeader(title: String) {
    Text(title, color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun AccountReadOnlyField(label: String, value: String, modifier: Modifier = Modifier, trailingIcon: @Composable (() -> Unit)? = null) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(label, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth().background(CardBg, RoundedCornerShape(8.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            trailingIcon?.invoke()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountEditableField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    trailingIcon: @Composable (() -> Unit)? = null,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(label, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = isEnabled,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = TextGray,
                focusedBorderColor = Gold,
                unfocusedBorderColor = OutlineColor,
                disabledBorderColor = OutlineColor.copy(alpha=0.3f),
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                disabledContainerColor = DarkBg,
                cursorColor = Gold
            ),
            textStyle = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
            trailingIcon = trailingIcon,
            singleLine = true
        )
    }
}