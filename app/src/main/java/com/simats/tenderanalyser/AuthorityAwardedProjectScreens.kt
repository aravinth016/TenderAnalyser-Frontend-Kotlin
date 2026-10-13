package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val GreenLive = Color(0xFF10B981)
private val OutlineColor = Color(0xFF333333)

// ==========================================
// 1. AWARD DETAILS SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityAwardDetailsScreen(
    tenderId: String,
    onBackClick: () -> Unit,
    onAnalyticsClick: () -> Unit,
    onViewLetterClick: () -> Unit
) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Awarded Details", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) } },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())) {

            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("NIT NO: 2023_AUTH_5102", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Surface(color = Gold.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                    Text("AWARDED", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("IT Infrastructure Upgradation & Cloud Integration", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
            Spacer(modifier = Modifier.height(24.dp))

            // Winner Card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(40.dp).background(Gold.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("CONTRACT WINNER", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Global Tech Solutions", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("CONTRACT ID", color = TextGray, fontSize = 10.sp)
                            Text("CNT-2023-005", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("AWARD DATE", color = TextGray, fontSize = 10.sp)
                            Text("Nov 02, 2023", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Value Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(modifier = Modifier.weight(1.5f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("FINAL CONTRACT VALUE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("₹14.25 Cr", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
                Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SAVING VS ESTIMATE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("5.2%", color = GreenLive, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = GreenLive, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Post Award Progress Timeline
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("POST-AWARD PROGRESS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(24.dp))

                    TimelineItem(title = "Letter of Award Issued", subtitle = "Completed on Nov 04, 2023", isCompleted = true, isLast = false)
                    TimelineItem(title = "Performance Security", subtitle = "Pending - Due by Nov 18", subtitleColor = Gold, isCompleted = false, isLast = false)
                    TimelineItem(title = "Agreement Signing", subtitle = "Awaiting Document Submission", isCompleted = false, isLast = true)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 🚨 FIXED: Adjusted Font Sizes and maxLines to stop wrapping/clipping
            Button(
                onClick = onAnalyticsClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, Gold)
            ) {
                Icon(Icons.Default.Analytics, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SHOW ANALYTICS",
                    color = Gold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onViewLetterClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Gold)
            ) {
                Icon(Icons.Outlined.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DOWNLOAD SIGNED AWARD LETTER",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Helper for Timeline
@Composable
fun TimelineItem(title: String, subtitle: String, subtitleColor: Color = TextGray, isCompleted: Boolean, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(24.dp)) {
            if (isCompleted) {
                Box(modifier = Modifier.size(24.dp).background(Gold, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
            } else {
                Box(modifier = Modifier.size(16.dp).background(Color(0xFF333333), CircleShape))
            }
            if (!isLast) {
                if (isCompleted) {
                    Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(Gold))
                } else {
                    Canvas(modifier = Modifier.width(2.dp).fillMaxHeight()) {
                        drawLine(color = Color(0xFF333333), start = Offset(size.width / 2, 0f), end = Offset(size.width / 2, size.height), strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 32.dp)) {
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = subtitleColor, fontSize = 11.sp)
        }
    }
}

// ==========================================
// 2. BIDDING ANALYTICS SCREEN
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityBiddingAnalyticsScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("BIDDING ANALYTICS", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("IT Infrastructure Upgradation", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp).verticalScroll(rememberScrollState())) {

            // Top Stats
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("PROJECT VALUE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("₹14.25 Cr", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("TOTAL BIDDERS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("08", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Ranking Card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FINANCIAL RANKING (TOP 3)", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    RankBarItem("L1 (WINNER)", "Global Tech Solutions", "₹14.25 Cr", 1.0f, Gold)
                    Spacer(modifier = Modifier.height(20.dp))
                    RankBarItem("L2 RUNNER UP", "Nexus Systems Ltd.", "₹15.80 Cr", 0.8f, Color(0xFF444444))
                    Spacer(modifier = Modifier.height(20.dp))
                    RankBarItem("L3 THIRD BIDDER", "DataSphere Corp", "₹16.45 Cr", 0.7f, Color(0xFF444444))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Verdict Card
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("AI SELECTION VERDICT", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Gold)) { append("Global Tech Solutions") }
                            append(" was identified as the lowest responsive bid.")
                        }, color = Color.White, fontSize = 14.sp, lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    VerdictCheck("100% technical compliance with RFP Clause 4.2 (Cloud redundancy).")
                    VerdictCheck("Financial bid is 5.2% below the estimated project cost.")
                    VerdictCheck("Strong historical performance rating (94/100).")

                    Spacer(modifier = Modifier.height(24.dp))
                    Text("DOWNLOAD FULL REPORT  ↓", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun RankBarItem(rank: String, name: String, value: String, fillFraction: Float, color: Color) {
    Column {
        Text(rank, color = if (color == Gold) Gold else TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(value, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFF2A2A35), RoundedCornerShape(3.dp))) {
            Box(modifier = Modifier.fillMaxWidth(fillFraction).height(6.dp).background(color, RoundedCornerShape(3.dp)))
        }
    }
}

@Composable
fun VerdictCheck(text: String) {
    Row(modifier = Modifier.padding(bottom = 12.dp), verticalAlignment = Alignment.Top) {
        Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = GreenLive, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = TextGray, fontSize = 12.sp, lineHeight = 18.sp)
    }
}

// ==========================================
// 3. SIGNED LETTER VIEWER
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthoritySignedLetterViewerScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = Color(0xFF1E1E1E), // PDF Viewer Background
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("DOCUMENT VIEWER", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("IT Infrastructure Upgradation", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = { IconButton(onClick = { /* Nothing, using close icon */ }) { } },
                actions = { IconButton(onClick = onBackClick) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.background(Color.White.copy(alpha=0.2f), CircleShape).padding(4.dp)) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            // PDF Document
            Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 80.dp), shape = RoundedCornerShape(4.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(modifier = Modifier.padding(24.dp)) {

                        // Header
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Gold, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("DEPARTMENT OF PUBLIC\nINFRASTRUCTURE", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, textAlign = TextAlign.Center)
                            Text("DIGITAL GOVERNANCE AUTHORITY", fontSize = 8.sp, color = Color.Gray, letterSpacing = 2.sp)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Ref: DGA/2023/LOA/5102", fontSize = 8.sp, color = Color.Gray)
                            Text("Date: Nov 04, 2023", fontSize = 8.sp, color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("To,\nThe Director,\nGlobal Tech Solutions Private Ltd.\nInnovation Park, Block 4-B, Sector 62\nNew Delhi - 110001", fontSize = 10.sp, color = Color.Black, lineHeight = 16.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("SUBJECT: LETTER OF AWARD (LOA) FOR IT INFRASTRUCTURE UPGRADATION & CLOUD INTEGRATION (NIT NO: 2023_AUTH_5102)", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Dear Sir/Madam,\n\nWith reference to your bid submitted against the aforementioned tender and subsequent technical and financial evaluations, we are pleased to inform you that the Authority has accepted your offer.\n\nThe total contract value is awarded at ₹14,25,00,000/- (Rupees Fourteen Crores Twenty-Five Lakhs Only) inclusive of all applicable taxes and levies.", fontSize = 10.sp, color = Color.Black, lineHeight = 18.sp)

                        Spacer(modifier = Modifier.height(60.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                            Column {
                                Text("Digitally Signed by:", fontSize = 8.sp, color = Color.Blue)
                                Text("Avinash Kumar, IAS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Blue)
                                Text("Chief Executive Officer", fontSize = 8.sp, color = Color.Gray)
                            }
                            Box(modifier = Modifier.size(60.dp).border(1.dp, Gold, CircleShape), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Verified, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                                    Text("VERIFIED\nDIGITAL SEAL", fontSize = 5.sp, color = Gold, textAlign = TextAlign.Center)
                                }
                            }
                        }
                    }
                }
            }

            // Overlay Buttons at Bottom
            Row(modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().background(Color(0xEE121212)).padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                    Icon(Icons.Outlined.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("DOWNLOAD PDF", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(onClick = { }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, Gold)) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SHARE", color = Gold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}