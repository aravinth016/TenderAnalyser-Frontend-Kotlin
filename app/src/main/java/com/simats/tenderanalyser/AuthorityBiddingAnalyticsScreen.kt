package com.simats.tenderanalyser.screens

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.BidListItem

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val GreenLive = Color(0xFF10B981)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityBiddingAnalyticsScreen(
    tenderId: String,
    tenderTitle: String = "Tender Analytics",
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var bidsList by remember { mutableStateOf<List<BidListItem>>(emptyList()) }

    // Fetch live bids for this tender from the backend endpoint
    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getBidsForTender(tenderId)
            if (response.success && response.bids != null) {
                bidsList = response.bids
            } else {
                Toast.makeText(context, response.message ?: "Failed to load bids analytics", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error loading analytics", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("BIDDING ANALYTICS", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text(tenderTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    }
                },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Live Stats Summary Card
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("TOTAL SUBMISSIONS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text("${bidsList.size}", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("EVALUATION STATUS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(if (bidsList.any { bid -> bid.status == "Awarded" }) "Awarded" else "Active / Pending", color = Gold, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Live Financial Rankings Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Leaderboard, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("LIVE FINANCIAL RANKINGS & SCORES", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(24.dp))

                        if (bidsList.isEmpty()) {
                            Text("No bids submitted for this tender yet.", color = TextGray, fontSize = 12.sp)
                        } else {
                            // Display live sorted list of bidders
                            bidsList.forEachIndexed { index, bid ->
                                val isLeader = index == 0
                                val rankLabel = "L${index + 1}"
                                RankBarItem(
                                    rank = "$rankLabel ${if (isLeader) "(WINNER / L1)" else ""}",
                                    name = bid.companyName ?: "Unknown Company",
                                    value = bid.bidAmount ?: "N/A",
                                    score = "Status: ${bid.status ?: "Pending"}",
                                    fillFraction = if (isLeader) 1.0f else (0.85f - (index * 0.1f)).coerceAtLeast(0.2f),
                                    color = if (isLeader) Gold else Color(0xFF444444)
                                )
                                if (index < bidsList.size - 1) {
                                    Spacer(modifier = Modifier.height(20.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // AI Evaluation Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("AI 360° ENGINE SUMMARY", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "All submissions were evaluated against mandatory RFP compliance clauses using TF-IDF text similarity and linear regression scoring models.",
                            color = TextGray,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RankBarItem(rank: String, name: String, value: String, score: String, fillFraction: Float, color: Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(rank, color = if (color == Gold) Gold else TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Text(score, color = GreenLive, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(name, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth().height(6.dp).background(Color(0xFF2A2A35), RoundedCornerShape(3.dp))) {
            Box(modifier = Modifier.fillMaxWidth(fillFraction.coerceIn(0.1f, 1f)).height(6.dp).background(color, RoundedCornerShape(3.dp)))
        }
    }
}