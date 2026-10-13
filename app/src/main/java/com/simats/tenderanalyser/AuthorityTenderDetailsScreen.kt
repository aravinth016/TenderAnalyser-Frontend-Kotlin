package com.simats.tenderanalyser.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.TenderDetail
import kotlinx.coroutines.launch

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val GreenLive = Color(0xFF10B981)
private val OutlineColor = Color(0xFF333333)
private val RedDoc = Color(0xFFEF4444)
private val GreenDoc = Color(0xFF10B981)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AuthorityTenderDetailsScreen(
    tenderId: String,
    onBackClick: () -> Unit,
    onEditTenderClick: () -> Unit,
    onEditTimelineClick: () -> Unit,
    onShareClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var tender by remember { mutableStateOf<TenderDetail?>(null) }

    val tabTitles = listOf("Summary", "Documents", "Submissions (0)", "Timeline")
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { tabTitles.size })

    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getTenderById(tenderId)
            if (response.success && response.tender != null) {
                tender = response.tender
            } else {
                Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                onBackClick()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error. Check connection.", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text(tender?.projectTitle ?: "Loading...", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) } },
                actions = { IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, contentDescription = "Options", tint = Color.White) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {

                    val buttonText = when (pagerState.currentPage) {
                        1 -> "Edit Documents"
                        3 -> "Edit Timeline"
                        else -> "Edit Tender"
                    }
                    val buttonIcon = when (pagerState.currentPage) {
                        1 -> Icons.Default.InsertDriveFile
                        3 -> Icons.Default.Event
                        else -> Icons.Outlined.Edit
                    }

                    Button(
                        onClick = {
                            if (pagerState.currentPage == 3) onEditTimelineClick() else onEditTenderClick()
                        },
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CardBg),
                        border = BorderStroke(1.dp, OutlineColor)
                    ) {
                        Icon(buttonIcon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = buttonText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }

                    Button(
                        onClick = onShareClick,
                        modifier = Modifier.weight(1f).height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share Tender", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
        } else if (tender != null) {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

                // HEADER INFO
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Surface(color = Gold.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                        Text("NATIONAL TENDERS +4", color = Gold, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        val isLive = tender!!.status.equals("Open", ignoreCase = true)
                        val statusColor = if (isLive) GreenLive else Gold

                        Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Box(modifier = Modifier.size(6.dp).background(statusColor, CircleShape))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(tender!!.status.uppercase(), color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("NIT: ${tender!!.nitNumber}", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(12.dp))

                        Surface(color = Gold.copy(alpha = 0.15f), shape = RoundedCornerShape(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                                Icon(Icons.Default.Schedule, contentDescription = null, tint = Gold, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("DUE: ${tender!!.deadline}", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    containerColor = DarkBg,
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        if (pagerState.currentPage < tabPositions.size) {
                            TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]), color = Gold, height = 3.dp)
                        }
                    },
                    divider = { HorizontalDivider(color = OutlineColor) }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                            text = {
                                Text(
                                    text = title,
                                    color = if (pagerState.currentPage == index) Color.White else TextGray,
                                    fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }
                }

                HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                    when (page) {
                        0 -> SummaryTabContent(tender!!)
                        1 -> DocumentsTabContent(tender!!)
                        2 -> SubmissionsTabContent()
                        3 -> TimelineTabContent(tender!!.deadline)
                    }
                }
            }
        }
    }
}

// ==========================================
// PAGER CONTENT 1: SUMMARY TAB
// ==========================================
@Composable
fun SummaryTabContent(tender: TenderDetail) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth().height(220.dp).background(Color(0xFF2A2C39))) {
                Icon(Icons.Default.Train, contentDescription = null, tint = Color.White.copy(alpha = 0.1f), modifier = Modifier.size(100.dp).align(Alignment.Center))
            }

            Card(
                modifier = Modifier.fillMaxWidth().padding(top = 180.dp, start = 16.dp, end = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                border = BorderStroke(1.dp, OutlineColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("KEY PARAMETERS", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("ESTIMATED VALUE", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("₹${tender.estimatedValue}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("EMD AMOUNT", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("₹${tender.emdAmount}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("WORK LOCATION", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(tender.location, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TENDER CATEGORY", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(tender.sector, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text("Project Description", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Text(tender.projectDescription, color = TextGray, fontSize = 13.sp, lineHeight = 20.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("READ FULL SCOPE", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Gold, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

// ==========================================
// PAGER CONTENT 2: DOCUMENTS TAB
// ==========================================
@Composable
fun DocumentsTabContent(tender: TenderDetail) {
    val documents = tender.tenderDocs ?: emptyList()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("PROJECT DOCUMENTS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Text("${documents.size} Files Attached", color = TextGray, fontSize = 10.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (documents.isEmpty()) {
            Text("No documents have been uploaded for this tender yet.", color = TextGray, fontSize = 13.sp, modifier = Modifier.padding(top = 16.dp))
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(documents.size) { index ->
                    val doc = documents[index]
                    val fileName = doc.title ?: doc.fileUrl?.substringAfterLast('/') ?: "Document"
                    val fileRef = doc.title ?: doc.fileUrl ?: ""
                    val isExcel = fileRef.endsWith(".xls", true) || fileRef.endsWith(".xlsx", true)

                    ProjectDocumentCard(
                        title = fileName,
                        subtitle = if (isExcel) "SPREADSHEET" else "PDF",
                        iconColor = if (isExcel) GreenDoc else RedDoc
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun ProjectDocumentCard(title: String, subtitle: String, iconColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, OutlineColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(if (iconColor == RedDoc) Icons.Default.PictureAsPdf else Icons.Default.TableView, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(subtitle, color = TextGray, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { }) {
                Icon(Icons.Outlined.Download, contentDescription = "Download", tint = Gold)
            }
        }
    }
}

// ==========================================
// PAGER CONTENT 3: SUBMISSIONS TAB
// ==========================================
@Composable
fun SubmissionsTabContent() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            border = BorderStroke(1.dp, OutlineColor)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("No bids submitted yet.", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(16.dp))
            }
        }
    }
}

// ==========================================
// PAGER CONTENT 4: TIMELINE TAB
// ==========================================
@Composable
fun TimelineTabContent(liveDeadline: String) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 24.dp)) {
        item {
            TimelineStatusItem("Publication of NIT", "Tender officially published.", "COMPLETED", false)
            TimelineStatusItem("Pre-bid Meeting", "Reviewing technical queries.", "ACTIVE", false)
            TimelineStatusItem("Bid Submission Deadline", "Target: $liveDeadline", "FUTURE", false)
            TimelineStatusItem("Technical Bid Opening", "Pending submission closure", "FUTURE", false)
            TimelineStatusItem("Final Award Date", "To be announced", "FUTURE", true)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun TimelineStatusItem(title: String, subtitle: String, status: String, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(30.dp)) {
            when (status) {
                "COMPLETED" -> { Box(modifier = Modifier.size(24.dp).background(Gold, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp)) } }
                "ACTIVE" -> { Box(modifier = Modifier.size(24.dp).border(2.dp, Gold, CircleShape), contentAlignment = Alignment.Center) { Box(modifier = Modifier.size(10.dp).background(Gold, CircleShape)) } }
                else -> { Box(modifier = Modifier.size(20.dp).border(2.dp, OutlineColor, CircleShape)) }
            }

            if (!isLast) {
                val lineColor = if (status == "COMPLETED") Gold else OutlineColor
                if (status == "COMPLETED") {
                    Box(modifier = Modifier.width(2.dp).fillMaxHeight().background(lineColor))
                } else {
                    Canvas(modifier = Modifier.width(2.dp).fillMaxHeight()) {
                        drawLine(color = OutlineColor, start = Offset(size.width / 2, 0f), end = Offset(size.width / 2, size.height), strokeWidth = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                    }
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 32.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(title, color = if (status == "FUTURE") TextGray else Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                if (status == "ACTIVE") {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(color = Gold, shape = RoundedCornerShape(4.dp)) { Text("ACTIVE", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(subtitle, color = if (status == "ACTIVE") Gold else TextGray, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
}