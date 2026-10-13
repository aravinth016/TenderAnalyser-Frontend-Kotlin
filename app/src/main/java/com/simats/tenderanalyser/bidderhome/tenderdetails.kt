package com.simats.tenderanalyser.bidderhome

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.TenderDetail
import com.simats.tenderanalyser.TenderDoc
import com.simats.tenderanalyser.components.TenderAgentBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TenderDetails(
    tenderId: String,
    onBackClick: () -> Unit = {},
    onAnalyzeClick: () -> Unit = {},
    onBidClick: () -> Unit = {},
    onNavigateToMyBids: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {}
) {
    val context = LocalContext.current
    var tender by remember { mutableStateOf<TenderDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var hasAlreadyBid by remember { mutableStateOf(false) }
    var showAlreadyBidDialog by remember { mutableStateOf(false) }

    // State for AI Agent Bottom Sheet
    var showAiAgent by remember { mutableStateOf(false) }

    val sharedPrefs = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
    val currentUserId = sharedPrefs.getString("USER_ID", "1") ?: "1"

    val tabs = listOf("Summary", "Documents", "Timeline", "Bidding Rules")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getTenderById(tenderId)
            if (response.success && response.tender != null) {
                tender = response.tender
            } else {
                Toast.makeText(context, response.message ?: "Tender not found", Toast.LENGTH_SHORT).show()
                onBackClick()
            }

            val bidResponse = ApiClient.apiService.getMyBids(currentUserId)
            if (bidResponse.success) {
                hasAlreadyBid = bidResponse.bids?.any { it.tenderId == tenderId } ?: false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            onBackClick()
        } finally {
            isLoading = false
        }
    }

    if (showAlreadyBidDialog) {
        Dialog(onDismissRequest = { showAlreadyBidDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(60.dp).background(Color(0xFFECFDF5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Bid Already Submitted", color = Color(0xFF1E293B), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "To edit your bid, go to the My Bids section, or use the withdraw button below.",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                showAlreadyBidDialog = false
                                onNavigateToDashboard()
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                        ) {
                            Text("Dashboard", color = Color(0xFF1E293B), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        Button(
                            onClick = {
                                showAlreadyBidDialog = false
                                onNavigateToMyBids()
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                        ) {
                            Text("My Bids", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            if (!isLoading && tender != null) {
                TenderDetailsHeader(tender!!, onBackClick)
            } else {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding()) {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back") }
                }
            }
        },
        bottomBar = {
            TenderDetailsBottomBar(
                onAnalyzeClick = { showAiAgent = true },
                onBidClick = onBidClick,
                onAlreadyBidClick = { showAlreadyBidDialog = true },
                onDeleteBidClick = {
                    coroutineScope.launch {
                        try {
                            val requestMap = mapOf(
                                "tender_id" to tenderId,
                                "user_id" to currentUserId
                            )
                            val response = ApiClient.apiService.deleteBid(requestMap)
                            if (response.success) {
                                Toast.makeText(context, "Bid withdrawn successfully", Toast.LENGTH_SHORT).show()
                                hasAlreadyBid = false
                                onNavigateToDashboard()
                            } else {
                                Toast.makeText(context, response.message ?: "Failed to withdraw bid", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Network Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                hasAlreadyBid = hasAlreadyBid,
                isLoading = isLoading
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2563EB))
            }
        } else if (tender != null) {
            Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        if (pagerState.currentPage < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                                color = Color(0xFF3B82F6),
                                height = 3.dp
                            )
                        }
                    },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 13.sp,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (pagerState.currentPage == index) Color.White else Color(0xFF94A3B8)
                                )
                            }
                        )
                    }
                }

                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        when (page) {
                            0 -> SummaryTabContent(tender!!)
                            1 -> DocumentsTabContent(tender!!.tenderDocs)
                            2 -> TimelineTabContent(tender!!.deadline)
                            3 -> BiddingRulesTabContent()
                        }
                    }
                }
            }
        }
    }

    // AI Agent Bottom Sheet Integration with full document text context
    val currentTender = tender
    if (showAiAgent && currentTender != null) {
        val docsContext = currentTender.tenderDocs?.joinToString("\n") { doc ->
            "- ${doc.title ?: "Document"} (${doc.fileUrl ?: ""})"
        } ?: "No documents attached."

        val tenderSummaryContext = """
            Project Title: ${currentTender.projectTitle}
            NIT Number: ${currentTender.nitNumber}
            Sector: ${currentTender.sector}
            Status: ${currentTender.status}
            Estimated Cost: ${currentTender.estimatedValue}
            EMD Amount: ${currentTender.emdAmount}
            Deadline: ${currentTender.deadline}
            Location: ${currentTender.location}
            
            ATTACHED DOCUMENTS UPLOADED BY AUTHORITY:
            $docsContext
            
            Scope of Work: ${currentTender.projectDescription}
        """.trimIndent()

        TenderAgentBottomSheet(
            tenderId = tenderId,
            tenderTitle = currentTender.projectTitle,
            tenderDetails = tenderSummaryContext,
            onDismiss = { showAiAgent = false }
        )
    }
}

