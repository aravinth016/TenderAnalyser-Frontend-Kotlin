package com.simats.tenderanalyser



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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch


// --- Light Theme Colors ---

private val LightBg = Color(0xFFF4F6F9)

private val CardWhite = Color.White

private val TextBlack = Color(0xFF1E1E1E)

private val TextLightGray = Color(0xFFB0B0B0)

private val Gold = Color(0xFFFFC107)

private val GreenLive = Color(0xFF10B981)

private val GreenLightBg = Color(0xFFE8F7F0)

private val RedReject = Color(0xFFEF4444)

private val AiBg = Color(0xFFFFF9E5)



@OptIn(ExperimentalMaterial3Api::class)

@Composable

fun AuthorityBidReviewScreen(

bidId: String,

onBackClick: () -> Unit,

onConfirmApproveClick: () -> Unit,

onConfirmRejectClick: (String) -> Unit,

onDocumentClick: (String) -> Unit

) {

val context = LocalContext.current

val coroutineScope = rememberCoroutineScope()



// Data States

var isLoading by remember { mutableStateOf(true) }

var isProcessingAction by remember { mutableStateOf(false) }

var bidDetail by remember { mutableStateOf<BidDetail?>(null) }



// Dialog States

var showApproveDialog by remember { mutableStateOf(false) }

var showRejectDialog by remember { mutableStateOf(false) }



// Rejection Form States

var selectedReason by remember { mutableStateOf("Technical Non-compliance") }

var remarks by remember { mutableStateOf("") }



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

},

bottomBar = {

if (!isLoading && bidDetail != null) {

Surface(color = LightBg, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {

Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {

Button(

onClick = { showRejectDialog = true },

modifier = Modifier.weight(1f).height(50.dp),

shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White),

border = BorderStroke(1.dp, RedReject)

) {

Text("Reject", color = RedReject, fontWeight = FontWeight.Bold, fontSize = 15.sp)

}

Button(

onClick = { showApproveDialog = true },

modifier = Modifier.weight(1f).height(50.dp),

shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold)

) {

Text("Approve", color = TextBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)

}

}

}

}

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



// 🚨 UPDATED TO SHOW ALL 7 DOCUMENTS

Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = CardWhite), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {

Column(modifier = Modifier.padding(vertical = 8.dp)) {



// 1. GST & PAN

ReviewDocumentRow("GST & PAN", bidDetail?.gstDoc ?: "Missing", Icons.Default.Assignment, Color(0xFF9C27B0)) {

bidDetail?.gstDoc?.let { onDocumentClick(it) }

}

HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))



// 2. Affidavit

ReviewDocumentRow("Affidavit", bidDetail?.affidavitDoc ?: "Missing", Icons.Default.Description, Color(0xFFFF9800)) {

bidDetail?.affidavitDoc?.let { onDocumentClick(it) }

}

HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))



// 3. EMD Receipt

ReviewDocumentRow("EMD Receipt", bidDetail?.emdDoc ?: "Missing", Icons.Default.AccountBalance, Color(0xFF4CAF50)) {

bidDetail?.emdDoc?.let { onDocumentClick(it) }

}

HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))



// 4. Past Work

ReviewDocumentRow("Past Experience", bidDetail?.pastWorkDoc ?: "Missing", Icons.Default.BusinessCenter, Color(0xFF795548)) {

bidDetail?.pastWorkDoc?.let { onDocumentClick(it) }

}

HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))



// 5. Financial Solvency

ReviewDocumentRow("Financial Solvency", bidDetail?.finSolvencyDoc ?: "Missing", Icons.Default.AccountBalance, Color(0xFF00BCD4)) {

bidDetail?.finSolvencyDoc?.let { onDocumentClick(it) }

}

HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))



// 6. Technical Proposal

ReviewDocumentRow("Technical Proposal", bidDetail?.techDoc ?: "Missing", Icons.Default.Description, Color(0xFF2196F3)) {

bidDetail?.techDoc?.let { onDocumentClick(it) }

}

HorizontalDivider(color = LightBg, thickness = 1.dp, modifier = Modifier.padding(horizontal = 20.dp))



// 7. Financial Overview (BOQ)

ReviewDocumentRow("Financial BOQ", bidDetail?.finDoc ?: "Missing", Icons.Default.AccountBalance, Color(0xFFE91E63)) {

bidDetail?.finDoc?.let { onDocumentClick(it) }

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

Spacer(modifier = Modifier.height(100.dp))

}

}

}



// ==========================================

// DIALOG: APPROVE (Saves to DB first!)

// ==========================================

if (showApproveDialog) {

Dialog(onDismissRequest = { if(!isProcessingAction) showApproveDialog = false }) {

Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {

Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {

Box(modifier = Modifier.size(60.dp).background(Gold, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp)) }

Spacer(modifier = Modifier.height(16.dp))

Text("Approve Submission?", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

Spacer(modifier = Modifier.height(12.dp))

Text(

buildAnnotatedString {

append("You are about to approve ")

withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) { append(bidDetail?.companyName ?: "this company") }

append(" for the ")

withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) { append(bidDetail?.bidAmount ?: "") }

append(" bid. This action will notify the bidder and move them to the award stage.")

},

color = Color(0xFFA0A0A0), fontSize = 13.sp, textAlign = TextAlign.Center, lineHeight = 20.sp

)

Spacer(modifier = Modifier.height(24.dp))

Button(

onClick = {

isProcessingAction = true

coroutineScope.launch {

try {

val response = ApiClient.apiService.reviewBid(bidId, "Approved", "")

if(response.success) {

showApproveDialog = false

onConfirmApproveClick()

} else {

Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()

}

} catch(e: Exception) {

Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()

} finally {

isProcessingAction = false

}

}

},

modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold),

enabled = !isProcessingAction

) {

if (isProcessingAction) {

CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)

} else {

Text("Confirm Approval", color = Color.Black, fontWeight = FontWeight.Bold)

}

}

