package com.simats.tenderanalyser.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Domain
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.PublishedWithChanges
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val RedLogout = Color(0xFFEF4444)
private val OutlineColor = Color(0xFF333333)

@Composable
fun AuthorityProfileScreen(
    onNavTendersClick: () -> Unit,
    onNavBidsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigateToSetting: (String) -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)

    var adminName by remember { mutableStateOf(sharedPref.getString("ADMIN_NAME", "Administrator") ?: "Administrator") }
    var designation by remember { mutableStateOf(sharedPref.getString("ADMIN_DESIGNATION", "N/A") ?: "N/A") }
    var deptName by remember { mutableStateOf(sharedPref.getString("ADMIN_DEPT", "Department Unknown") ?: "Department Unknown") }
    var status by remember { mutableStateOf(sharedPref.getString("ADMIN_STATUS", "under_review") ?: "under_review") }
    var profileImageUrl by remember { mutableStateOf(sharedPref.getString("ADMIN_PROFILE_PIC", null)) }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                adminName = sharedPref.getString("ADMIN_NAME", "Administrator") ?: "Administrator"
                designation = sharedPref.getString("ADMIN_DESIGNATION", "N/A") ?: "N/A"
                deptName = sharedPref.getString("ADMIN_DEPT", "Department Unknown") ?: "Department Unknown"
                status = sharedPref.getString("ADMIN_STATUS", "under_review") ?: "under_review"
                profileImageUrl = sharedPref.getString("ADMIN_PROFILE_PIC", null)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                    selected = false, onClick = onNavBidsClick,
                    icon = { Icon(Icons.Outlined.BusinessCenter, contentDescription = "Bids") },
                    label = { Text("BIDS", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(unselectedIconColor = TextGray, unselectedTextColor = TextGray)
                )
                NavigationBarItem(
                    selected = true, onClick = { },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("PROFILE", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = Gold, selectedTextColor = Gold, indicatorColor = Color.Transparent)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Box(contentAlignment = Alignment.BottomEnd) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .border(2.dp, Gold, CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!profileImageUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = "http://192.168.1.8:5000$profileImageUrl",
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Gold, CircleShape)
                            .border(2.dp, DarkBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(adminName.uppercase(), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { onNavigateToSetting("authority_edit_profile") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit Profile", tint = Gold, modifier = Modifier.size(16.dp))
                    }
                }
                Text("$designation - $deptName", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(12.dp))

                val isVerified = status.equals("verified", ignoreCase = true)
                Surface(
                    color = if (isVerified) Color(0xFF10B981).copy(alpha = 0.1f) else Gold.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isVerified) Color(0xFF10B981).copy(alpha = 0.3f) else Gold.copy(alpha = 0.3f))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Icon(
                            Icons.Default.CheckCircle, contentDescription = null,
                            tint = if (isVerified) Color(0xFF10B981) else Gold, modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            if (isVerified) "VERIFIED ADMIN" else "UNDER REVIEW",
                            color = if (isVerified) Color(0xFF10B981) else Gold,
                            fontSize = 9.sp, fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }

            item { ProfileSectionHeader("PERSONAL") }
            item { ProfileRow("Account Details", Icons.Default.PersonOutline) { onNavigateToSetting("authority_edit_profile") } }
            item { ProfileRow("Change Password", Icons.Default.PublishedWithChanges) { onNavigateToSetting("password") } }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item { ProfileSectionHeader("DEPARTMENTAL") }
            item { ProfileRow("Department Info", Icons.Default.Domain) { onNavigateToSetting("dept") } }
            item { ProfileRow("Documents for Verification", Icons.Default.Badge) { onNavigateToSetting("docs") } }
            item { Spacer(modifier = Modifier.height(24.dp)) }

            item { ProfileSectionHeader("LEGAL & SUPPORT") }
            item { ProfileRow("About Us", Icons.Default.Info) { onNavigateToSetting("authority_about") } }
            item { ProfileRow("Terms of Use", Icons.Default.Gavel) { onNavigateToSetting("authority_terms") } }
            item { ProfileRow("Help Center", Icons.Default.HelpOutline) { onNavigateToSetting("authority_help") } }

            item {
                Spacer(modifier = Modifier.height(40.dp))
                OutlinedButton(
                    onClick = onLogoutClick, modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFF2D1515)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RedLogout)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Logout", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("VERSION 2.4.0 BUILD 88", color = Color(0xFF333333), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileSectionHeader(title: String) {
    Text(title, color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
}

@Composable
fun ProfileRow(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color(0xFF444444), modifier = Modifier.size(18.dp))
    }
}