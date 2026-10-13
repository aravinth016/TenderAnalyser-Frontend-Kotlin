package com.simats.tenderanalyser.screens

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import com.simats.tenderanalyser.TenderDoc
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.util.Calendar

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val FieldBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val GreenLive = Color(0xFF10B981)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityEditTenderScreen(
    tenderId: String,
    onBackClick: () -> Unit,
    onUpdateClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var estValue by remember { mutableStateOf("") }
    var emdAmount by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }
    var nitNumber by remember { mutableStateOf("Loading...") }
    var status by remember { mutableStateOf("...") }

    // 🚨 STATE: Existing Documents (Fetched as TenderDoc objects)
    var existingDocuments by remember { mutableStateOf<List<TenderDoc>>(emptyList()) }

    // 🚨 STATE: New Documents (Picked from Device)
    var newDocumentUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        newDocumentUris = newDocumentUris + uris
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            deadline = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    LaunchedEffect(tenderId) {
        try {
            val response = ApiClient.apiService.getTenderById(tenderId)
            if (response.success && response.tender != null) {
                val t = response.tender
                title = t.projectTitle
                estValue = t.estimatedValue
                emdAmount = t.emdAmount
                location = t.location
                description = t.projectDescription
                deadline = t.deadline
                nitNumber = t.nitNumber
                status = t.status
                // 🚨 Load existing documents safely as List<TenderDoc>
                existingDocuments = t.tenderDocs ?: emptyList()
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
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                TopAppBar(
                    title = { Text("Edit Tender", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Surface(color = if (status.equals("Open", true)) Color(0xFF003314) else Color(0xFF332D00), shape = RoundedCornerShape(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                            Box(modifier = Modifier.size(6.dp).clip(RoundedCornerShape(3.dp)).background(if (status.equals("Open", true)) GreenLive else Gold))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(status.uppercase(), color = if (status.equals("Open", true)) GreenLive else Gold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("NIT: $nitNumber", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EditSectionHeader("GENERAL INFORMATION")

                EditWhiteTextField("PROJECT TITLE", title) { title = it }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditWhiteTextField("ESTIMATED VALUE", estValue, modifier = Modifier.weight(1f), prefix = "₹ ") { estValue = it }
                    EditWhiteTextField("EMD AMOUNT", emdAmount, modifier = Modifier.weight(1f), prefix = "₹ ") { emdAmount = it }
                }

                EditWhiteTextField("WORK LOCATION", location, trailingIcon = Icons.Default.LocationOn) { location = it }

                Text("PROJECT DESCRIPTION", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth().height(140.dp),
                    shape = RoundedCornerShape(8.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 13.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = FieldBg, unfocusedContainerColor = FieldBg,
                        focusedBorderColor = Gold, unfocusedBorderColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ==========================================
                // 2. DOCUMENTS (EXISTING & NEW)
                // ==========================================
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    EditSectionHeader("PROJECT DOCUMENTS")
                    Text(
                        "+ ADD FILES",
                        color = Gold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { filePickerLauncher.launch("application/pdf") }
                    )
                }

                // Show Existing Documents
                if (existingDocuments.isNotEmpty()) {
                    Text("Previously Uploaded", color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 8.dp))
                    existingDocuments.forEach { doc ->
                        val fileName = doc.title ?: doc.fileUrl?.substringAfterLast('/') ?: "Document"
                        DocumentEditCard(fileName, "Existing File", Color(0xFF1E88E5), Icons.Default.InsertDriveFile) {
                            existingDocuments = existingDocuments.filter { it != doc }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Show New Documents
                if (newDocumentUris.isNotEmpty()) {
                    Text("New Uploads", color = TextGray, fontSize = 10.sp, modifier = Modifier.padding(bottom = 8.dp))
                    newDocumentUris.forEach { uri ->
                        val fileName = getEditScreenFileName(context, uri)
                        DocumentEditCard(fileName, "Just now", Color(0xFF10B981), Icons.Default.InsertDriveFile) {
                            newDocumentUris = newDocumentUris.filter { it != uri }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                if (existingDocuments.isEmpty() && newDocumentUris.isEmpty()) {
                    Text("No documents attached.", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 12.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ==========================================
                // 3. TIMELINE (WITH DATE PICKER)
                // ==========================================
                EditSectionHeader("TIMELINE")
                Box(modifier = Modifier.clickable { datePickerDialog.show() }) {
                    EditWhiteTextField(
                        label = "BID SUBMISSION DEADLINE",
                        value = deadline,
                        trailingIcon = Icons.Default.CalendarToday,
                        enabled = false,
                        onValueChange = {}
                    )
                }
                Text("Bidders will be notified of any deadline extension.", color = TextGray, fontSize = 10.sp)

                Spacer(modifier = Modifier.height(32.dp))

                // ==========================================
                // 4. ACTION BUTTONS
                // ==========================================
                Button(
                    onClick = {
                        if (title.isBlank() || description.isBlank()) {
                            Toast.makeText(context, "Title and Description cannot be empty", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isUpdating = true

                        coroutineScope.launch {
                            try {
                                val idBody = tenderId.toRequestBody("text/plain".toMediaTypeOrNull())
                                val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                                val estValueBody = estValue.toRequestBody("text/plain".toMediaTypeOrNull())
                                val emdAmountBody = emdAmount.toRequestBody("text/plain".toMediaTypeOrNull())
                                val locationBody = location.toRequestBody("text/plain".toMediaTypeOrNull())
                                val descBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                                val deadlineBody = deadline.toRequestBody("text/plain".toMediaTypeOrNull())

                                // 🚨 Retain URLs/files format for Flask backend
                                val retainedUrls = existingDocuments.mapNotNull { it.fileUrl }
                                val retainedDocsJsonStr = "[${retainedUrls.joinToString(",") { "\"$it\"" }}]"
                                val retainedDocsBody = retainedDocsJsonStr.toRequestBody("application/json".toMediaTypeOrNull())

                                // Prepare new files
                                val fileParts = newDocumentUris.map { uri ->
                                    val inputStream = context.contentResolver.openInputStream(uri)
                                    val bytes = inputStream?.readBytes() ?: ByteArray(0)
                                    val requestFile = bytes.toRequestBody("application/pdf".toMediaTypeOrNull())
                                    MultipartBody.Part.createFormData("newDocuments", getEditScreenFileName(context, uri), requestFile)
                                }

                                val response = ApiClient.apiService.updateTenderWithFiles(
                                    tender_id = idBody, title = titleBody, est_value = estValueBody,
                                    emd_amount = emdAmountBody, location = locationBody, description = descBody,
                                    deadline = deadlineBody, retainedDocs = retainedDocsBody,
                                    newDocuments = if (fileParts.isEmpty()) null else fileParts
                                )

                                if (response.success) {
                                    Toast.makeText(context, "Tender Updated Successfully!", Toast.LENGTH_SHORT).show()
                                    onUpdateClick()
                                } else {
                                    Toast.makeText(context, response.message ?: "Update failed", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: HttpException) {
                                Toast.makeText(context, "Server Error ${e.code()}", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                Toast.makeText(context, "Network Error: Check connection.", Toast.LENGTH_LONG).show()
                            } finally {
                                isUpdating = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    enabled = !isUpdating
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black, strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Update & Notify Bidders", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Text("Discard Changes", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- Helper Functions ---

private fun getEditScreenFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) result = cursor.getString(index)
            }
        }
    }
    return result ?: uri.path?.substringAfterLast('/') ?: "Unknown_File"
}

@Composable
private fun EditSectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
        Box(modifier = Modifier.width(3.dp).height(14.dp).background(Gold))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
private fun EditWhiteTextField(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    prefix: String = "",
    trailingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column(modifier = modifier.padding(bottom = 16.dp)) {
        Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(4.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Medium),
            prefix = { if (prefix.isNotEmpty()) Text(prefix, color = Color.Gray, fontWeight = FontWeight.Bold) },
            trailingIcon = trailingIcon?.let { { Icon(it, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp)) } },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                disabledContainerColor = Color.White,
                disabledTextColor = Color.Black,
                disabledTrailingIconColor = Color.Gray,
                focusedBorderColor = Gold, unfocusedBorderColor = Color.Transparent, disabledBorderColor = Color.Transparent
            ),
            singleLine = true
        )
    }
}

@Composable
private fun DocumentEditCard(fileName: String, date: String, iconColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, onDelete: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = FieldBg),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(40.dp).background(iconColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(fileName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                    Text("Added: $date", color = TextGray, fontSize = 10.sp)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color(0xFFEF5350))
            }
        }
    }
}