// ----------------------------------------------------
// HEADER & BOTTOM BAR
// ----------------------------------------------------
@Composable
fun TenderDetailsHeader(tender: TenderDetail, onBackClick: () -> Unit) {
    val isActive = tender.status.equals("Open", ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0F172A))
            .padding(top = 16.dp, bottom = 8.dp)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(20.dp)) }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) { Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(20.dp)) }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
            Surface(color = if (isActive) Color(0xFF064E3B) else Color(0xFF334155), shape = RoundedCornerShape(4.dp)) {
                Text(
                    text = tender.status.uppercase(),
                    color = if (isActive) Color(0xFF34D399) else Color(0xFFCBD5E1),
                    fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(tender.projectTitle, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(tender.sector, color = Color(0xFF94A3B8), fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text("NIT: ${tender.nitNumber}", color = Color(0xFF93C5FD), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun TenderDetailsBottomBar(
    onAnalyzeClick: () -> Unit,
    onBidClick: () -> Unit,
    onAlreadyBidClick: () -> Unit,
    onDeleteBidClick: () -> Unit,
    hasAlreadyBid: Boolean,
    isLoading: Boolean
) {
    val buttonText = if (isLoading) "Checking..." else if (hasAlreadyBid) "Already Bid" else "Start Bidding"
    val buttonColor = if (hasAlreadyBid) Color(0xFF10B981) else Color(0xFF2563EB)

    Surface(color = Color.White, shadowElevation = 16.dp, modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAnalyzeClick,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A))
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color(0xFF60A5FA))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Analyze with AI", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        if (!isLoading) {
                            if (hasAlreadyBid) onAlreadyBidClick() else onBidClick()
                        }
                    },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                ) {
                    Text(buttonText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    if (hasAlreadyBid) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    } else {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            if (hasAlreadyBid) {
                Button(
                    onClick = onDeleteBidClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Withdraw Bid", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

// ----------------------------------------------------
// TAB 1: SUMMARY
// ----------------------------------------------------
@Composable
fun SummaryTabContent(tender: TenderDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        DetailSection("SCOPE OF WORK", tender.projectDescription)

        Column {
            Text("FINANCIAL DETAILS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FinancialRow("Estimated Cost", tender.estimatedValue, Color(0xFF1E293B))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    FinancialRow("EMD Amount", tender.emdAmount, Color(0xFF1E293B))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    FinancialRow("Location", tender.location, Color.Gray)
                }
            }
        }
    }
}

@Composable
fun FinancialRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 13.sp, color = Color.Gray)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = valueColor)
    }
}

@Composable
fun DetailSection(title: String, body: String) {
    Column {
        Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(body, fontSize = 13.sp, color = Color(0xFF1E293B), lineHeight = 20.sp)
    }
}

// ----------------------------------------------------
// TAB 2: DOCUMENTS
// ----------------------------------------------------
@Composable
fun DocumentsTabContent(docs: List<TenderDoc>?) {
    val context = LocalContext.current

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (docs.isNullOrEmpty()) {
            Text("No additional documents uploaded.", color = Color.Gray, fontSize = 14.sp)
        } else {
            docs.forEach { doc ->
                val docUrl = doc.fileUrl ?: ""
                val title = doc.title ?: docUrl.substringAfterLast("/").substringAfterLast("_")
                val isExcel = docUrl.endsWith(".xls", true) || docUrl.endsWith(".xlsx", true)

                DocumentCard(
                    title = if (title.isNotBlank()) title else "Document",
                    subtitle = if (isExcel) "SPREADSHEET (Tap to view)" else "PDF (Tap to view)",
                    onClick = {
                        openDocumentUrl(context, docUrl)
                    }
                )
            }
        }
    }
}

@Composable
fun DocumentCard(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFEF2F2)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.PictureAsPdf, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, fontSize = 11.sp, color = Color.Gray)
            }
            Icon(Icons.Outlined.Download, contentDescription = "View", tint = Color(0xFF2563EB))
        }
    }
}

// ----------------------------------------------------
// TAB 3: TIMELINE
// ----------------------------------------------------
@Composable
fun TimelineTabContent(deadline: String) {
    Column(modifier = Modifier.padding(start = 8.dp)) {
        TimelineStep("Tender Published", "Live Now", true, false)
        TimelineStep("Pre-Bid Clarification Window", "Currently Open", true, false)
        TimelineStep("Bid Submission Deadline", deadline, false, false)
        TimelineStep("Technical Bid Opening", "To be notified after deadline", false, true)
    }
}

@Composable
fun TimelineStep(title: String, date: String, isCompleted: Boolean, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(if (isCompleted) Color(0xFF10B981) else Color(0xFFCBD5E1)))
            if (!isLast) Box(modifier = Modifier.width(2.dp).height(50.dp).background(if (isCompleted) Color(0xFF10B981) else Color(0xFFCBD5E1)))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(modifier = Modifier.height(4.dp))
            val dateColor = if (title == "Bid Submission Deadline") Color(0xFF2563EB) else Color.Gray
            val dateWeight = if (title == "Bid Submission Deadline") FontWeight.Bold else FontWeight.Normal
            Text(date, fontSize = 12.sp, color = dateColor, fontWeight = dateWeight)
        }
    }
}

// ----------------------------------------------------
// TAB 4: BIDDING RULES
// ----------------------------------------------------
@Composable
fun BiddingRulesTabContent() {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        DetailSection(
            title = "ELIGIBILITY REQUIREMENTS",
            body = "• Must have completed at least one similar project worth 80% of the estimated cost in the last 5 years.\n" +
                    "• Average annual financial turnover should be at least 30% of estimated cost during the last 3 years.\n" +
                    "• Valid GST and PAN registration mandatory.\n" +
                    "• Relevant ISO Certifications where applicable."
        )

        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color(0xFFFEF2F2), border = BorderStroke(1.dp, Color(0xFFFECACA))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.WarningAmber, contentDescription = "Warning", tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("PENALTY CLAUSES", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("A penalty of 0.5% of the contract value per week of delay will be levied, up to a maximum of 10% of the total contract value. False documentation will result in immediate disqualification and forfeiture of EMD.", fontSize = 12.sp, color = Color(0xFF7F1D1D), lineHeight = 18.sp)
            }
        }
    }
}

// ----------------------------------------------------
// HELPER: DOCUMENT LAUNCHER WITH BROWSER FALLBACK
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