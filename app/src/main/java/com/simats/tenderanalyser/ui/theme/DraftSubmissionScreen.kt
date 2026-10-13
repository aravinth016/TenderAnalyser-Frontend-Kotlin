package com.simats.tenderanalyser.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DraftSubmissionScreen(
    tenderId: String = "Draft",
    onBackClick: () -> Unit,
    onResumeBiddingClick: () -> Unit,
    onInfoClick: () -> Unit
) {
    Scaffold(
        topBar = { DraftTopBar(tenderId, onBackClick, onInfoClick) },
        bottomBar = { DraftBottomBar(onResumeBiddingClick) },
        containerColor = Color(0xFFF8FAFC) // Light gray background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ProgressCard()

            ApplicationStepsSection()

            DeadlineInfoBox()

            Spacer(modifier = Modifier.height(20.dp)) // Extra space above bottom bar
        }
    }
}

@Composable
fun DraftTopBar(tenderId: String, onBackClick: () -> Unit, onInfoClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1D4ED8)) // Deep Blue Background
            .padding(top = 16.dp, bottom = 20.dp)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Draft for: $tenderId",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "DRAFT SUBMISSION",
                    color = Color(0xFF93C5FD), // Light blue text
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            IconButton(onClick = onInfoClick) {
                Icon(Icons.Outlined.Info, contentDescription = "Tender Info", tint = Color.White)
            }
        }
    }
}

@Composable
fun ProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Progress Ring
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(100.dp)
            ) {
                // Background Track
                CircularProgressIndicator(
                    progress = 1f,
                    color = Color(0xFFF1F5F9),
                    strokeWidth = 8.dp,
                    modifier = Modifier.fillMaxSize()
                )
                // Actual Progress (75%)
                CircularProgressIndicator(
                    progress = 0.75f,
                    color = Color(0xFF1D4ED8),
                    strokeWidth = 8.dp,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "75%",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Draft Progress",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You are almost there! Complete the final step\nto submit.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun ApplicationStepsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "APPLICATION STEPS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Gray,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            CompletedStepCard(title = "General Information", date = "Completed on Oct 24, 2023")
            CompletedStepCard(title = "Technical Documents", date = "Completed on Oct 24, 2023")
            CompletedStepCard(title = "EMD Payment", date = "Completed on Oct 25, 2023")
            PendingStepCard(title = "Financial Bid Upload", status = "Pending action")
        }
    }
}

@Composable
fun CompletedStepCard(title: String, date: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Completed", tint = Color(0xFF00C853), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = date, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun PendingStepCard(title: String, status: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)), // Light Blue background
        border = BorderStroke(1.dp, Color(0xFF1D4ED8)), // Blue Border
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = "Pending",
                tint = Color(0xFF1D4ED8),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = status, fontSize = 12.sp, color = Color(0xFF60A5FA))
            }
            Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color(0xFF1D4ED8), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun DeadlineInfoBox() {
    Surface(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)),
        color = Color(0xFFF1F5F9) // Gray-blue bg
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(Icons.Outlined.Info, contentDescription = "Info", tint = Color.Gray, modifier = Modifier.size(20.dp).padding(top = 2.dp))
            Spacer(modifier = Modifier.width(12.dp))

            val infoText = buildAnnotatedString {
                append("The deadline for this tender is ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))) {
                    append("Oct 30, 2023 at 17:00 IST")
                }
                append(". Ensure all digital signatures are valid before the final upload.")
            }

            Text(text = infoText, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 20.sp)
        }
    }
}

@Composable
fun DraftBottomBar(onResumeBiddingClick: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Button(
                onClick = onResumeBiddingClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8))
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Resume Bidding", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}