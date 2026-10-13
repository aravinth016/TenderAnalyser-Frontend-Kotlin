package com.simats.tenderanalyser.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatBold
import androidx.compose.material.icons.filled.FormatItalic
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.FormatUnderlined
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

// ==========================================
// 1. GENERATE AWARD LETTER
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityGenerateAwardScreen(onBackClick: () -> Unit, onPreviewClick: () -> Unit, onGenerateClick: () -> Unit) {
    var letterBody by remember { mutableStateOf("Subject: Letter of Award for Smart Railway Station Modernization Phase I\n\nDear Directors of Global Infra Ltd,\n\nWe are pleased to inform you that your bid for the project \"Smart Railway Station\" has been evaluated as the most competitive (L1). Consequently, the Authority hereby awards the contract to Global Infra Ltd at the total value of ₹14,25,00,000 (Fourteen Crores Twenty-Five Lakhs Only).\n\nAs per the tender requirements, you are requested to...") }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Generate Award Letter", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) } },
                actions = { Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Gold, modifier = Modifier.padding(end = 16.dp)) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {

            // Recipient Card
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, OutlineColor)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Column {
                            Text("RECIPIENT", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Global Infra Ltd", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Surface(color = Gold.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                            Text("OFFICIAL AWARD", color = Gold, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("PROJECT", color = TextGray, fontSize = 10.sp)
                            Text("Smart Railway Station", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("AWARDED VALUE", color = TextGray, fontSize = 10.sp)
                            Text("₹14,25,00,000", color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Letter Editor
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("LETTER BODY", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Template: Official_Award_V2.0", color = TextGray, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                Column {
                    // Fake Toolbar
                    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF252530)).padding(8.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(Icons.Default.FormatBold, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                        Icon(Icons.Default.FormatItalic, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                        Icon(Icons.Default.FormatUnderlined, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                        Icon(Icons.Default.FormatListBulleted, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    }
                    OutlinedTextField(
                        value = letterBody, onValueChange = { letterBody = it },
                        modifier = Modifier.fillMaxWidth().height(250.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp, lineHeight = 20.sp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Authentication Box
            Text("OFFICIAL AUTHENTICATION", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Card(modifier = Modifier.weight(1f).height(80.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Upload Seal/Signature", color = Color.White, fontSize = 10.sp)
                    }
                }
                Card(modifier = Modifier.weight(1f).height(80.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A35)), border = BorderStroke(1.dp, Gold)) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.VpnKey, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Use Saved Digital\nSignature", color = Color.White, fontSize = 10.sp, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PKI Encryption Enabled for Issuance", color = Color(0xFF10B981), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Bottom Buttons
            Button(
                onClick = onGenerateClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate & Issue Award", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onPreviewClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, Gold)
            ) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Preview PDF", color = Gold, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ==========================================
// 2. PREVIEW AWARD LETTER (PDF VIEW)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityPreviewAwardScreen(onBackClick: () -> Unit, onIssueClick: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFFE2E8F0), // Light grey background to mimic a PDF viewer
        topBar = {
            TopAppBar(
                title = { Text("Preview Award Letter", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) } },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

            // The Paper Document
            Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), shape = RoundedCornerShape(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Header
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Row {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("OFFICE OF THE\nCHIEF ENGINEER\nDEPARTMENT OF INFRASTRUCTURE &\nRAILWAYS", fontSize = 6.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 8.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Ref:\nSTA/AWARD/2024/002", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Color.Black, textAlign = TextAlign.End)
                            Text("Date: October 24, 2023", fontSize = 8.sp, color = Color.DarkGray)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("LETTER OF AWARD", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Project: Smart Railway Station Modernization (Phase I)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("To,\nThe Managing Director,\nGlobal Infra Ltd,\nTech Park North, Sector 62,\nNew Delhi - 110001", fontSize = 10.sp, color = Color.Black, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Subject: Notification of Award for Tender ID: CPWD-2023-RS01-Smart", fontSize = 10.sp, fontWeight = FontWeight.Bold, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, color = Color.Black)

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Dear Sir/Madam, This is to notify you that your bid dated September 12, 2023, for execution of the \"Smart Railway Station\" project has been accepted by the competent authority.", fontSize = 10.sp, color = Color.Black, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("The total awarded contract value is ₹14,25,00,000 (Rupees Fourteen Crores Twenty-Five Lakhs Only) including all applicable taxes and statutory levies.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black, lineHeight = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You are hereby requested to furnish the Performance Security within 14 (fourteen) days in accordance with the Conditions of Contract...", fontSize = 10.sp, color = Color.Black, lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(40.dp))
                    Text("Page 1 of 1", fontSize = 8.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }

            // Bottom Floating Actions
            Button(onClick = onIssueClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Issue Award Letter", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White), border = BorderStroke(1.dp, Color.Black)) {
                Icon(Icons.Outlined.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download PDF", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// ==========================================
// 3. AWARD ISSUED SUCCESS
// ==========================================
@Composable
fun AuthorityAwardSuccessScreen(onHomeClick: () -> Unit) {
    Scaffold(containerColor = DarkBg) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

            Box(modifier = Modifier.size(100.dp).background(Gold, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text("AWARD ISSUED!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.width(60.dp).height(4.dp).background(Gold))

            Spacer(modifier = Modifier.height(40.dp))

            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, OutlineColor)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("RECIPIENT", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Global Infra Ltd", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("PROJECT", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Smart Railway Station", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("AWARD ID", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("AWD - 2024 - 001", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("The official award letter has been digitally signed and sent to the bidder via secure channel.", color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))

            Spacer(modifier = Modifier.height(40.dp))

            Button(onClick = { }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                Icon(Icons.Default.Dashboard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Go to Project Hub", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onHomeClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, OutlineColor)) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Return to Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}