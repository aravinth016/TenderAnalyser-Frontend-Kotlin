package com.simats.tenderanalyser.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageGstPanScreen(
    userId: String = "1", // Make sure this matches your test user ID in MySQL
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccessBanner by remember { mutableStateOf(false) }

    // State Variables
    var gstin by remember { mutableStateOf("") }
    var panNumber by remember { mutableStateOf("") }
    var existingGstFileName by remember { mutableStateOf("") }
    var existingPanFileName by remember { mutableStateOf("") }

    var newGstUri by remember { mutableStateOf<Uri?>(null) }
    var newPanUri by remember { mutableStateOf<Uri?>(null) }

    val gstLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> newGstUri = uri }
    val panLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> newPanUri = uri }

    // Fetch initial data
    LaunchedEffect(userId) {
        try {
            val response = ApiClient.apiService.getGstPan(userId)
            if (response.success) {
                gstin = response.gstin ?: ""
                panNumber = response.panNumber ?: ""
                existingGstFileName = response.gstFileName ?: "Not uploaded"
                existingPanFileName = response.panFileName ?: "Not uploaded"

                // If PAN is empty but GST is present, extract PAN from Indian GSTIN format
                if (panNumber.isEmpty() && gstin.length >= 12) {
                    panNumber = gstin.substring(2, 12)
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error loading details: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White).statusBarsPadding()) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if (isEditing) isEditing = false else onBackClick() }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Color(0xFF1E40AF))
                    }
                    Text(
                        text = if (isEditing) "Update Details" else "Manage GST & PAN",
                        fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black,
                        modifier = Modifier.weight(1f), textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.size(24.dp))
                }
                HorizontalDivider(color = Color(0xFFF1F5F9))
            }
        },
        bottomBar = {
            if (isEditing) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(
                            onClick = {
                                isSaving = true
                                coroutineScope.launch {
                                    try {
                                        val textType = "text/plain".toMediaTypeOrNull()

                                        // 🚨 Calling API correctly via ApiClient
                                        val response = ApiClient.apiService.updateGstPan(
                                            userId = userId.toRequestBody(textType),
                                            gstin = gstin.toRequestBody(textType),
                                            panNumber = panNumber.toRequestBody(textType),
                                            new_gst_certificate = prepareFilePartHelper(context, "new_gst_certificate", newGstUri),
                                            new_pan_card = prepareFilePartHelper(context, "new_pan_card", newPanUri)
                                        )

                                        if (response.success) {
                                            isEditing = false
                                            showSuccessBanner = true
                                            // Update local filenames if changed
                                            if (newGstUri != null) existingGstFileName = getFileNameHelper(context, newGstUri!!)
                                            if (newPanUri != null) existingPanFileName = getFileNameHelper(context, newPanUri!!)
                                        } else {
                                            Toast.makeText(context, "Update failed: ${response.message}", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Network error: Check connection", Toast.LENGTH_LONG).show()
                                    } finally {
                                        isSaving = false
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E40AF)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSaving
                        ) {
                            if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            else Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("By saving changes, you confirm that all provided\ninformation is accurate.", textAlign = TextAlign.Center, fontSize = 9.sp, color = Color.Gray, lineHeight = 12.sp)
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp).verticalScroll(rememberScrollState())) {

                if (!isEditing && showSuccessBanner) {
                    BannerItem(color = Color(0xFF10B981), bgColor = Color(0xFFD1FAE5), icon = Icons.Default.CheckCircle, title = "Documents updated successfully") { showSuccessBanner = false }
                }

                if (!isEditing) {
                    BannerItem(color = Color(0xFF1E40AF), bgColor = Color(0xFFEFF6FF), icon = Icons.Outlined.VerifiedUser, title = "Your documents are securely encrypted.")
                } else {
                    BannerItem(color = Color(0xFFD97706), bgColor = Color(0xFFFFFBEB), icon = Icons.Outlined.Info, title = "Updating triggers a re-verification process.")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // --- GST SECTION ---
                SectionHeader("GST INFORMATION", if (!isEditing) "VERIFIED" else null)
                Spacer(modifier = Modifier.height(16.dp))

                Text("GST NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                if (isEditing) {
                    OutlinedTextField(value = gstin, onValueChange = { gstin = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
                } else {
                    ReadOnlyFieldWithLock(gstin)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(if (isEditing) "GST CERTIFICATE" else "", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                if (isEditing) Spacer(modifier = Modifier.height(8.dp))

                if (!isEditing) {
                    DocumentCard(fileName = existingGstFileName, onAction = { isEditing = true }, actionText = "Update")
                } else {
                    EditableDocumentCard(
                        fileName = if (newGstUri != null) getFileNameHelper(context, newGstUri!!) else existingGstFileName,
                        onReplace = { gstLauncher.launch("application/pdf") },
                        onDelete = { newGstUri = null }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- PAN SECTION ---
                SectionHeader("PAN INFORMATION", if (!isEditing) "VERIFIED" else null)
                Spacer(modifier = Modifier.height(16.dp))

                Text("PAN NUMBER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                if (isEditing) {
                    OutlinedTextField(value = panNumber, onValueChange = { panNumber = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White, focusedContainerColor = Color.White))
                } else {
                    ReadOnlyFieldWithLock(panNumber)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(if (isEditing) "PAN CARD COPY" else "", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                if (isEditing) Spacer(modifier = Modifier.height(8.dp))

                if (!isEditing) {
                    DocumentCard(fileName = existingPanFileName, onAction = { isEditing = true }, actionText = "Update")
                } else {
                    EditableDocumentCard(
                        fileName = if (newPanUri != null) getFileNameHelper(context, newPanUri!!) else existingPanFileName,
                        onReplace = { panLauncher.launch("image/*") },
                        onDelete = { newPanUri = null }
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun BannerItem(color: Color, bgColor: Color, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, onClose: (() -> Unit)? = null) {
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(title, fontSize = 11.sp, color = color, modifier = Modifier.weight(1f), lineHeight = 16.sp)
            if (onClose != null) {
                IconButton(onClick = onClose, modifier = Modifier.size(20.dp)) {
                    Icon(Icons.Default.Close, null, tint = color)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, chipText: String?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
        if (chipText != null) {
            Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(4.dp)) {
                Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF10B981), modifier = Modifier.size(10.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(chipText, fontSize = 9.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ReadOnlyFieldWithLock(value: String) {
    Surface(color = Color(0xFFF8FAFC), border = BorderStroke(1.dp, Color(0xFFF1F5F9)), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(value, fontWeight = FontWeight.ExtraBold, color = Color.Black, fontSize = 14.sp)
            Icon(Icons.Outlined.Lock, null, tint = Color.LightGray, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun DocumentCard(fileName: String, onAction: () -> Unit, actionText: String) {
    Surface(color = Color.White, border = BorderStroke(1.dp, Color(0xFFE2E8F0)), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.InsertDriveFile, null, tint = Color(0xFF1E40AF), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(fileName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black, maxLines = 1)
                Text("Secure File", color = Color.Gray, fontSize = 9.sp)
            }
            Surface(color = Color(0xFFEFF6FF), shape = RoundedCornerShape(6.dp), modifier = Modifier.clickable { onAction() }) {
                Text(actionText, color = Color(0xFF1E40AF), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}

@Composable
fun EditableDocumentCard(fileName: String, onReplace: () -> Unit, onDelete: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)).padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.InsertDriveFile, null, tint = Color(0xFF1E40AF), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(fileName, fontSize = 12.sp, color = Color.Black, modifier = Modifier.weight(1f), maxLines = 1)
            IconButton(onClick = onDelete, modifier = Modifier.size(20.dp)) { Icon(Icons.Outlined.Delete, null, tint = Color(0xFFEF4444)) }
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = onReplace, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFF1E40AF))) {
            Icon(Icons.Outlined.CloudUpload, null, tint = Color(0xFF1E40AF), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Replace File", color = Color(0xFF1E40AF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }
}

// --- FILE HELPERS ---
fun prepareFilePartHelper(context: Context, partName: String, fileUri: Uri?): MultipartBody.Part? {
    if (fileUri == null) return null
    val fileName = getFileNameHelper(context, fileUri)
    val tempFile = File(context.cacheDir, fileName)
    val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
    val outputStream = FileOutputStream(tempFile)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    val requestBody = tempFile.asRequestBody("application/octet-stream".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
}

fun getFileNameHelper(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { if (it.moveToFirst()) result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)) }
    }
    return result ?: uri.path?.substringAfterLast('/') ?: "unknown_file"
}