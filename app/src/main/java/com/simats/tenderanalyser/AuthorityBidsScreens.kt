package com.simats.tenderanalyser.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.BidListItem
import com.simats.tenderanalyser.TenderListItem

// --- Private Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val GreenLive = Color(0xFF10B981)
private val RedFlag = Color(0xFFEF4444)
private val OutlineColor = Color(0xFF333333)

// ==========================================
// SCREEN 1: MANAGE TENDERS (LIVE DATA)
// ==========================================
@Composable
fun AuthorityManageTendersScreen(
    onNavTendersClick: () -> Unit,
    onNavProfileClick: () -> Unit,
    onManageSubmissionsClick: (String) -> Unit,
    onViewAwardClick: (String) -> Unit
) {
    val context = LocalContext.current
    var tenders by remember { mutableStateOf<List<TenderListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getAllTenders()
            if (response.success && response.tenders != null) {
                tenders = response.tenders
            } else {
                Toast.makeText(context, response.message ?: "No tenders found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = DarkBg,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1A1A1A), contentColor = TextGray, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = false, onClick = onNavTendersClick,
                    icon = { Icon(Icons.Outlined.Description, contentDescription = "Tenders") },
                    label = { Text("TENDERS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
                )
                NavigationBarItem(
                    selected = true, onClick = { },
                    icon = { Icon(Icons.Default.BusinessCenter, contentDescription = "Bids") },
                    label = { Text("BIDS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold, selectedTextColor = Gold, indicatorColor = Color.Transparent)
                )
                NavigationBarItem(
                    selected = false, onClick = onNavProfileClick,
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("PROFILE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("AUTHORITY ADMIN", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Manage Tenders", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Icon(Icons.Default.Search, contentDescription = "Search", tint = TextGray)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("PUBLISHED PROJECTS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Gold)
                }
            } else if (tenders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No published tenders yet.", color = TextGray, fontSize = 14.sp)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                    items(tenders) { tender ->
                        val isActive = tender.status.equals("Open", ignoreCase = true)

                        ManageTenderCard(
                            nit = "NIT: ${tender.nitNumber}",
                            badge = tender.status.uppercase(),
                            badgeColor = if (isActive) GreenLive else TextGray,
                            title = tender.projectTitle,
                            bids = "Tap to view",
                            dateLabel = "DEADLINE",
                            date = tender.deadline,
                            buttonText = "MANAGE SUBMISSIONS",
                            buttonFilled = isActive,
                            onClick = { onManageSubmissionsClick(tender.id) }
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// SCREEN 2: BIDS RECEIVED (Ranked Bidders)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityBidsReceivedScreen(
    tenderId: String,
    onBackClick: () -> Unit,
    onReviewBidClick: (String) -> Unit,
    onViewAnalyticsClick: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var bids by remember { mutableStateOf<List<BidListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getBidsForTender(tenderId)
            if (response.success && response.bids != null) {
                bids = response.bids
            } else {
                Toast.makeText(context, response.message ?: "No bids found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bids Received", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text("TENDER ID: $tenderId", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("RANKED BIDDERS (${bids.size} TOTAL)", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = Gold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("SORT BY ML SCORE", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Gold) }
            } else if (bids.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No bids found.", color = TextGray) }
            } else {
                val awardedBid = bids.find { it.status.equals("Awarded", ignoreCase = true) }
                val otherBids = bids.filter { !it.status.equals("Awarded", ignoreCase = true) }.sortedByDescending { it.mlScore ?: 0.0 }
                val isAwarded = awardedBid != null

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    if (awardedBid != null) {
                        item {
                            Text(
                                text = "🏆 WINNING BID (AI SELECTED)",
                                color = GreenLive,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            AuthorityRankedBidCard(
                                rank = awardedBid.rank,
                                rankLabel = "WINNER",
                                rankColor = GreenLive,
                                isLowest = true,
                                company = awardedBid.companyName,
                                id = "BID-${awardedBid.bidId}",
                                value = awardedBid.bidAmount,
                                statusText = "Awarded",
                                statusColor = GreenLive,
                                statusIcon = Icons.Default.CheckCircle,
                                onReviewClick = { onReviewBidClick(awardedBid.bidId) }
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                            HorizontalDivider(color = OutlineColor, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(20.dp))

                            Text(
                                text = "OTHER SUBMISSIONS",
                                color = TextGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }

                    items(otherBids) { bid ->
                        val isLowest = bid.rank == "L1"
                        val (statusText, statusColor, statusIcon) = when {
                            bid.status == "Pending Review" -> Triple("Pending Review", Gold, Icons.Default.Schedule)
                            bid.status == "Rejected" -> Triple("Flagged/Rejected", RedFlag, Icons.Default.Cancel)
                            bid.status == "Not Awarded" -> Triple("Not Awarded", RedFlag, Icons.Default.Cancel)
                            bid.isVerified -> Triple("Docs Verified", GreenLive, Icons.Default.CheckCircle)
                            else -> Triple("Pending Verification", Gold, Icons.Default.Schedule)
                        }

                        AuthorityRankedBidCard(
                            rank = bid.rank,
                            rankLabel = if (isLowest) "${bid.rank} LOWEST" else bid.rank,
                            rankColor = if (isLowest) Gold else TextGray,
                            isLowest = isLowest,
                            company = bid.companyName,
                            id = "BID-${bid.bidId}",
                            value = bid.bidAmount,
                            statusText = statusText,
                            statusColor = statusColor,
                            statusIcon = statusIcon,
                            onReviewClick = { onReviewBidClick(bid.bidId) }
                        )
                    }
                }

                // 🚨 ANALYTICS BUTTON VISIBLE ONLY WHEN TENDER IS AWARDED
                if (isAwarded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onViewAnalyticsClick(tenderId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Leaderboard, contentDescription = null, tint = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("VIEW FINAL BIDDING ANALYTICS", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// --- Reusable Components ---

@Composable
fun ManageTenderCard(nit: String, badge: String, badgeColor: Color, title: String, bids: String, dateLabel: String, date: String, buttonText: String, buttonFilled: Boolean, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, OutlineColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(nit, color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Surface(color = badgeColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) { Text(badge, color = badgeColor, fontSize = 9.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(48.dp), colors = ButtonDefaults.buttonColors(containerColor = if (buttonFilled) Gold else Color.Transparent), border = if (!buttonFilled) BorderStroke(1.dp, Gold) else null) {
                Text(buttonText, color = if (buttonFilled) Color.Black else Gold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AuthorityRankedBidCard(
    rank: String, rankLabel: String, rankColor: Color, isLowest: Boolean,
    company: String, id: String, value: String, statusText: String,
    statusColor: Color, statusIcon: ImageVector, onReviewClick: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CardBg), border = BorderStroke(1.dp, if (isLowest) Gold else OutlineColor)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(42.dp).background(if (isLowest) Gold.copy(alpha = 0.1f) else DarkBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Domain, contentDescription = null, tint = if (isLowest) Gold else TextGray) }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(company, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("ID: $id", color = TextGray, fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Surface(color = rankColor.copy(alpha = 0.15f), shape = RoundedCornerShape(6.dp)) {
                        Text(rankLabel, color = rankColor, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(value, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(statusText, color = statusColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Button(onClick = onReviewClick, modifier = Modifier.height(32.dp), shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(containerColor = if (isLowest) Gold else DarkBg), border = if (!isLowest) BorderStroke(1.dp, Gold) else null) {
                    Text("REVIEW SUBMISSION", color = if (isLowest) Color.Black else Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}