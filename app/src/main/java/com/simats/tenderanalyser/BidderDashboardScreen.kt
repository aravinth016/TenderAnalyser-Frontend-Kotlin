package com.simats.tenderanalyser.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.TenderListItem

@Composable
fun BidderDashboardScreen(
    onTenderClick: (String) -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAIAgentClick: () -> Unit = {},
    onBidsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var activeTenders by remember { mutableStateOf<List<TenderListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // FETCH LIVE TENDERS FROM DATABASE
    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getAllTenders()
            if (response.success && response.tenders != null) {
                // Filter to show "Open" tenders to bidders
                activeTenders = response.tenders.filter { it.status.equals("Open", ignoreCase = true) }
            } else {
                Toast.makeText(context, response.message ?: "No tenders available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { DashboardTopBar() },
        floatingActionButton = {
            // 🤖 Floating Action Button for quick AI Assistant access
            FloatingActionButton(
                onClick = onAIAgentClick,
                containerColor = Color(0xFF2563EB),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Tender AI Assistant",
                    tint = Color(0xFFFFC107)
                )
            }
        },
        bottomBar = {
            BidderBottomNavigation(
                onBidsClick = onBidsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Search bar with keyboard action & clickable AI tip banner
            item { SearchAndAITools(onAIAgentClick = onAIAgentClick) }

            // Filters
            item { FilterChipsRow() }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Section Header
            item { SectionHeader("Live Tenders", "View All") }

            // DYNAMIC LIVE TENDER LIST
            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF2563EB))
                    }
                }
            } else if (activeTenders.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No active tenders available right now.", color = Color(0xFF94A3B8), fontSize = 14.sp)
                    }
                }
            } else {
                items(activeTenders) { tender ->
                    TenderCard(tender = tender, onClick = { onTenderClick(tender.id) })
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun DashboardTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
            .statusBarsPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF2563EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Subject, contentDescription = "Logo", tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = "Tenders", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
        }
        IconButton(onClick = { }) {
            Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color(0xFF64748B))
        }
    }
}

@Composable
fun SearchAndAITools(onAIAgentClick: () -> Unit = {}) {
    var searchQuery by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            placeholder = {
                Text("Search by NIT, Department, or State", color = Color(0xFF94A3B8), fontSize = 13.sp)
            },
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color(0xFF94A3B8), modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                IconButton(onClick = { onAIAgentClick() }) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI Agent", tint = Color(0xFFEAB308), modifier = Modifier.size(24.dp))
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    if (searchQuery.isNotBlank()) {
                        onAIAgentClick()
                    }
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = true
        )

        // 🤖 Clickable Tip Banner that triggers the AI Agent
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFEF9C3))
                .clickable { onAIAgentClick() }
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = Color(0xFFEAB308), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tip: Click here to ask Tender AI about any tender or document.",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF422006),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FilterChipsRow() {
    val filters = listOf(
        FilterChipData("All", Icons.Default.AllInclusive),
        FilterChipData("Public Works", Icons.Default.Build),
        FilterChipData("Energy", Icons.Default.Bolt)
    )
    var selectedFilter by remember { mutableStateOf("All") }

    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        items(filters) { filter: FilterChipData ->
            val isSelected = filter.title == selectedFilter
            Surface(
                modifier = Modifier.clickable { selectedFilter = filter.title },
                shape = CircleShape,
                color = if (isSelected) Color(0xFF2563EB) else Color.White,
                border = if (isSelected) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = filter.icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF64748B), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = filter.title, color = if (isSelected) Color.White else Color(0xFF475569), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
        Text(text = actionText, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
    }
}

@Composable
fun TenderCard(tender: TenderListItem, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFF8FAFC)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Business, contentDescription = "Dept", tint = Color(0xFF334155), modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(tender.projectTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A), lineHeight = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Publishing Authority", fontSize = 11.sp, color = Color(0xFF64748B))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("NIT NUMBER", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(tender.nitNumber, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("ESTIMATED COST", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF94A3B8))
                    Text(tender.estimatedValue, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = "Location", tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View details", fontSize = 11.sp, color = Color(0xFF64748B))
                }

                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFF7ED), border = BorderStroke(1.dp, Color(0xFFFFEDD5))) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Timer, contentDescription = "Timer", tint = Color(0xFFF97316), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Due: ${tender.deadline}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                    }
                }
            }
        }
    }
}

@Composable
fun BidderBottomNavigation(onBidsClick: () -> Unit = {}, onProfileClick: () -> Unit = {}) {
    val selectedItem = 0
    val items = listOf("HOME", "MY BIDS", "PROFILE")
    val selectedIcons = listOf(Icons.Filled.Home, Icons.Filled.Work, Icons.Filled.Person)
    val unselectedIcons = listOf(Icons.Outlined.Home, Icons.Outlined.WorkOutline, Icons.Outlined.PersonOutline)

    NavigationBar(containerColor = Color.White, contentColor = Color(0xFF2563EB), tonalElevation = 8.dp) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(imageVector = if (selectedItem == index) selectedIcons[index] else unselectedIcons[index], contentDescription = item) },
                label = { Text(item, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                selected = selectedItem == index,
                onClick = {
                    if (index == 1) {
                        onBidsClick()
                    } else if (index == 2) {
                        onProfileClick()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFF2563EB),
                    selectedTextColor = Color(0xFF2563EB),
                    unselectedIconColor = Color(0xFF94A3B8),
                    unselectedTextColor = Color(0xFF94A3B8),
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

data class FilterChipData(val title: String, val icon: ImageVector)