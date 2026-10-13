package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val GreenLive = Color(0xFF10B981)
private val RedFlag = Color(0xFFEF4444)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BidderAwardLetterScreen(
    tenderId: String,
    isAwarded: Boolean,
    rejectionReason: String = "Technical non-compliance or pricing variance",
    onBackClick: () -> Unit,
    onViewAnalyticsClick: (String) -> Unit
) {
    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isAwarded) "OFFICIAL AWARD LETTER" else "EVALUATION NOTICE",
                        color = Gold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, if (isAwarded) GreenLive else RedFlag)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (isAwarded) Icons.Outlined.Verified else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (isAwarded) GreenLive else RedFlag,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isAwarded) "CERTIFICATE OF AWARD" else "NOTICE OF NON-SELECTION",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(text = "Tender Reference ID: $tenderId", color = TextGray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = OutlineColor)
                    Spacer(modifier = Modifier.height(24.dp))

                    if (isAwarded) {
                        Text(
                            text = "This is to officially certify that your proposal has been evaluated through our automated 360° ML Evaluation Engine and successfully selected as the winning bid (L1) for the project execution. Please review the signed issuance terms and proceed with project mobilization.",
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                    } else {
                        Text(
                            text = "Thank you for participating. Following our automated ML evaluation benchmarking pricing, compliance clauses, and BOQ items, your submission was not selected as the winning bid for this tender cycle.",
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(
                            color = RedFlag.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, RedFlag.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("REJECTION REASON RECORDED:", color = RedFlag, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(rejectionReason, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Button that transitions directly to your ComparisonAnalyticsScreen
            Button(
                onClick = { onViewAnalyticsClick(tenderId) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Leaderboard, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Detailed Bidding Analytics", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}