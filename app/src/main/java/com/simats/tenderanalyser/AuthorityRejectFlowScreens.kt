package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

@Composable
fun AuthorityRejectSuccessScreen(
    reason: String,
    onBackToSubmissions: () -> Unit,
    onReturnHome: () -> Unit
) {
    // 🚨 Controls the transition from "Processing" to "Success"
    var isProcessing by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000) // Simulate a 2-second network call
        isProcessing = false
    }

    Scaffold(containerColor = DarkBg) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {

            if (isProcessing) {
                // ==========================================
                // 1. PROCESSING REJECTION UI
                // ==========================================
                Card(
                    modifier = Modifier.fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Gold, modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Processing Rejection...", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            buildAnnotatedString {
                                append("We are recording the rejection reasons and notifying ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Gold)) { append("Global Infra Ltd.") }
                                append(" Please wait.")
                            },
                            color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { }, enabled = false,
                            modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(disabledContainerColor = OutlineColor)
                        ) {
                            Text("Processing...", color = TextGray, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // ==========================================
                // 2. REJECTION CONFIRMED UI
                // ==========================================
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.size(80.dp).border(2.dp, Gold, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Gold, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Text("BID REJECTED", color = Gold, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("The bidder has been officially notified\nof this decision.", color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(40.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, OutlineColor)
                    ) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("BIDDER", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Global Infra Ltd", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("REASON", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(reason.ifEmpty { "Technical Non-compliance" }, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, modifier = Modifier.weight(1f).padding(start = 16.dp))
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider(color = OutlineColor, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("REF ID", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("BID-REJ-2024-001", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = onBackToSubmissions, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Text("Back to Submissions", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onReturnHome, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), border = BorderStroke(1.dp, OutlineColor)
                    ) {
                        Text("Return to Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}