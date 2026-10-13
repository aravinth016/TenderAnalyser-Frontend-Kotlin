package com.simats.tenderanalyser.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import kotlinx.coroutines.launch

// --- DATA MODEL FOR LIVE BIDS ---
data class LiveBidModel(
    val tenderId: String,
    val tenderTitle: String,
    val tenderRef: String,
    val quotedPrice: String,
    val status: String,
    val rank: String? = null,
    val gapToL1: String? = null,
    val l1Price: String? = null
)

// Colors
private val PrimaryBlue = Color(0xFF0A1931)
private val ButtonBlue = Color(0xFF2962FF)
private val BadgeGreen = Color(0xFF10B981)
private val BadgeGreenBg = Color(0xFFD1FAE5)
private val BadgeRed = Color(0xFFEF4444)
private val BadgeRedBg = Color(0xFFFEE2E2)
private val BadgeGray = Color(0xFF64748B)
private val BadgeGrayBg = Color(0xFFF1F5F9)
private val AnalyticsDark = Color(0xFF0F172A)

@Composable
fun MyBidsScreen(
    onNavigateBack: () -> Unit = {},
    onEditBidClick: (String) -> Unit = {},
    onDeleteBidClick: (String) -> Unit = {},
    onViewAnalyticsClick: (String, Boolean) -> Unit = { _, _ -> },
    onNavigateToHome: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    // Swipe Logic States
    var offsetX by remember { mutableStateOf(0f) }
    val swipeThreshold = 150f

    // LIVE DATA STATES
    var isLoading by remember { mutableStateOf(true) }
    var activeBids by remember { mutableStateOf<List<LiveBidModel>>(emptyList()) }
    var completedBids by remember { mutableStateOf<List<LiveBidModel>>(emptyList()) }

    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val sharedPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
            val realUserId = sharedPrefs.getString("USER_ID", "1") ?: "1"

            val response = ApiClient.apiService.getMyBids(userId = realUserId)

            if (response.success && response.bids != null) {
                val allBids = response.bids.map { dbRow ->
                    LiveBidModel(
                        tenderId = dbRow.tenderId,
                        tenderTitle = dbRow.tenderTitle,
                        tenderRef = dbRow.tenderRef,
                        quotedPrice = dbRow.quotedPrice,
                        status = dbRow.status,
                        rank = dbRow.rank,
                        gapToL1 = dbRow.gapToL1,
                        l1Price = dbRow.l1Price
                    )
                }
                activeBids = allBids.filter { it.status !in listOf("Awarded", "Not Awarded", "Approved", "Rejected") }
                completedBids = allBids.filter { it.status in listOf("Awarded", "Not Awarded", "Approved", "Rejected") }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        bottomBar = {
            BidderBottomNavBar(
                currentRoute = "my_bids",
                onNavigateToHome = onNavigateToHome,
                onNavigateToMyBids = { /* Already here */ },
                onNavigateToProfile = onNavigateToProfile
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC))
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > swipeThreshold) {
                                onNavigateToHome()
                            } else if (offsetX < -swipeThreshold) {
                                onNavigateToProfile()
                            }
                            offsetX = 0f
                        }
                    ) { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount
                    }
                }
        ) {
            // --- TOP HEADER ---
            Column(
                modifier = Modifier
                    .background(PrimaryBlue)
                    .statusBarsPadding()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Text("My Bids", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).background(Color.Transparent),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TabPill("ACTIVE", selectedTabIndex == 0, Modifier.weight(1f)) { selectedTabIndex = 0 }
                    TabPill("COMPLETED", selectedTabIndex == 1, Modifier.weight(1f)) { selectedTabIndex = 1 }
                }
            }

            // --- TAB CONTENT ---
            Box(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(PrimaryBlue))

                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.align(Alignment.TopCenter).padding(top = 32.dp))
                } else {
                    val listToDisplay = if (selectedTabIndex == 0) activeBids else completedBids

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (listToDisplay.isEmpty()) {
                            item { EmptyStateMessage(if (selectedTabIndex == 0) "No active bids found." else "No completed bids found.") }
                        } else {
                            items(listToDisplay) { bid ->
                                if (selectedTabIndex == 0) {
                                    ActiveBidCard(bid, onEditBidClick, onDeleteBidClick)
                                } else {
                                    CompletedBidCard(bid, onViewAnalyticsClick)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// BOTTOM NAVIGATION BAR
// ==========================================
@Composable
fun BidderBottomNavBar(
    currentRoute: String,
    onNavigateToHome: () -> Unit,
    onNavigateToMyBids: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(70.dp)
    ) {
        val navItems = listOf(
            Triple("HOME", Icons.Default.Home, "dashboard"),
            Triple("MY BIDS", Icons.Default.BusinessCenter, "my_bids"),
            Triple("PROFILE", Icons.Default.Person, "profile")
        )

        navItems.forEach { (label, icon, route) ->
            val isSelected = currentRoute == route
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    when (route) {
                        "dashboard" -> onNavigateToHome()
                        "my_bids" -> onNavigateToMyBids()
                        "profile" -> onNavigateToProfile()
                    }
                },
                icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
                label = { Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = ButtonBlue,
                    selectedTextColor = ButtonBlue,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
            )
        }
    }
}

// ==========================================
// UI HELPERS
// ==========================================
@Composable
fun TabPill(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(color = if (isSelected) Color.White else Color.Transparent, shape = RoundedCornerShape(24.dp), modifier = modifier.height(40.dp).clickable { onClick() }) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = if (isSelected) PrimaryBlue else Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

@Composable
fun ActiveBidCard(
    bid: LiveBidModel,
    onEditBidClick: (String) -> Unit,
    onDeleteBidClick: (String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sharedPrefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val currentUserId = sharedPrefs.getString("USER_ID", "1") ?: "1"

    val isEditable = bid.status == "Pending Review"
    val isEvaluating = bid.status == "Evaluating"
    val isWinnerLead = bid.status == "Winner Lead" || bid.rank == "L1"

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(modifier = Modifier.weight(1f)) {
                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(bid.tenderTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text("Government / Official Sector", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                val badgeText = if (isEditable) "SUBMITTED" else if (isWinnerLead) "L1 - LOWEST" else if (isEvaluating) "EVALUATING" else "OUTBID"
                val badgeBg = if (isEditable || isEvaluating) BadgeGrayBg else if (isWinnerLead) BadgeGreenBg else BadgeRedBg
                val badgeFg = if (isEditable || isEvaluating) BadgeGray else if (isWinnerLead) BadgeGreen else BadgeRed

                Surface(color = badgeBg, shape = RoundedCornerShape(4.dp)) {
                    Text(badgeText, color = badgeFg, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("NIT NUMBER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(bid.tenderRef, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("YOUR QUOTA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("₹ ${bid.quotedPrice}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = ButtonBlue)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isEditable) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onEditBidClick(bid.tenderId) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ButtonBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("EDIT BID", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    val requestMap = mapOf(
                                        "tender_id" to bid.tenderId,
                                        "user_id" to currentUserId
                                    )
                                    val response = ApiClient.apiService.deleteBid(requestMap)
                                    if (response.success) {
                                        Toast.makeText(context, "Bid withdrawn successfully", Toast.LENGTH_SHORT).show()
                                        onDeleteBidClick(bid.tenderId)
                                    } else {
                                        Toast.makeText(context, response.message ?: "Failed to withdraw bid", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BadgeRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("WITHDRAW BID", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                    }
                }
            } else {
                BidProgressBar(currentStatus = bid.status)
                if (!isWinnerLead && !isEvaluating) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(color = Color(0xFFF8FAFC), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ErrorOutline, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Financial bid opened. You are currently ${bid.rank ?: "L2"}.", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CompletedBidCard(bid: LiveBidModel, onViewAnalyticsClick: (String, Boolean) -> Unit) {
    val isAwarded = bid.status == "Awarded" || bid.status == "Approved"

    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Row(modifier = Modifier.weight(1f)) {
                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(40.dp)) {
                        Icon(Icons.Default.Business, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.padding(8.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(bid.tenderTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A), maxLines = 2, overflow = TextOverflow.Ellipsis)
                        Text("Government / Official Sector", fontSize = 10.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = if (isAwarded) BadgeGreenBg else BadgeRedBg, shape = RoundedCornerShape(4.dp)) {
                    Text(if (isAwarded) "AWARDED" else "NOT AWARDED", color = if (isAwarded) BadgeGreen else BadgeRed, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("YOUR QUOTA", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("₹ ${bid.quotedPrice}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("WINNING BID (L1)", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("₹ ${bid.l1Price ?: bid.quotedPrice}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = BadgeGreen)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onViewAnalyticsClick(bid.tenderId, isAwarded) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isAwarded) ButtonBlue else AnalyticsDark
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isAwarded) "VIEW AWARD LETTER" else "VIEW REJECTION NOTICE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun BidProgressBar(currentStatus: String) {
    val isFinancial = currentStatus != "Pending Review"
    val isAwarded = currentStatus == "Awarded" || currentStatus == "Approved"

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("TECHNICAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ButtonBlue)
            Text("FINANCIAL", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isFinancial) ButtonBlue else Color.LightGray)
            Text("AWARDED", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isAwarded) ButtonBlue else Color.LightGray)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(1f).height(4.dp).background(ButtonBlue, RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp)))
            Spacer(modifier = Modifier.width(2.dp))
            Box(modifier = Modifier.weight(1f).height(4.dp).background(if (isFinancial) ButtonBlue else Color(0xFFE2E8F0)))
            Spacer(modifier = Modifier.width(2.dp))
            Box(modifier = Modifier.weight(1f).height(4.dp).background(if (isAwarded) ButtonBlue else Color(0xFFE2E8F0), RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp)))
        }
    }
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(top = 64.dp), contentAlignment = Alignment.Center) {
        Surface(color = Color.White, shape = RoundedCornerShape(12.dp), shadowElevation = 2.dp) {
            Text(message, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(24.dp))
        }
    }
}