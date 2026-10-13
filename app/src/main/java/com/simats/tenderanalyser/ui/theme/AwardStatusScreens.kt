package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border

// ==========================================
// SCREEN 1: AWARDED STATUS & RANKING
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AwardedStatusScreen(
    onBackClick: () -> Unit,
    onDownloadClick: () -> Unit // Navigate to AwardLetterPreviewScreen
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF0A1931)).statusBarsPadding()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column(modifier = Modifier.padding(start = 8.dp)) {
                        Text("Construction of Smart High-Speed\nRailway Station", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("PROJECT ID: RAIL/2024/SHS/982", color = Color.White.copy(0.7f), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {

            // 1. Status Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE6FDF4)), // Mint Green
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(48.dp).background(Color(0xFF10B981), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(28.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("STATUS: AWARDED", fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF10B981))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Congratulations! Your proposal has been\nselected.", textAlign = TextAlign.Center, fontSize = 13.sp, color = Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Financial Comparison Card
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("FINANCIAL COMPARISON", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Your Bid
                        Column(modifier = Modifier.weight(1f).background(Color(0xFFF4F6FF), RoundedCornerShape(8.dp)).padding(16.dp)) {
                            Text("YOUR BID VALUE", fontSize = 9.sp, color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹38.45 Cr", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                        }
                        // Winning Bid
                        Column(modifier = Modifier.weight(1f).background(Color(0xFFECFDF5), RoundedCornerShape(8.dp)).padding(16.dp)) {
                            Text("WINNING BID (L1)", fontSize = 9.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("₹38.45 Cr", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CheckCircleOutline, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Your bid matched the lowest evaluated price.", fontSize = 11.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Final Ranking Card
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("FINAL RANKING", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    // L1 (Winner)
                    RankingItemRow(rank = "L1", name = "Your Company", status = "Winner", price = "₹38.45 Cr", isWinner = true)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                    // L2
                    RankingItemRow(rank = "L2", name = "Skyline Infra Ltd.", status = "Qualified", price = "₹41.20 Cr", isWinner = false)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                    // L3
                    RankingItemRow(rank = "L3", name = "Global Builders Corp.", status = "Qualified", price = "₹43.85 Cr", isWinner = false)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. Download Award Letter Button
            Button(
                onClick = onDownloadClick,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)) // Blue
            ) {
                Icon(Icons.Outlined.FileDownload, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Award Letter", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "LEGALLY BINDING DOCUMENT AS PER SECTION 4 OF\nTHE DIGITAL TENDERS ACT.",
                textAlign = TextAlign.Center, fontSize = 8.sp, color = Color.LightGray, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun RankingItemRow(rank: String, name: String, status: String, price: String, isWinner: Boolean) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).background(if (isWinner) Color(0xFF10B981) else Color(0xFFF1F5F9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(rank, fontWeight = FontWeight.Bold, color = if (isWinner) Color.White else Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Black)
            Text(status, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = if (isWinner) Color(0xFF10B981) else Color.Gray)
        }
        Text(price, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
    }
}


// ==========================================
// SCREEN 2: AWARD LETTER PREVIEW (LOA)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AwardLetterPreviewScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF0A1931)).statusBarsPadding()) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                        Text("Award Letter - SHSR 2024", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    IconButton(onClick = { /* Share Logic */ }) {
                        Icon(Icons.Outlined.Share, "Share", tint = Color.White)
                    }
                }
            }
        },
        containerColor = Color(0xFFF1F5F9) // Light gray background to make the white document pop
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            // --- THE DOCUMENT ---
            Column(modifier = Modifier.fillMaxSize().padding(16.dp).padding(bottom = 100.dp).verticalScroll(rememberScrollState())) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp)) {

                        // WATERMARK (Rotated text in background)
                        Text(
                            text = "APPROVED",
                            color = Color(0xFF10B981).copy(alpha = 0.1f),
                            fontSize = 68.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.align(Alignment.Center).rotate(-45f)
                        )

                        // DOCUMENT CONTENT
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Header Row
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(modifier = Modifier.size(64.dp).background(Color(0xFFF4F6FF), CircleShape).border(1.dp, Color(0xFFE2E8F0), CircleShape), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Outlined.AccountBalance, null, tint = Color(0xFF64748B), modifier = Modifier.size(32.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("GOVERNMENT AUTHORITY", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("CENTRAL PUBLIC WORKS DEPT", fontWeight = FontWeight.ExtraBold, fontSize = 11.sp, color = Color.Black)
                                    Text("Regional Office - South Zone\nNirman Bhawan, New Delhi", textAlign = TextAlign.End, fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // Ref & Date
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Ref: CPWD/2024/AW-8921", fontSize = 11.sp, color = Color.DarkGray)
                                Text("Date: Oct 24, 2023", fontSize = 11.sp, color = Color.DarkGray)
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            // To Address
                            Text("To,", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                            Text("Your Construction Company Pvt. Ltd.", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E40AF))
                            Text("Corporate Suite 402, Business Hub,\nMumbai, Maharashtra - 400001", fontSize = 11.sp, color = Color.DarkGray, lineHeight = 16.sp)

                            Spacer(modifier = Modifier.height(24.dp))

                            // Title
                            Text("LETTER OF ACCEPTANCE (LOA)", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color.Black, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, textDecoration = TextDecoration.Underline)

                            Spacer(modifier = Modifier.height(24.dp))

                            // Subject
                            Text(
                                "Subject: Widening and Strengthening of Highway (SHSR 2024) - Section 4A - North Corridor.",
                                fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black, lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Body Paragraph 1
                            Text(
                                "This is to notify you that your bid dated September 15, 2023, for the execution of the project titled \"Widening and Strengthening of Highway SHSR 2024\" for the contract price of INR 121,20,00,000 (One Hundred Twenty-One Crores and Twenty Lakhs) is hereby accepted by our department.",
                                fontSize = 11.sp, color = Color.DarkGray, lineHeight = 18.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Body Paragraph 2
                            Text(
                                "You are requested to furnish the Performance Security within 21 days from the receipt of this letter in accordance with the Conditions of Contract. Failure to comply may lead to cancellation of award.",
                                fontSize = 11.sp, color = Color.DarkGray, lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Page 1 of 3 - Securely stored in Tender Vault", fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontSize = 10.sp, color = Color.Gray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }

            // --- BOTTOM FLOATING ACTION BAR ---
            Card(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().padding(16.dp).padding(bottom = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {

                    BottomActionIcon(icon = Icons.Outlined.FileDownload, label = "Download", tint = Color(0xFF2563EB), onClick = { /* Download Logic */ })

                    HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color(0xFFE2E8F0))

                    BottomActionIcon(icon = Icons.Outlined.Print, label = "Print", tint = Color(0xFF2563EB), onClick = { /* Print Logic */ })

                    HorizontalDivider(modifier = Modifier.height(40.dp).width(1.dp), color = Color(0xFFE2E8F0))

                    BottomActionIcon(icon = Icons.Outlined.GroupAdd, label = "Share Team", tint = Color(0xFF2563EB), onClick = { /* Share Logic */ })
                }
            }
        }
    }
}

@Composable
fun BottomActionIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }.padding(horizontal = 16.dp)) {
        Box(modifier = Modifier.size(40.dp).background(Color(0xFFF4F6FF), CircleShape), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}