package com.simats.tenderanalyser.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkBg = Color(0xFF121212)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityTermsScreen(onBackClick: () -> Unit) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AUTHORITY TERMS OF USE",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Gavel, contentDescription = "Legal", tint = Gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .navigationBarsPadding()
                ) {
                    Text(
                        "DOCUMENT ID: AUTH-HUB-LEGAL-2026-V1",
                        color = OutlineColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Text("ACCEPT & CLOSE", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text("GOVERNANCE PROTOCOL", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Authority Portal", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("VERSION 1.0.0", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("Last Rev: March 2026", color = TextGray, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = OutlineColor)
            Spacer(modifier = Modifier.height(24.dp))

            TermsSection(
                title = "1. Administrative Authentication & Access Control",
                body = "Access to the Tendering Authority Portal is strictly reserved for authorized evaluation officers and procurement personnel. All administrative interactions are authenticated via secure role-based permissions to ensure compliance and prevent unauthorized access."
            )

            TermsSection(
                title = "2. Document Integrity & Cloud Storage",
                body = "All tender specifications, compliance documents, and bidder submissions are securely handled using Supabase Cloud Storage and PostgreSQL database infrastructure. Authority officers must maintain strict confidentiality when inspecting vendor GST, PAN, and financial credentials."
            )

            TermsSection(
                title = "3. Automated Evaluation & Analytics Disclaimer",
                body = "AI-assisted analytics tools in this portal are provided as decision-support mechanisms to accelerate technical and financial evaluations. Officers are required to review all bidder proposal documents independently before issuing final award selections or rejections."
            )

            TermsSection(
                title = "4. Audit Trail & Cryptographic Timestamps",
                body = "Every administrative action—including tender creation, proposal status changes ('Under Review', 'Accepted', 'Rejected'), and document verifications—is logged with immutable timestamps for transparency and statutory compliance."
            )

            TermsSection(
                title = "5. Data Sovereignty & Jurisdiction",
                body = "All procurement data hosted within the Tender Analyser environment remains protected under applicable procurement and digital data protection frameworks. Any disputes or discrepancies arising from portal usage are subject to primary jurisdictional courts."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TermsSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(title, color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(body, color = TextGray, fontSize = 12.sp, lineHeight = 20.sp, textAlign = TextAlign.Justify)
    }
}