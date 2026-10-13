package com.simats.tenderanalyser.screens

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.TenderDetail
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.Calendar

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityEditTimelineScreen(
    tenderId: String,
    onCloseClick: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showSuccessDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var tender by remember { mutableStateOf<TenderDetail?>(null) }

    // 🚨 STATE: ALL DATES CAN NOW BE EDITED
    var editedDeadline by remember { mutableStateOf("Not Set") }
    var pubDate by remember { mutableStateOf("Not Set") }
    var preBidDate by remember { mutableStateOf("Not Set") }
    var clarificationDate by remember { mutableStateOf("Not Set") }
    var techBidDate by remember { mutableStateOf("Not Set") }
    var awardDate by remember { mutableStateOf("Not Set") }

    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getTenderById(tenderId)
            if (response.success && response.tender != null) {
                tender = response.tender
                // Pre-fill the mandatory deadline from the database
                editedDeadline = response.tender.deadline
            } else {
                Toast.makeText(context, "Failed to load tender", Toast.LENGTH_SHORT).show()
                onCloseClick()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
            onCloseClick()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Edit Timeline", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(tender?.projectTitle ?: "Loading...", color = TextGray, fontSize = 10.sp, maxLines = 1)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) { Icon(Icons.Default.Close, contentDescription = "Close", tint = TextGray) }
                },
                actions = {
                    Surface(color = Gold.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(end = 16.dp)) {
                        Text("MODIFYING", color = Gold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            if (tender == null) return@Button

                            isSaving = true
                            coroutineScope.launch {
                                try {
                                    val idBody = tenderId.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val titleBody = tender!!.projectTitle.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val estValueBody = tender!!.estimatedValue.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val emdAmountBody = tender!!.emdAmount.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val locationBody = tender!!.location.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val descBody = tender!!.projectDescription.toRequestBody("text/plain".toMediaTypeOrNull())

                                    // Main Deadline saves to DB
                                    val deadlineBody = editedDeadline.toRequestBody("text/plain".toMediaTypeOrNull())

                                    val existingDocs = tender!!.tenderDocs ?: emptyList()
                                    val retainedDocsJsonStr = "[${existingDocs.joinToString(",") { "\"$it\"" }}]"
                                    val retainedDocsBody = retainedDocsJsonStr.toRequestBody("application/json".toMediaTypeOrNull())

                                    val response = ApiClient.apiService.updateTenderWithFiles(
                                        tender_id = idBody, title = titleBody, est_value = estValueBody,
                                        emd_amount = emdAmountBody, location = locationBody, description = descBody,
                                        deadline = deadlineBody, retainedDocs = retainedDocsBody, newDocuments = null
                                    )

                                    if (response.success) {
                                        showSuccessDialog = true
                                    } else {
                                        Toast.makeText(context, "Update failed: ${response.message}", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Server Error", Toast.LENGTH_LONG).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onCloseClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        border = BorderStroke(1.dp, OutlineColor)
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Discard Changes", color = TextGray, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(16.dp))

                // 🚨 ALL DATES ARE NOW CLICKABLE CALENDARS!
                Box(modifier = Modifier.clickable { showDatePicker(context) { pubDate = it } }) {
                    TimelineInputField("PUBLICATION DATE", pubDate, Icons.Default.CalendarToday)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.clickable { showDatePicker(context) { preBidDate = it } }) {
                    TimelineInputField("PRE-BID MEETING", preBidDate, Icons.Default.People)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.clickable { showDatePicker(context) { clarificationDate = it } }) {
                    TimelineInputField("CLARIFICATION DEADLINE", clarificationDate, Icons.Outlined.HelpOutline)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.clickable { showDatePicker(context) { editedDeadline = it } }) {
                    TimelineInputField("BID SUBMISSION DEADLINE", editedDeadline, Icons.Default.Description)
                }
                Text("Tap any box above to pick a date.", color = Gold, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.clickable { showDatePicker(context) { techBidDate = it } }) {
                    TimelineInputField("TECHNICAL BID OPENING", techBidDate, Icons.Default.Visibility)
                }
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.clickable { showDatePicker(context) { awardDate = it } }) {
                    TimelineInputField("FINAL AWARD DATE", awardDate, Icons.Default.EmojiEvents)
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    if (showSuccessDialog) {
        Dialog(onDismissRequest = { }) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                border = BorderStroke(1.dp, Gold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(60.dp).background(Gold, CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Timeline Updated\nSuccessfully!", color = Gold, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 26.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        buildAnnotatedString {
                            append("The schedule for ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White)) { append(tender?.projectTitle ?: "this project") }
                            append(" has been officially updated and all active bidders have been notified via secure alerts.")
                        },
                        color = Color(0xFFA0A0A0), fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = {
                            showSuccessDialog = false
                            onSaveSuccess()
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Gold)
                    ) {
                        Text("DONE", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    }
}

// 🚨 HELPER FUNCTION: Pops up a native Android Calendar
fun showDatePicker(context: Context, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onDateSelected("$dayOfMonth/${month + 1}/$year")
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

@Composable
fun TimelineInputField(label: String, value: String, icon: ImageVector) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBg, RoundedCornerShape(8.dp))
                .border(1.dp, OutlineColor, RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Gold, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(value, color = if (value == "Not Set") Color.Gray else Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}