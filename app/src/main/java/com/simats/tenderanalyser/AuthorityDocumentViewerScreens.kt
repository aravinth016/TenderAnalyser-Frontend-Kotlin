package com.simats.tenderanalyser.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Theme Colors ---
private val DarkTopBar = Color(0xFF0F172A)
private val DocBg = Color(0xFFF8FAFC)
private val GreenVerified = Color(0xFF10B981)
private val BlueHighlight = Color(0xFF3B82F6)
private val Gold = Color(0xFFFFC107)
private val TextBlack = Color(0xFF1E293B)
private val TextGray = Color(0xFF64748B)

// Router Composable
@Composable
fun AuthorityDocumentViewerScreen(docType: String, onBackClick: () -> Unit) {
    when (docType) {
        "technical" -> TechnicalProposalViewer(onBackClick)
        "financial" -> FinancialAuditViewer(onBackClick)
        "emd" -> EmdReceiptViewer(onBackClick)
        else -> TechnicalProposalViewer(onBackClick) // Fallback
    }
}

// ==========================================
// 1. TECHNICAL PROPOSAL VIEWER
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TechnicalProposalViewer(onBackClick: () -> Unit) {
    Scaffold(
        topBar = { ViewerTopBar("Technical_Proposal_GlobalIn...", "VERIFIED DOCUMENT", onBackClick) },
        containerColor = DocBg
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // Main Document Content
            Column(modifier = Modifier.fillMaxSize().padding(24.dp).background(Color.White).padding(24.dp).verticalScroll(rememberScrollState())) {
                Text("TECHNICAL PROPOSAL", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Project: Global Infrastructure Development\nPhase IV", fontSize = 12.sp, color = TextGray)

                Spacer(modifier = Modifier.height(32.dp))

                Text("1. EXECUTIVE SUMMARY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text("This proposal outlines the strategic approach for the infrastructure project, focusing on sustainability, regional connectivity, and advanced engineering practices. Global Infra Ltd brings over 25 years of experience in diverse geographical terrains...", fontSize = 12.sp, color = TextBlack, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("2. TERRAIN EXPERTISE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BlueHighlight, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BlueHighlight, modifier = Modifier.size(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(buildAnnotatedString {
                    append("Our specialized team possesses extensive ")
                    withStyle(style = SpanStyle(background = BlueHighlight.copy(alpha = 0.2f), color = BlueHighlight, fontWeight = FontWeight.Bold)) { append("experience in hilly terrains") }
                    append(", having completed over 14 projects in the Himalayan belt and Andean range. We employ proprietary landslide mitigation techniques...")
                }, fontSize = 12.sp, color = TextBlack, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(24.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("3. RESOURCE MOBILIZATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenVerified, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = GreenVerified, modifier = Modifier.size(12.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(buildAnnotatedString {
                    append("Detailed below is our comprehensive ")
                    withStyle(style = SpanStyle(background = GreenVerified.copy(alpha = 0.2f), color = GreenVerified, fontWeight = FontWeight.Bold)) { append("equipment inventory") }
                    append(" specifically allocated for this tender. This includes 24 heavy-duty excavators, 8 specialized rock drills...")
                }, fontSize = 12.sp, color = TextBlack, lineHeight = 20.sp)

                Spacer(modifier = Modifier.height(60.dp))
            }

            // AI Insights Floating Card
            Card(
                modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp, top = 16.dp).width(180.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = BlueHighlight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI Insights", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("HILLY TERRAINS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Text("98%", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = BlueHighlight)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(0.98f).height(4.dp).background(BlueHighlight, RoundedCornerShape(2.dp)))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("EQUIPMENT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextGray)
                        Text("92%", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = GreenVerified)
                    }
                    Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(Color(0xFFE2E8F0), RoundedCornerShape(2.dp))) {
                        Box(modifier = Modifier.fillMaxWidth(0.92f).height(4.dp).background(GreenVerified, RoundedCornerShape(2.dp)))
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. FINANCIAL AUDIT VIEWER
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialAuditViewer(onBackClick: () -> Unit) {
    Scaffold(
        topBar = { ViewerTopBar("Financial_Audit_GlobalIn...", "VERIFIED", onBackClick) },
        containerColor = DocBg
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState())) {

            // AI Insights Top Box
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRoundRect(color = Gold, style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)), cornerRadius = CornerRadius(12.dp.toPx()))
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("AI FINANCIAL INSIGHTS", fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("3Y AVG\nTURNOVER", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                            Text("₹450 Cr", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack)
                        }
                        Column {
                            Text("NET WORTH", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = GreenVerified, modifier = Modifier.size(14.dp))
                                Text("Positive", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = GreenVerified)
                            }
                        }
                        Column {
                            Text("AUDITOR SEAL", fontSize = 9.sp, color = TextGray, fontWeight = FontWeight.Bold)
                            Text("Verified", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = GreenVerified)
                        }
                    }
                }
            }

            // Document Content
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(8.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                        Text("INDEPENDENT\nAUDITOR'S REPORT", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack, lineHeight = 20.sp)
                        Surface(color = DarkTopBar, shape = RoundedCornerShape(4.dp)) { Text("CONFIDENTIAL", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("To the Directors of Global Infra Ltd.", fontSize = 10.sp, color = TextGray)

                    Spacer(modifier = Modifier.height(32.dp))
                    Text("BALANCE SHEET SUMMARY (FY 2021-23)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Table Header
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                        Text("Particulars", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.weight(2f))
                        Text("2023 (₹ Cr)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextBlack, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text("2022 (₹ Cr)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Table Rows
                    FinancialTableRow("A. Non-current Assets", "1,240.50", "1,112.20")
                    FinancialTableRow("B. Current Assets", "582.15", "490.45")
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text("TOTAL ASSETS (A+B)", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack, modifier = Modifier.weight(2f))
                        Text("1,822.65", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        Text("1,602.65", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    FinancialTableRow("C. Equity & Reserves", "845.00", "720.00")

                    Spacer(modifier = Modifier.height(40.dp))

                    // Auditor Seal Box
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                        Box(modifier = Modifier.size(70.dp).border(1.dp, TextGray.copy(alpha = 0.5f), RoundedCornerShape(35.dp)), contentAlignment = Alignment.Center) {
                            Text("CERTIFIED\nAUDITOR\nSEAL", fontSize = 7.sp, textAlign = TextAlign.Center, color = TextGray)
                        }
                    }

                    // 🚨 FIXED: Padding moved properly into the Modifier chain!
                    Text(
                        text = "UDIN: 23145021B218",
                        fontSize = 7.sp,
                        color = TextGray,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, end = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun FinancialTableRow(col1: String, col2: String, col3: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(col1, fontSize = 9.sp, color = TextBlack, modifier = Modifier.weight(2f))
        Text(col2, fontSize = 9.sp, color = TextBlack, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text(col3, fontSize = 9.sp, color = TextGray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

// ==========================================
// 3. EMD RECEIPT VIEWER
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmdReceiptViewer(onBackClick: () -> Unit) {
    Scaffold(
        topBar = { ViewerTopBar("EMD_Receipt_SHSR.pdf", "", onBackClick, true) },
        containerColor = DocBg
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {

            // Top Status Info
            Spacer(modifier = Modifier.height(16.dp))
            Surface(color = DarkTopBar, shape = RoundedCornerShape(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Gold, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI VERIFICATION", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Transaction ID: TXN99281 (Verified)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GreenVerified)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text("Amount: ₹85,00,000 (Matched)", fontSize = 10.sp, color = GreenVerified, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.width(16.dp))
                Text("Issuing Bank: SBI", fontSize = 10.sp, color = TextGray)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // The Receipt Card
            Card(modifier = Modifier.fillMaxWidth(0.9f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Bank Transaction\nReceipt", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack, lineHeight = 22.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("STATE BANK OF INDIA", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("REF NO.", fontSize = 8.sp, color = TextGray)
                            Text("TXN992814402", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("DATE & TIME", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Text("24 Oct 2023, 14:22:10", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("PAYMENT MODE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Text("RTGS / NEFT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SENDER NAME", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Text("SHSR Infra Projects\nLtd.", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextBlack)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("STATUS", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextGray)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = GreenVerified, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SUCCESS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenVerified)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL AMOUNT PAID", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextGray, letterSpacing = 1.sp)
                        Text("₹85,00,000.00", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextBlack)
                        Text("Eighty Five Lakhs Rupees Only", fontSize = 9.sp, color = TextLightGray)

                        Spacer(modifier = Modifier.height(32.dp))
                        Box(modifier = Modifier.size(60.dp).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(4.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.QrCode2, contentDescription = "QR Code", tint = TextGray, modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("This is a system generated receipt and does not require a physical signature.", fontSize = 7.sp, color = TextLightGray, textAlign = TextAlign.Center)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
private val TextLightGray = Color(0xFFCBD5E1)

// --- Reusable Top Bar ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewerTopBar(title: String, badge: String, onBackClick: () -> Unit, showShare: Boolean = false) {
    TopAppBar(
        title = {
            Column {
                Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                if (badge.isNotEmpty()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenVerified, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(badge, color = GreenVerified, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }
        },
        actions = {
            if (showShare) {
                IconButton(onClick = { }) { Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White) }
            } else {
                IconButton(onClick = { }) { Icon(Icons.Default.MoreHoriz, contentDescription = "Options", tint = Color.White) }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkTopBar)
    )
}