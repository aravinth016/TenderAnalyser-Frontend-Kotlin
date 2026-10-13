package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.ComparisonBidItem
import com.simats.tenderanalyser.TenderComparisonResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonAnalyticsScreen(
    tenderId: String,
    userId: String,
    onBack: () -> Unit
) {
    var data by remember { mutableStateOf<TenderComparisonResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getTenderComparison(tenderId, userId)
            if (response.success) data = response
        } catch (e: Exception) { e.printStackTrace() }
        finally { isLoading = false }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comparison Analytics", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Share, null) } }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            data?.let { analytics ->
                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .background(Color(0xFFFBFBFE))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. Project Header Card
                    ProjectHeaderCard(analytics.projectTitle, analytics.nitNumber)

                    // 2. Financial Ranking
                    Column {
                        Text("FINANCIAL RANKING", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("${analytics.userBid}", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Text("Cr", fontSize = 16.sp, color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                            Spacer(Modifier.weight(1f))
                            Surface(color = Color(0xFFFEE2E2), shape = RoundedCornerShape(4.dp)) {
                                Text("Rank: ${analytics.userRank}", color = Color(0xFFEF4444), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 3. Bid Values Comparison
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("BID VALUES (INR CR)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("MARKET AVG: ${analytics.marketAvg}", fontSize = 10.sp, color = Color.Gray)
                            }

                            analytics.bids.forEach { bid ->
                                BidComparisonRow(bid, maxVal = analytics.bids.last().amount)
                            }
                        }
                    }

                    // 4. AI Insight Card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("✨ AI COMPETITIVE INSIGHT", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(Modifier.height(12.dp))
                            Text(
                                "Your bid was ${analytics.deviation}% above L1. The deviation is primarily attributed to higher overhead projections in the material logistics phase. L1 utilized a local sourcing strategy.",
                                color = Color(0xFF94A3B8), fontSize = 13.sp, lineHeight = 18.sp
                            )
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("MARGIN GAP: ${analytics.userBid - analytics.bids[0].amount} CR", color = Color.Gray, fontSize = 10.sp)
                                Text("VIEW STRATEGY RECOM. →", color = Color(0xFF3B82F6), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 5. Footer Stats
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        StatCard("Competitors", "${analytics.competitors} Total", Modifier.weight(1f))
                        StatCard("Bidding Phase", "Completed", Modifier.weight(1f), isStatus = true)
                    }
                }
            }
        }
    }
}

@Composable
fun BidComparisonRow(bid: ComparisonBidItem, maxVal: Double) {
    val progress = (bid.amount / maxVal).toFloat()
    val barColor = if (bid.rank == "L1") Color(0xFF10B981) else if (bid.isYou) Color(0xFF2962FF) else Color(0xFFCBD5E1)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("${if(bid.isYou) "YOU " else ""}${bid.rank}: ${bid.companyName}", fontSize = 11.sp, fontWeight = if(bid.isYou) FontWeight.Bold else FontWeight.Normal)
            Text("${bid.amount}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFF1F5F9))) {
            Box(Modifier.fillMaxWidth(progress).fillMaxHeight().background(barColor))
        }
    }
}

@Composable
fun ProjectHeaderCard(title: String, id: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4ED8))))
            .padding(16.dp)
    ) {
        Column {
            Surface(color = Color.White.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                Text("PROJECT ID: $id", color = Color.White, fontSize = 10.sp, modifier = Modifier.padding(4.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier, isStatus: Boolean = false) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFF1F5F9))) {
        Column(Modifier.padding(16.dp)) {
            Text(label, fontSize = 10.sp, color = Color.Gray)
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = if(isStatus) Color.Red else Color.Black)
        }
    }
}