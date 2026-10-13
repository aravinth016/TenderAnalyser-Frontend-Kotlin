package com.simats.tenderanalyser.screens

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
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PortalScreen(
    onBidderSignUpClick: () -> Unit,
    onBidderLoginClick: () -> Unit,
    onAuthoritySignUpClick: () -> Unit, // 🚨 Placeholders removed!
    onAuthorityLoginClick: () -> Unit   // 🚨 Placeholders removed!
) {
    val scrollState = rememberScrollState()
    val bgColor = Color(0xFFF8FAFC)
    val bidderBlue = Color(0xFF1B49B5)
    val authorityGold = Color(0xFFC28E0E)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .verticalScroll(scrollState)
    ) {
        // --- Top Bar ---
        Surface(color = Color.White, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
            Text(
                text = "Tender Analyser",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- Hero Image Section ---
        Box(
            modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.DarkGray), // Replace with your building image later
            contentAlignment = Alignment.BottomStart
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Unified Portal Access", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Select your portal to continue", color = Color.LightGray, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BIDDER PORTAL CARD ---
        PortalCard(
            title = "BIDDER PORTAL",
            description = "Search, analyze, and submit your bid proposals. Access the marketplace of active opportunities.",
            icon = Icons.Outlined.PersonSearch,
            themeColor = bidderBlue,
            onSignUpClick = onBidderSignUpClick,
            onLoginClick = onBidderLoginClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- AUTHORITY PORTAL CARD ---
        PortalCard(
            title = "AUTHORITY PORTAL",
            description = "Create, publish, and manage tender applications. Control the procurement lifecycle efficiently.",
            icon = Icons.Outlined.WorkOutline,
            themeColor = authorityGold,
            onSignUpClick = onAuthoritySignUpClick,
            onLoginClick = onAuthorityLoginClick
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Footer ---
        Text(
            text = "By accessing these portals, you agree to our Terms of\nService and Privacy Policy.",
            fontSize = 11.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun PortalCard(title: String, description: String, icon: ImageVector, themeColor: Color, onSignUpClick: () -> Unit, onLoginClick: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(themeColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = themeColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = description, fontSize = 12.sp, color = Color(0xFF64748B), lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(24.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onSignUpClick, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = themeColor)) {
                    Text("Sign Up", fontWeight = FontWeight.Bold, color = Color.White)
                }
                OutlinedButton(onClick = onLoginClick, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, themeColor), colors = ButtonDefaults.outlinedButtonColors(contentColor = themeColor)) {
                    Text("Login", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}