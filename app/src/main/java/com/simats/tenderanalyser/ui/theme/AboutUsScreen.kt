package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val primaryBlue = Color(0xFF1B49B5)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Tender Analyser", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {

            // --- Hero Banner ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(primaryBlue),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Surface(
                        color = Color(0xFFFFC107),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "SMART BIDDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Empowering Bidders", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Streamlined Tender Submissions & Analysis", fontSize = 13.sp, color = Color.White.copy(alpha = 0.8f))
                }
            }

            // --- Mission Statement ---
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.width(20.dp), color = primaryBlue, thickness = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OUR PURPOSE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    HorizontalDivider(
                        modifier = Modifier
                            .width(3.dp)
                            .fillMaxHeight(),
                        color = Color(0xFFFFC107)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "\"Designed to take the complexity out of government & commercial bidding. Tender Analyser helps vendors easily evaluate eligibility requirements, submit verified bids, and track evaluation statuses in real time.\"",
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        lineHeight = 22.sp
                    )
                }
            }

            // --- Key Bidder Capabilities ---
            Text(
                text = "Key Bidder Capabilities",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                BidderFeatureCard(
                    icon = Icons.Outlined.Psychology,
                    title = "AI Document Summarization",
                    description = "Extract key financial criteria, mandatory document checklists, and submission deadlines instantly.",
                    primaryBlue = primaryBlue
                )

                BidderFeatureCard(
                    icon = Icons.Outlined.FileUpload,
                    title = "Seamless Bid Management",
                    description = "Upload compliance documents, GST/PAN proofs, and financial proposals with dynamic edit/withdrawal support.",
                    primaryBlue = primaryBlue
                )

                BidderFeatureCard(
                    icon = Icons.Outlined.FactCheck,
                    title = "Live Status Tracking",
                    description = "Stay updated on your bid evaluation lifecycle from 'Pending Review' to final authority decisions.",
                    primaryBlue = primaryBlue
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Platform Architecture ---
            Text(
                text = "Built for Security & Speed",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF8FAFC),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Powered by Supabase Cloud Storage & PostgreSQL, ensuring bank-grade encryption for all tender documents and financial submissions.",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // --- Footer / Contact ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(primaryBlue)
                    .padding(24.dp)
            ) {
                Text("Support & Inquiries", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(20.dp))

                ContactRow(Icons.Outlined.Email, "Bidder Support Email", "support@tenderanalyser.ai")
                Spacer(modifier = Modifier.height(16.dp))
                ContactRow(Icons.Outlined.Language, "Platform Website", "www.tenderanalyser.ai")
                Spacer(modifier = Modifier.height(16.dp))
                ContactRow(Icons.Outlined.Verified, "Current Build", "Version 1.0 (Portfolio Release)")

                Spacer(modifier = Modifier.height(28.dp))
                HorizontalDivider(color = Color(0xFF3B82F6).copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "© 2026 TENDER ANALYSER. ALL RIGHTS RESERVED.\nv1.0.0",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// --- HELPER COMPOSABLES ---

@Composable
fun BidderFeatureCard(
    icon: ImageVector,
    title: String,
    description: String,
    primaryBlue: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF8FAFC),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(primaryBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, fontSize = 12.sp, color = Color(0xFF64748B), lineHeight = 18.sp)
            }
        }
    }
}

@Composable
fun ContactRow(icon: ImageVector, title: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(value, fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
        }
    }
}