Spacer(modifier = Modifier.height(8.dp))

TextButton(onClick = { if(!isProcessingAction) showApproveDialog = false }) { Text("Cancel", color = Color.White, fontWeight = FontWeight.Medium) }

}

}

}

}



// ==========================================

// DIALOG: REJECT (Saves to DB first!)

// ==========================================

if (showRejectDialog) {

Dialog(onDismissRequest = { if(!isProcessingAction) showRejectDialog = false }) {

Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {

Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {



Box(modifier = Modifier.size(50.dp).background(Color(0xFF3B1212), CircleShape), contentAlignment = Alignment.Center) {

Icon(Icons.Outlined.ErrorOutline, contentDescription = null, tint = RedReject, modifier = Modifier.size(28.dp))

}

Spacer(modifier = Modifier.height(16.dp))

Text("Reject Submission?", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)

Spacer(modifier = Modifier.height(8.dp))

Text("Please provide a reason for rejecting the bid\nfrom ${bidDetail?.companyName}.", color = Color(0xFFA0A0A0), fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp)



Spacer(modifier = Modifier.height(24.dp))



// Quick Selection Chips

Column(modifier = Modifier.fillMaxWidth()) {

Text("QUICK SELECTION", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

Spacer(modifier = Modifier.height(12.dp))



Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

ReasonChip("Technical Non-compliance", selectedReason) { selectedReason = it }

}

Spacer(modifier = Modifier.height(8.dp))

Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {

ReasonChip("Invalid Documents", selectedReason) { selectedReason = it }

ReasonChip("Excessive Margin", selectedReason) { selectedReason = it }

}

}



Spacer(modifier = Modifier.height(20.dp))



// Additional Remarks

Column(modifier = Modifier.fillMaxWidth()) {

Text("ADDITIONAL REMARKS", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)

Spacer(modifier = Modifier.height(8.dp))

OutlinedTextField(

value = remarks, onValueChange = { remarks = it },

placeholder = { Text("Type detailed feedback here...", color = Color.DarkGray, fontSize = 12.sp) },

modifier = Modifier.fillMaxWidth().height(100.dp),

shape = RoundedCornerShape(8.dp),

textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 12.sp),

colors = OutlinedTextFieldDefaults.colors(

focusedBorderColor = Color.DarkGray, unfocusedBorderColor = Color(0xFF333333),

focusedContainerColor = Color(0xFF141414), unfocusedContainerColor = Color(0xFF141414)

)

)

}



Spacer(modifier = Modifier.height(24.dp))



Button(

onClick = {

isProcessingAction = true

coroutineScope.launch {

try {

val response = ApiClient.apiService.reviewBid(bidId, "Rejected", "$selectedReason: $remarks")

if(response.success) {

showRejectDialog = false

onConfirmRejectClick(selectedReason)

} else {

Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()

}

} catch(e: Exception) {

Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()

} finally {

isProcessingAction = false

}

}

},

modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(8.dp),

colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),

border = BorderStroke(1.dp, RedReject),

enabled = !isProcessingAction

) {

if (isProcessingAction) {

CircularProgressIndicator(modifier = Modifier.size(24.dp), color = RedReject)

} else {

Text("Confirm Rejection", color = RedReject, fontWeight = FontWeight.Bold)

}

}

Spacer(modifier = Modifier.height(8.dp))

TextButton(onClick = { if(!isProcessingAction) showRejectDialog = false }) { Text("Cancel", color = Color.White, fontWeight = FontWeight.Medium) }

}

}

}

}

}



// Helper for the Reject Chips

@Composable

fun ReasonChip(text: String, selectedText: String, onClick: (String) -> Unit) {

val isSelected = text == selectedText

Surface(

color = if (isSelected) Gold.copy(alpha = 0.1f) else Color.Transparent,

shape = RoundedCornerShape(16.dp),

border = BorderStroke(1.dp, if (isSelected) Gold else Color(0xFF444444)),

modifier = Modifier.clickable { onClick(text) }

) {

Text(

text = text,

color = if (isSelected) Gold else Color(0xFFA0A0A0),

fontSize = 11.sp,

fontWeight = FontWeight.Medium,

modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)

)

}

}



@Composable

fun ReviewDocumentRow(title: String, subtitle: String, icon: ImageVector, iconColor: Color, onClick: () -> Unit) {

Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {

Row(verticalAlignment = Alignment.CenterVertically) {

Box(modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {

Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))

}

Spacer(modifier = Modifier.width(16.dp))

Column {

Text(title, color = TextBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(160.dp))

Spacer(modifier = Modifier.height(2.dp))

Text(subtitle, color = TextGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.width(160.dp))

}

}

Icon(Icons.Outlined.RemoveRedEye, contentDescription = "View", tint = TextGray, modifier = Modifier.size(20.dp))

}

}