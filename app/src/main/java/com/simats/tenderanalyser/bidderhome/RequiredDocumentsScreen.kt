package com.simats.tenderanalyser.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.simats.tenderanalyser.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

// --- DATA MODELS ---
enum class DocStatus { MISSING, VERIFIED }

data class DocumentItem(
    val id: String, // Must match Python's expected request.files key
    val title: String,
    val category: String,
    var status: DocStatus,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val isViewOnly: Boolean = false // If true, it cannot be replaced (e.g., GST/PAN)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequiredDocumentsScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPref = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
    val myUserId = sharedPref.getString("USER_ID", "1") ?: "1"

    var documents by remember { mutableStateOf<List<DocumentItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isUploading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }

    var selectedDoc by remember { mutableStateOf<DocumentItem?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // --- FETCH DATA FROM SERVER ON LOAD ---
    LaunchedEffect(Unit) {
        try {
            // Assumes your ApiClient has a getComplianceDocs function returning a ComplianceDocsResponse
            val response = ApiClient.apiService.getComplianceDocs(myUserId)
            if (response.success && response.documents != null) {
                val dbDocs = response.documents
                documents = listOf(
                    // Registration Docs (View Only)
                    DocumentItem("gst", "GST Registration", "LEGAL", if (dbDocs["gst"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.Business, isViewOnly = true),
                    DocumentItem("pan", "PAN Card", "LEGAL", if (dbDocs["pan"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.Badge, isViewOnly = true),

                    // Uploadable / Replaceable Docs
                    DocumentItem("trade_license", "Trade License", "COMPLIANCE", if (dbDocs["trade"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.ReceiptLong),
                    DocumentItem("bank_solvency", "Bank Solvency", "COMPLIANCE", if (dbDocs["bank"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.AccountBalance),
                    DocumentItem("it_return", "IT Returns", "COMPLIANCE", if (dbDocs["it"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.Description),
                    DocumentItem("iso_cert", "ISO 9001 Cert", "QUALITY", if (dbDocs["iso"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.Verified),
                    DocumentItem("msme_cert", "MSME Registration", "QUALITY", if (dbDocs["msme"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.Store),
                    DocumentItem("project_letters", "Project Experience", "EXPERIENCE", if (dbDocs["projects"] != null) DocStatus.VERIFIED else DocStatus.MISSING, Icons.Default.Work)
                )
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load documents", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    // --- FILE PICKER ---
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedDoc?.let { doc ->
                scope.launch {
                    isUploading = true
                    val successStatus = uploadDocumentToServer(context, myUserId, doc.id, it)
                    isUploading = false

                    if (successStatus != null) {
                        // Instantly update the UI to show the green tick
                        documents = documents.map { item ->
                            if (item.id == doc.id) item.copy(status = DocStatus.VERIFIED) else item
                        }

                        if (successStatus == "tender_ready") {
                            showSuccessDialog = true
                        }
                    }
                }
            }
        }
    }

    val verifiedCount = documents.count { it.status == DocStatus.VERIFIED }
    val progress = if (documents.isEmpty()) 0f else verifiedCount.toFloat() / documents.size.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Compliance Tracker", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = { if (progress >= 1f) onBackClick() },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isUploading && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (progress >= 1f) Color(0xFF10B981) else Color(0xFF1B49B5)
                    )
                ) {
                    if (isUploading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text(if (progress >= 1f) "Finish Profile" else "Upload Documents", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1B49B5))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ComplianceProgressBar(progress = progress, verified = verifiedCount, total = documents.size)
                }

                val grouped = documents.groupBy { it.category }
                grouped.forEach { (cat, list) ->
                    item {
                        Text(cat, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                    }
                    items(list) { doc ->
                        DocumentCard(doc = doc, onUploadClick = {
                            selectedDoc = doc
                            showBottomSheet = true
                        })
                    }
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(onDismissRequest = { showBottomSheet = false }, sheetState = sheetState) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (selectedDoc?.status == DocStatus.VERIFIED) "REPLACE ${selectedDoc?.title?.uppercase()}" else "UPLOAD ${selectedDoc?.title?.uppercase()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                BottomSheetOption("Browse PDF Files", Icons.Outlined.Folder) {
                    showBottomSheet = false
                    filePickerLauncher.launch("application/pdf")
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    if (showSuccessDialog) {
        TenderReadyDialog(onDismiss = { showSuccessDialog = false })
    }
}

// --- API LOGIC (SUSPEND) ---
suspend fun uploadDocumentToServer(context: Context, userId: String, docId: String, fileUri: Uri): String? {
    return try {
        val file = uriToFile(context, fileUri)
        val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())

        // docId must perfectly match Python's expected key (e.g., "trade_license")
        val filePart = MultipartBody.Part.createFormData(docId, file.name, requestFile)
        val userBody = userId.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = ApiClient.apiService.updateComplianceDocs(userBody, filePart)

        if (response.success) {
            Toast.makeText(context, "Document Saved!", Toast.LENGTH_SHORT).show()
            response.status
        } else {
            Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_LONG).show()
            null
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Connection Failed: ${e.message}", Toast.LENGTH_LONG).show()
        null
    }
}

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "upload_temp_${System.currentTimeMillis()}.pdf")
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    return file
}

// --- UI COMPONENTS ---
@Composable
fun DocumentCard(doc: DocumentItem, onUploadClick: () -> Unit) {
    val isVerified = doc.status == DocStatus.VERIFIED

    OutlinedCard(
        // Allow clicking ONLY if it is not view-only. If verified but not view-only, they can click to replace.
        modifier = Modifier.fillMaxWidth().then(if (doc.isViewOnly) Modifier else Modifier.clickable { onUploadClick() }),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isVerified) Color(0xFF10B981) else Color(0xFFE2E8F0))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(doc.icon, null, tint = if (isVerified) Color(0xFF10B981) else Color.Gray, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(doc.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                // Dynamic subtext based on status and view-only permissions
                val subText = when {
                    doc.isViewOnly && isVerified -> "Verified & Locked"
                    isVerified -> "Tap to replace document"
                    else -> "Missing Document"
                }
                Text(subText, fontSize = 11.sp, color = if (isVerified) Color(0xFF10B981) else Color.Gray)
            }

            if (isVerified) {
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981))
            } else if (!doc.isViewOnly) {
                Icon(Icons.Default.CloudUpload, null, tint = Color(0xFF1B49B5))
            }
        }
    }
}

@Composable
fun ComplianceProgressBar(progress: Float, verified: Int, total: Int) {
    OutlinedCard(shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("OVERALL COMPLIANCE: ${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = if (progress >= 1f) Color(0xFF10B981) else Color(0xFF1B49B5),
                trackColor = Color(0xFFF1F5F9)
            )
            Text("$verified of $total documents uploaded", fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
fun TenderReadyDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Column(
            modifier = Modifier.fillMaxWidth(0.85f).background(Color.White, RoundedCornerShape(24.dp)).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Stars, null, tint = Color(0xFFEAB308), modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("TENDER READY!", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
            Text("All compliance documents are verified. You can now submit bids for all active tenders.",
                fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
            Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) { Text("Great!") }
        }
    }
}

@Composable
fun BottomSheetOption(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = Color(0xFF1B49B5))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontWeight = FontWeight.Medium)
    }
}