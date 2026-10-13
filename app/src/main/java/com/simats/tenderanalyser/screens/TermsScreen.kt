package com.simats.tenderanalyser.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val primaryBlue = Color(0xFF1B49B5)
    val textGray = Color(0xFF475569)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms of Use (Bidder)", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue)
                    ) {
                        Text("I Understand & Close", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Last Updated: March 2026", fontSize = 12.sp, color = textGray)
            HorizontalDivider(
                modifier = Modifier.width(32.dp).padding(vertical = 8.dp),
                color = primaryBlue,
                thickness = 2.dp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Section 1
            Text("1. Acceptance of Terms", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "By submitting bids or interacting with tenders through the Tender Analyser mobile platform, you agree to comply with these terms. These terms govern vendor eligibility, bid submission rules, and document management workflows.",
                fontSize = 12.sp, color = textGray, lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Section 2
            Text("2. Vendor Credentials & Verification", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Bidders must provide accurate registration details, including valid GSTIN, PAN, and business certificate documentation. Accounts containing fraudulent or unverified credentials will face bid disqualification and platform suspension.",
                fontSize = 12.sp, color = textGray, lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Section 3
            Text("3. AI Tender Summaries Disclaimer", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "AI-driven document summaries provided in the app are designed to assist in quick evaluation. They do not replace original tender documents published by authorities. Bidders are strictly required to verify official PDFs before submitting pricing proposals.",
                fontSize = 12.sp, color = textGray, lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Notice Box
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E222D),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = "Notice", tint = primaryBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BIDDER NOTICE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "The platform holds no legal liability for errors arising from reliance solely on AI summaries. Always inspect the full tender scope.",
                        fontSize = 11.sp, color = Color.LightGray, lineHeight = 16.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Section 4
            Text("4. Bidding & Edit Guidelines", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Bidders are granted full flexibility to update or withdraw submitted proposals prior to the official closing deadline:",
                fontSize = 12.sp, color = textGray, lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            val bidderRules = listOf(
                "Bids can be edited or replaced prior to the tender deadline.",
                "Withdrawal completely removes your entry and permits a fresh submission.",
                "Uploaded documents are securely stored using cloud storage encryption.",
                "All financial bids submitted are binding once the tender deadline passes."
            )
            bidderRules.forEach { rule ->
                Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.CheckCircleOutline,
                        contentDescription = null,
                        tint = primaryBlue,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(rule, fontSize = 12.sp, color = textGray, lineHeight = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Section 5
            Text("5. Document Privacy & Supabase Storage", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryBlue)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Your compliance documents (GST, PAN, EMD, Solvency) are processed securely through Supabase Cloud Storage and are accessible only by verified evaluation authorities.",
                fontSize = 12.sp, color = textGray, lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}