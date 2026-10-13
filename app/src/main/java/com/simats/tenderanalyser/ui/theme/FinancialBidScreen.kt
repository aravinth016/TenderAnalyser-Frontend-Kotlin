package com.simats.tenderanalyser.screens // 🚨 Ensure this matches your file's location

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.simats.tenderanalyser.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinancialBidScreen(
    tenderId: String,
    onBackClick: () -> Unit = {},
    onDoneClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // --- STATES ---
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var generatedReferenceId by remember { mutableStateOf("") }

    var quotedPrice by remember { mutableStateOf("12,45,00,000") }
    var financialFileUri by remember { mutableStateOf<Uri?>(null) }

    val sheetState = rememberModalBottomSheetState()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) financialFileUri = uri
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color(0xFF1E40AF)).statusBarsPadding()) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Column {
                        Text("Construction of Bridge Over River X", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("STEP 4: FINANCIAL BID UPLOAD", color = Color.White.copy(alpha=0.7f), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        bottomBar = {
            BottomActionRow(
                isUploading = isUploading,
                onSaveClick = { showSaveDialog = true },
                onSubmitClick = {
                    if (quotedPrice.isBlank() || financialFileUri == null) {
                        Toast.makeText(context, "Please attach BOQ document", Toast.LENGTH_SHORT).show()
                    } else {
                        showConfirmDialog = true
                    }
                }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {

            // Stepper
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                StepCircleItem("General", true)
                StepLineItem(true)
                StepCircleItem("Technical", true)
                StepLineItem(true)
                StepCircleItem("Payment", true)
                StepLineItem(true)
                StepCircleItem("Financial", false, isActive = true)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Document Section
            Text("Upload Financial Document", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Please upload the filled BOQ (Bill of Quantities) in PDF or Excel format. Max size: 10MB.", fontSize = 11.sp, color = Color.Gray, lineHeight = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))

            if (financialFileUri == null) {
                OutlinedButton(
                    onClick = { launcher.launch("*/*") },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.CloudUpload, null, tint = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Browse Files to Upload", color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)
                }
            } else {
                FileCardItem(
                    fileName = getFinancialFileName(context, financialFileUri!!),
                    onDelete = { financialFileUri = null }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Price Input Section
            Text("QUOTED PRICE (INR)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = quotedPrice,
                onValueChange = { quotedPrice = it },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("₹ ", fontWeight = FontWeight.Bold, color = Color.Black) },
                textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.ExtraBold, fontSize = 18.sp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color(0xFFE2E8F0), focusedBorderColor = Color(0xFF1E40AF))
            )
            Text("TWELVE CRORES FORTY FIVE LAKHS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), modifier = Modifier.padding(top = 8.dp, start = 4.dp))

            Spacer(modifier = Modifier.height(32.dp))

            // Warning Banner
            Surface(color = Color(0xFFFFFBEB), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFFEF3C7))) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Outlined.WarningAmber, null, tint = Color(0xFFD97706), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Once submitted, the financial bid cannot be modified. Ensure the quoted price matches exactly with the attached document.", fontSize = 11.sp, color = Color(0xFF92400E), lineHeight = 16.sp)
                }
            }
        }

        // --- DIALOGS ---

        if (showConfirmDialog) {
            ConfirmSubmissionPopup(
                price = "₹$quotedPrice",
                onCancel = { showConfirmDialog = false },
                onConfirm = {
                    showConfirmDialog = false
                    isUploading = true

                    coroutineScope.launch {
                        try {
                            val textType = "text/plain".toMediaTypeOrNull()

                            val tIdBody = tenderId.toRequestBody(textType)
                            val uIdBody = "1".toRequestBody(textType) // Hardcoded User ID "1" for testing
                            val cleanPrice = quotedPrice.replace(",", "")
                            val amountBody = cleanPrice.toRequestBody(textType)

                            val finPart = getFinancialMultipartFromUri(context, financialFileUri!!, "financial_doc")

                            // 🚨 Using ApiClient here! Sending straight to Python on Port 5000
                            val response = ApiClient.apiService.submitBid(
                                tenderId = tIdBody,
                                bidderId = uIdBody,
                                bidAmount = amountBody,
                                technicalDoc = null,
                                financialDoc = finPart,
                                emdReceipt = null,
                                gstPanDoc = null,
                                affidavitDoc = null,
                                pastWorkDoc = null,
                                finSolvencyDoc = null
                            )

                            isUploading = false
                            if (response.success) {
                                generatedReferenceId = "LIVE-BID-${System.currentTimeMillis().toString().takeLast(6)}"
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(context, response.message ?: "Submission failed", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            isUploading = false
                            Toast.makeText(context, "Upload Failed: Check network connection", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }

        if (showSuccessDialog) {
            SuccessPopup(bidId = generatedReferenceId.ifBlank { "LIVE-BID-0512" }, onDone = { showSuccessDialog = false; onDoneClick() })
        }

        if (showSaveDialog) {
            ModalBottomSheet(onDismissRequest = { showSaveDialog = false }, sheetState = sheetState, containerColor = Color.White) {
                DraftSavedContent(onGoToDrafts = { showSaveDialog = false; onDoneClick() })
            }
        }
    }
}

// ==========================================
// 🎨 POPUP WINDOWS
// ==========================================

@Composable
fun ConfirmSubmissionPopup(price: String, onCancel: () -> Unit, onConfirm: () -> Unit) {
    Dialog(onDismissRequest = onCancel) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Surface(modifier = Modifier.size(64.dp), shape = CircleShape, color = Color(0xFFEFF6FF)) {
                    Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.padding(16.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Confirm Submission?", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "You are about to submit your final bid of\n$price. Once submitted,\ndocuments and prices cannot be modified.",
                    textAlign = TextAlign.Center, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162A70)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm & Submit", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
                TextButton(onClick = onCancel, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Cancel", color = Color.Gray, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun SuccessPopup(bidId: String, onDone: () -> Unit) {
    Dialog(onDismissRequest = onDone) {
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onDone, modifier = Modifier.align(Alignment.TopEnd).padding(8.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
                Column(modifier = Modifier.padding(24.dp).padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.size(72.dp).background(Color(0xFF00C853), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Submission Successful!", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp)).padding(16.dp)) {
                        Text("PROJECT DETAILS", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("PROJECT", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Text("Construction of Bridge Over River X", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("AUTHORITY", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
                        Text("NHAI", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("BID REFERENCE NUMBER", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)).padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(bidId, fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF), fontSize = 14.sp)
                            Icon(Icons.Outlined.ContentCopy, contentDescription = "Copy", tint = Color(0xFF1E40AF), modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    Button(
                        onClick = onDone,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162A70)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Done", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}

// ==========================================
// BOTTOM BAR, STEPPER, AND FILE CARDS
// ==========================================

@Composable
fun BottomActionRow(isUploading: Boolean, onSaveClick: () -> Unit, onSubmitClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp, color = Color.White) {
        Row(modifier = Modifier.padding(16.dp).navigationBarsPadding(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onSaveClick, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), enabled = !isUploading) {
                Text("Save", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onSubmitClick, modifier = Modifier.weight(2f).height(54.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF162A70)), enabled = !isUploading) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text("Submit Bid ↗", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StepCircleItem(label: String, isDone: Boolean, isActive: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(24.dp).background(if (isDone) Color(0xFF10B981) else if (isActive) Color(0xFF1E40AF) else Color.LightGray, CircleShape), contentAlignment = Alignment.Center) {
            if (isDone) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            else if (isActive) Text("4", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
        Text(label, fontSize = 9.sp, modifier = Modifier.padding(top = 4.dp), color = if (isActive) Color.Black else Color.Gray)
    }
}

@Composable
fun StepLineItem(isDone: Boolean) {
    Box(modifier = Modifier.width(40.dp).height(2.dp).background(if (isDone) Color(0xFF10B981) else Color.LightGray))
}

@Composable
fun FileCardItem(fileName: String, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFF1F5F9))) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFECFDF5), modifier = Modifier.size(40.dp)) {
                Icon(Icons.Outlined.InsertDriveFile, null, tint = Color(0xFF10B981), modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(fileName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("UPLOADED", color = Color(0xFF10B981), fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Default.DeleteOutline, null, tint = Color.LightGray) }
        }
    }
}

@Composable
fun DraftSavedContent(onGoToDrafts: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = Color(0xFFE3F2FD)) { Icon(Icons.Default.CloudDone, null, tint = Color(0xFF2563EB), modifier = Modifier.padding(20.dp)) }
        Spacer(modifier = Modifier.height(24.dp))
        Text("Draft Saved Successfully!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGoToDrafts, modifier = Modifier.fillMaxWidth().height(54.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)), shape = RoundedCornerShape(12.dp)) { Text("Go to Drafts", fontWeight = FontWeight.Bold) }
    }
}

// ==========================================
// FILE HELPERS
// ==========================================

private fun getFinancialFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    result = cursor.getString(index)
                }
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/') ?: -1
        if (cut != -1) {
            result = result?.substring(cut + 1)
        }
    }
    return result ?: "document.pdf"
}

private fun getFinancialMultipartFromUri(context: Context, uri: Uri, partName: String): MultipartBody.Part? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val bytes = inputStream.readBytes()
        val requestBody = bytes.toRequestBody("application/pdf".toMediaTypeOrNull())
        val fileName = getFinancialFileName(context, uri)
        MultipartBody.Part.createFormData(partName, fileName, requestBody)
    } catch (e: Exception) {
        null
    }
}