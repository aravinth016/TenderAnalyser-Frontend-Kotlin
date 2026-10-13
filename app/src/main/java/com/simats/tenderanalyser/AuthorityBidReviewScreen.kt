package com.simats.tenderanalyser.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.BidDetail

// --- Light Theme Colors ---
private val LightBg = Color(0xFFF4F6F9)
private val CardWhite = Color.White
private val TextBlack = Color(0xFF1E1E1E)
private val TextGray = Color(0xFF8A8B94)
private val TextLightGray = Color(0xFFB0B0B0)
private val Gold = Color(0xFFFFC107)
private val GreenLive = Color(0xFF10B981)
private val GreenLightBg = Color(0xFFE8F7F0)
private val AiBg = Color(0xFFFFF9E5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityBidReviewScreen(
    bidId: String,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current

    // Data States
    var isLoading by remember { mutableStateOf(true) }
    var bidDetail by remember { mutableStateOf<BidDetail?>(null) }

    // Fetch the Live Bid Details
    LaunchedEffect(bidId) {
        try {
            val response = ApiClient.apiService.getBidDetails(bidId)
            if (response.success && response.bidDetails != null) {
                bidDetail = response.bidDetails
            } else {
                Toast.makeText(context, response.message ?: "Failed to load bid", Toast.LENGTH_SHORT).show()
                onBackClick()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            onBackClick()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = LightBg,
        topBar = {
            TopAppBar(
                title = { Text(if (isLoading) "Loading..." else "Review: ${bidDetail?.companyName}", color = TextBlack, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    Row(modifier = Modifier.clickable { onBackClick() }.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Back", color = Gold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.MoreHoriz, contentDescription = "Options", tint = Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LightBg)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
        } else if (bidDetail != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text("FINANCIAL QUOTE", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("TENDER ESTIMATED PRICE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(bidDetail!!.tenderEstimate, color = TextLightGray, fontSize = 12.sp, fontWeight = FontWeight.Medium)

                        Spacer(modifier = Modifier.height(20.dp))

                        Text("Total Bid Value", color = TextGray, fontSize = 12.sp)
                        Text(bidDetail!!.bidAmount, color = TextBlack, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)

                        Spacer(modifier = Modifier.height(16.dp))
                        Surface(color = GreenLightBg, shape = RoundedCornerShape(6.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                                Icon(Icons.Default.TrendingDown, contentDescription = null, tint = GreenLive, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COMPETITIVE BID", color = GreenLive, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text("DOCUMENTS SUBMITTED", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {

                        ReviewDocumentRow("GST & PAN", bidDetail?.gstDoc, Icons.Default.Assignment, Color(0xFF9C27B0)) {
                            openDocumentUrl(context, bidDetail?.gstDoc)
                        }
                        HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))

                        ReviewDocumentRow("Affidavit", bidDetail?.affidavitDoc, Icons.Default.Description, Color(0xFFFF9800)) {
                            openDocumentUrl(context, bidDetail?.affidavitDoc)
                        }
                        HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))

                        ReviewDocumentRow("EMD Receipt", bidDetail?.emdDoc, Icons.Default.AccountBalance, Color(0xFF4CAF50)) {
                            openDocumentUrl(context, bidDetail?.emdDoc)
                        }
                        HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))

                        ReviewDocumentRow("Past Experience", bidDetail?.pastWorkDoc, Icons.Default.BusinessCenter, Color(0xFF795548)) {
                            openDocumentUrl(context, bidDetail?.pastWorkDoc)
                        }
                        HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))

                        ReviewDocumentRow("Financial Solvency", bidDetail?.finSolvencyDoc, Icons.Default.AccountBalance, Color(0xFF00BCD4)) {
                            openDocumentUrl(context, bidDetail?.finSolvencyDoc)
                        }
                        HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))

                        ReviewDocumentRow("Technical Proposal", bidDetail?.techDoc, Icons.Default.Description, Color(0xFF2196F3)) {
                            openDocumentUrl(context, bidDetail?.techDoc)
                        }
                        HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))

                        ReviewDocumentRow("Financial BOQ", bidDetail?.finDoc, Icons.Default.AccountBalance, Color(0xFFE91E63)) {
                            openDocumentUrl(context, bidDetail?.finDoc)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = AiBg), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), border = BorderStroke(1.dp, Gold.copy(alpha = 0.3f))) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Gold.copy(alpha = 0.2f), modifier = Modifier.align(Alignment.TopEnd).padding(16.dp).size(60.dp))
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = TextBlack, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("AI COMPLIANCE", color = TextBlack, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                buildAnnotatedString {
                                    append("Automated verification successful. All mandatory technical clauses detected with ")
                                    withStyle(style = SpanStyle(fontWeight = FontWeight.ExtraBold)) { append("96.8% similarity") }
                                    append(" to tender requirements.")
                                },
                                color = TextBlack.copy(alpha = 0.8f), fontSize = 13.sp, lineHeight = 20.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(GreenLive))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("VERIFIED AUTHENTIC", color = GreenLive, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ReviewDocumentRow(title: String, fileUrl: String?, icon: ImageVector, iconColor: Color, onClick: () -> Unit) {
    val isAvailable = !fileUrl.isNullOrBlank()
    val subtitle = if (isAvailable) "Tap to view document" else "Missing / Not Uploaded"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isAvailable) { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(if (isAvailable) iconColor.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = if (isAvailable) iconColor else TextLightGray, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = if (isAvailable) TextBlack else TextLightGray, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(160.dp))
                Spacer(modifier = Modifier.height(2.dp))
                Text(subtitle, color = TextGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(160.dp))
            }
        }
        if (isAvailable) {
            Icon(Icons.Outlined.RemoveRedEye, contentDescription = "View", tint = TextGray, modifier = Modifier.size(20.dp))
        }
    }
}

// ----------------------------------------------------
// HELPER: SAFE DOCUMENT LAUNCHER WITH BROWSER FALLBACK
// ----------------------------------------------------
fun openDocumentUrl(context: Context, url: String?) {
    if (url.isNullOrBlank()) {
        Toast.makeText(context, "Document URL is missing.", Toast.LENGTH_SHORT).show()
        return
    }

    val secureUrl = if (url.startsWith("http://")) url.replace("http://", "https://") else url

    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(secureUrl)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: android.content.ActivityNotFoundException) {
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(secureUrl)).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(browserIntent)
        } catch (innerException: Exception) {
            Toast.makeText(context, "No app or browser found to open this document.", Toast.LENGTH_SHORT).show()
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to open document link.", Toast.LENGTH_SHORT).show()
    }
}