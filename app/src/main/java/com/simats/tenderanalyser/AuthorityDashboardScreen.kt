package com.simats.tenderanalyser.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // 🚨 ADDED
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.TenderListItem
import retrofit2.HttpException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityDashboardScreen(
    onCreateNewClick: () -> Unit = {},
    onManageProjectClick: (String) -> Unit = {},
    onTenderImageClick: (String) -> Unit = {}, // 🚨 ADDED: Action for clicking the image
    onNavBidsClick: () -> Unit = {},
    onNavProfileClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }

    var tenders by remember { mutableStateOf<List<TenderListItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val darkBg = Color(0xFF121212)
    val cardBg = Color(0xFF1E1E1E)
    val goldAccent = Color(0xFFFFC107)
    val textGray = Color(0xFFA0A0A0)
    val outlineColor = Color(0xFF333333)

    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getAllTenders()
            if (response.success && response.tenders != null) {
                tenders = response.tenders
            } else {
                Toast.makeText(context, "API Error: ${response.message}", Toast.LENGTH_LONG).show()
            }
        } catch (e: HttpException) {
            Toast.makeText(context, "Python Error ${e.code()}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "App Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        } finally {
            isLoading = false
        }
    }

    val filteredTenders = tenders.filter {
        it.projectTitle.contains(searchQuery, ignoreCase = true) ||
                it.nitNumber.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = darkBg,
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF1A1A1A), contentColor = textGray, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = true, onClick = { },
                    icon = { Icon(Icons.Outlined.Description, contentDescription = "Tenders") },
                    label = { Text("TENDERS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = goldAccent, selectedTextColor = goldAccent, indicatorColor = Color.Transparent, unselectedIconColor = textGray, unselectedTextColor = textGray)
                )
                NavigationBarItem(
                    selected = false, onClick = onNavBidsClick,
                    icon = { Icon(Icons.Outlined.BusinessCenter, contentDescription = "Bids") },
                    label = { Text("BIDS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = goldAccent, selectedTextColor = goldAccent, indicatorColor = Color.Transparent, unselectedIconColor = textGray, unselectedTextColor = textGray)
                )
                NavigationBarItem(
                    selected = false, onClick = onNavProfileClick,
                    icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
                    label = { Text("PROFILE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = goldAccent, selectedTextColor = goldAccent, indicatorColor = Color.Transparent, unselectedIconColor = textGray, unselectedTextColor = textGray)
                )
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Published Tenders", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onCreateNewClick,
                    colors = ButtonDefaults.buttonColors(containerColor = goldAccent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create", tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Create New", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = searchQuery, onValueChange = { searchQuery = it },
                placeholder = { Text("Search by NIT or Title", color = textGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = goldAccent) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(8.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = goldAccent, unfocusedBorderColor = outlineColor, focusedContainerColor = cardBg, unfocusedContainerColor = cardBg)
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = goldAccent)
                }
            } else if (filteredTenders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tenders found.", color = textGray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp), contentPadding = PaddingValues(bottom = 24.dp)) {
                    items(filteredTenders) { tender ->
                        AuthorityTenderCard(
                            tender = tender,
                            onManageClick = { onManageProjectClick(tender.id) },
                            onImageClick = { onTenderImageClick(tender.id) } // 🚨 Passes the click up!
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AuthorityTenderCard(tender: TenderListItem, onManageClick: () -> Unit, onImageClick: () -> Unit) {
    val cardBg = Color(0xFF1E1E1E)
    val goldAccent = Color(0xFFFFC107)
    val textGray = Color(0xFFA0A0A0)
    val outlineColor = Color(0xFF333322)

    val isAwarded = tender.status.equals("Awarded", ignoreCase = true)
    val isOpen = tender.status.equals("Open", ignoreCase = true)

    val badgeColor = when {
        isOpen -> Color(0xFF10B981)
        isAwarded -> Color(0xFF3B82F6)
        else -> goldAccent
    }

    val badgeBgColor = when {
        isOpen -> Color(0xFF003314)
        isAwarded -> Color(0xFF001F4D)
        else -> Color(0xFF332D00)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, outlineColor),
        colors = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column {
            // 🚨 IMAGE PLACEHOLDER IS NOW CLICKABLE!
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFF2A2A2A))
                    .clickable { onImageClick() }, // <-- This triggers the navigation
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Image, contentDescription = "Project Image", tint = Color.DarkGray, modifier = Modifier.size(48.dp))
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = tender.projectTitle, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1)
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = badgeBgColor, shape = RoundedCornerShape(4.dp)) {
                        Text(text = tender.status.uppercase(), color = badgeColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                InfoRow(icon = Icons.Outlined.Info, text = "NIT: ${tender.nitNumber}", tint = textGray)
                Spacer(modifier = Modifier.height(4.dp))
                InfoRow(icon = Icons.Outlined.Group, text = "Deadline: ${tender.deadline}", tint = textGray)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onManageClick,
                    modifier = Modifier.fillMaxWidth().height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = goldAccent)
                ) {
                    Text("Manage Project", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, text: String, tint: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = tint, fontSize = 12.sp)
    }
}