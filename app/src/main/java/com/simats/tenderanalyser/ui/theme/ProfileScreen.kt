package com.simats.tenderanalyser

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userId: String = "1",
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current // Added for safe Toasts

    // --- STATES FOR DATABASE SYNC ---
    var fullName by remember { mutableStateOf("Loading...") }
    var companyName by remember { mutableStateOf("Loading...") }
    var profileImageUrl by remember { mutableStateOf<String?>(null) }

    // FETCH DATA FROM DATABASE WHEN SCREEN OPENS
    LaunchedEffect(userId) {
        try {
            val response = ApiClient.apiService.getUserProfile(userId)
            if (response.success) {
                fullName = response.fullName?.takeIf { it.isNotBlank() } ?: "No Name Provided"
                companyName = response.companyName?.takeIf { it.isNotBlank() } ?: "No Company Provided"
                profileImageUrl = response.profileImageUrl
            } else {
                fullName = "Profile Error"
                companyName = "Could not load data"
            }
        } catch (e: Exception) {
            fullName = "Network Error"
            companyName = "Cannot connect to database"
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .swipeToNavigate(
                onSwipeRight = {
                    navController.navigate("my_bids") {
                        popUpTo("dashboard") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            ),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle settings icon click */ }) {
                        Icon(Icons.Outlined.Settings, contentDescription = "Settings", tint = Color(0xFF1976D2))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            ProfileBottomNavigationBar(navController = navController)
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .background(Color.White)
        ) {
            // --- Profile Header ---
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // DYNAMIC PROFILE PICTURE LOGIC (UPDATED FOR SUPABASE / RENDER)
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (!profileImageUrl.isNullOrEmpty()) {
                        val imageModel = if (profileImageUrl!!.startsWith("http://") || profileImageUrl!!.startsWith("https://")) {
                            profileImageUrl
                        } else {
                            "https://tender-analyser-api.onrender.com$profileImageUrl"
                        }

                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, contentDescription = "Profile Pic", modifier = Modifier.size(50.dp), tint = Color.White)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = Color(0xFF1976D2),
                            modifier = Modifier.size(24.dp).background(Color.White, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // DYNAMIC FULL NAME
                    Text(fullName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1C2833))
                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = { onNavigate("edit_profile") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp), tint = Color.Gray)
                    }
                }

                // DYNAMIC COMPANY NAME
                Text(companyName, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE3F2FD),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = "Verified", tint = Color(0xFF1976D2), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("VERIFIED VENDOR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1976D2))
                    }
                }
            }

            Divider(color = Color(0xFFEEEEEE), thickness = 8.dp)

            // --- Company Details ---
            SectionHeader("COMPANY DETAILS")
            SettingsOptionItem(icon = Icons.Outlined.Assignment, title = "Manage GST & PAN", onClick = { onNavigate("manage_gst") })

            SettingsOptionItem(
                icon = Icons.Outlined.Description,
                title = "Business Certificates",
                onClick = { onNavigate("required_documents") }
            )

            // --- Security ---
            SectionHeader("SECURITY")
            SettingsOptionItem(icon = Icons.Outlined.Lock, title = "Change Password", onClick = { onNavigate("bidder_change_password") })

            // --- Support & Legal ---
            SectionHeader("SUPPORT & LEGAL")
            SettingsOptionItem(icon = Icons.Outlined.Info, title = "About Us", onClick = { onNavigate("about_us") })
            SettingsOptionItem(icon = Icons.Outlined.Article, title = "Terms and Conditions", onClick = { onNavigate("terms") })
            SettingsOptionItem(icon = Icons.Outlined.HelpOutline, title = "Contact Us", onClick = { onNavigate("contact_us") })

            Spacer(modifier = Modifier.height(32.dp))

            // --- Logout & Delete ---
            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(50.dp),
                border = BorderStroke(1.dp, Color(0xFFD32F2F)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = "Logout", tint = Color(0xFFD32F2F))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Version 2.4.1 (Build 890)",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsOptionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF4F6F9), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = Color(0xFF5D6D7E), modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            color = Color(0xFF1C2833),
            modifier = Modifier.weight(1f)
        )

        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Arrow", tint = Color.LightGray)
    }
    Divider(color = Color(0xFFF4F6F9), thickness = 1.dp, modifier = Modifier.padding(horizontal = 24.dp))
}

@Composable
fun ProfileBottomNavigationBar(navController: NavController) {
    val selectedItem = 2 // PROFILE
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
                    if (index == 0) {
                        navController.navigate("dashboard") {
                            popUpTo("dashboard") { inclusive = false }
                            launchSingleTop = true
                        }
                    } else if (index == 1) {
                        navController.navigate("my_bids") {
                            popUpTo("dashboard") { inclusive = false }
                            launchSingleTop = true
                        }
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

fun Modifier.swipeToNavigate(
    onSwipeLeft: () -> Unit = {},
    onSwipeRight: () -> Unit = {}
): Modifier = this.pointerInput(Unit) {
    var offsetX = 0f
    detectHorizontalDragGestures(
        onDragEnd = {
            if (offsetX < -150) {
                onSwipeLeft()
            } else if (offsetX > 150) {
                onSwipeRight()
            }
            offsetX = 0f
        }
    ) { _, dragAmount ->
        offsetX += dragAmount
    }
}