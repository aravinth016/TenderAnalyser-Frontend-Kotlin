package com.simats.tenderanalyser.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityAboutUsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("About Authority Portal", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(16.dp))
            Text("EVALUATION & CONTROL", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Revolutionizing Authority Workflow & Bid Oversight", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Empowering tendering officers and evaluation authorities with automated document analysis, cloud-backed bid verification, and real-time bidder compliance tracking.", color = TextGray, fontSize = 13.sp, lineHeight = 22.sp)

            Spacer(modifier = Modifier.height(32.dp))

            // --- Feature Cards ---
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AboutFeatureItem(
                    Modifier.weight(1f),
                    Icons.Default.Visibility,
                    "TRANSPARENCY",
                    "Real-time bid status\n& verified audit trail"
                )
                AboutFeatureItem(
                    Modifier.weight(1f),
                    Icons.Default.Speed,
                    "EFFICIENCY",
                    "Fast technical & financial\nevaluation cycles"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, OutlineColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.TrackChanges, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("SECURE CLOUD STORAGE", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Encrypted document access powered by Supabase & PostgreSQL", color = TextGray, fontSize = 10.sp, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Platform Live Stats Box ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = DarkBg),
                border = BorderStroke(1.dp, Gold)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("Active Platform", color = Gold, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("VERIFIED BIDDER EVALUATIONS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(32.dp).background(Color.Gray, CircleShape).border(2.dp, DarkBg, CircleShape))
                        Box(modifier = Modifier.size(32.dp).offset(x = (-10).dp).background(Color.LightGray, CircleShape).border(2.dp, DarkBg, CircleShape))
                        Box(modifier = Modifier.size(32.dp).offset(x = (-20).dp).background(Color(0xFF2A2A2A), CircleShape).border(2.dp, DarkBg, CircleShape), contentAlignment = Alignment.Center) {
                            Text("v1.0", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("2026 Production Release", color = TextGray, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // --- Contact & Official Details (Interactive) ---
            Text("OFFICIAL CONTACT", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(20.dp))

            ContactRow(
                icon = Icons.Outlined.LocationOn,
                title = "Headquarters",
                detail = "Central Procurement Office, Tech Corridor,\nBengaluru, India 560001"
            )
            Spacer(modifier = Modifier.height(20.dp))
            ContactRow(
                icon = Icons.Outlined.Language,
                title = "Official Portal",
                detail = "www.tenderanalyser.ai",
                isLink = true,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tenderanalyser.ai"))
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Cannot open browser", Toast.LENGTH_SHORT).show()
                    }
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            ContactRow(
                icon = Icons.Outlined.Email,
                title = "Authority Email Support",
                detail = "authority-support@tenderanalyser.ai",
                isLink = true,
                onClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:authority-support@tenderanalyser.ai")
                        putExtra(Intent.EXTRA_SUBJECT, "Authority Portal Query")
                    }
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider(color = OutlineColor)
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "© 2026 TENDER ANALYSER AUTHORITY PORTAL.\nALL RIGHTS RESERVED • VERSION 1.0.0",
                color = TextGray,
                fontSize = 10.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun AboutFeatureItem(modifier: Modifier, icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, OutlineColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = TextGray, fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}

@Composable
private fun ContactRow(
    icon: ImageVector,
    title: String,
    detail: String,
    isLink: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    ) {
        Icon(icon, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(detail, color = if (isLink) Gold else TextGray, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}