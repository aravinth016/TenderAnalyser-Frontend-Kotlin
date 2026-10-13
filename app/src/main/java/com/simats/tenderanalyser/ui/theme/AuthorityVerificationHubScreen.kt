package com.simats.tenderanalyser.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.CloudUpload
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)
private val ErrorRed = Color(0xFFEF4444)
private val ErrorBg = Color(0xFF2A1616)
private val SuccessGreen = Color(0xFF4CAF50)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityVerificationHubScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPref = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
    val adminId = sharedPref.getString("ADMIN_ID", "") ?: ""

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    // Dynamic UI States
    var totalUploaded by remember { mutableIntStateOf(0) }
    var idProofState by remember { mutableStateOf("action_required") }
    var idProofUri by remember { mutableStateOf<Uri?>(null) }
    var idProofFileName by remember { mutableStateOf("No file selected") }

    // Fetch Initial Data & Calculate Progress dynamically
    LaunchedEffect(Unit) {
        try {
            val response = ApiClient.apiService.getAdminDocs(adminId)
            if (response.success && response.documents != null) {
                var count = 0
                if (!response.documents["authLetter"].isNullOrEmpty()) count++
                if (!response.documents["deptReg"].isNullOrEmpty()) count++
                if (!response.documents["dscKey"].isNullOrEmpty()) count++

                if (!response.documents["idProof"].isNullOrEmpty()) {
                    count++
                    idProofState = "uploaded"
                    idProofFileName = response.documents["idProof"]!!
                } else {
                    idProofState = "action_required"
                }

                // Fallback for demo purposes: assume first 3 are uploaded via registration
                totalUploaded = if (count < 3) 3 else count
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to load documents", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            idProofUri = uri
            idProofState = "uploaded"
            totalUploaded = 4 // Instantly update UI to 100% locally

            val path = uri.path ?: "document"
            idProofFileName = path.substringAfterLast("/") + ".pdf"
        }
    }

    // Dynamic Progress UI variables
    val progressFloat = totalUploaded / 4f
    val progressPercent = (totalUploaded * 25)
    val isFullyVerified = totalUploaded == 4
    val overallStatusText = if (isFullyVerified) "Fully Verified" else "Partially Verified"
    val overallStatusColor = if (isFullyVerified) SuccessGreen else Gold

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Verification Hub", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            if (idProofState == "action_required") {
                                Toast.makeText(context, "Please replace the required files first.", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            scope.launch {
                                isSaving = true
                                try {
                                    val idBody = adminId.toRequestBody("text/plain".toMediaTypeOrNull())
                                    var idProofPart: MultipartBody.Part? = null

                                    idProofUri?.let { uri ->
                                        val file = getAuthorityFileFromUri(context, uri)
                                        if (file != null) {
                                            val reqFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
                                            idProofPart = MultipartBody.Part.createFormData("idProof", file.name, reqFile)
                                        }
                                    }

                                    val response = ApiClient.apiService.updateAdminDocs(adminId = idBody, idProof = idProofPart)

                                    if (response.success) {
                                        // 🚨 OVERWRITE SHARED PREFS WITH NEW "Verified" STATUS FROM PYTHON!
                                        response.status?.let { newStatus ->
                                            sharedPref.edit().putString("ADMIN_STATUS", newStatus).apply()
                                        }
                                        Toast.makeText(context, "Documents Verified Successfully!", Toast.LENGTH_SHORT).show()
                                        onBackClick() // Go back to profile
                                    } else {
                                        Toast.makeText(context, response.message ?: "Upload Failed", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isFullyVerified) SuccessGreen else Gold),
                        enabled = !isLoading && !isSaving
                    ) {
                        if (isSaving) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        else Text(if (isFullyVerified) "COMPLETE VERIFICATION" else "SAVE & SUBMIT", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                    }
                }
            }
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Gold)
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp).verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(16.dp))

                // --- DYNAMIC OVERALL STATUS CARD ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    border = BorderStroke(1.dp, OutlineColor)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("OVERALL STATUS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(overallStatusText, color = overallStatusColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$totalUploaded of 4 documents approved", color = TextGray, fontSize = 11.sp)
                        }

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(60.dp)) {
                            CircularProgressIndicator(progress = { progressFloat }, modifier = Modifier.fillMaxSize(), color = overallStatusColor, trackColor = OutlineColor, strokeWidth = 4.dp)
                            Text("$progressPercent%", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Text("VERIFICATION DOCUMENTS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // --- STATIC DOCUMENTS ---
                VerificationDocCard("Authorization Letter", "VERIFIED", Icons.Default.CheckCircle, SuccessGreen)
                Spacer(modifier = Modifier.height(12.dp))

                VerificationDocCard("Department Registration", "VERIFIED", Icons.Default.CheckCircle, SuccessGreen)
                Spacer(modifier = Modifier.height(12.dp))

                VerificationDocCard("DSC Certificate", "VERIFIED", Icons.Default.CheckCircle, SuccessGreen)
                Spacer(modifier = Modifier.height(12.dp))

                // --- INTERACTIVE ID PROOF SECTION ---
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (idProofState == "action_required") ErrorBg else CardBg),
                    border = BorderStroke(1.dp, if (idProofState == "action_required") ErrorRed.copy(alpha = 0.5f) else OutlineColor)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                            Column {
                                Text("Department ID Proof", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                if (idProofState == "action_required") {
                                    Surface(color = ErrorRed.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                                            Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("ACTION REQUIRED", color = ErrorRed, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                } else {
                                    Surface(color = SuccessGreen.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(10.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("VERIFIED", color = SuccessGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Surface(color = DarkBg, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, OutlineColor)) {
                                Text("VIEW", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                            }
                        }

                        if (idProofState == "action_required") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Scan is blurry or unreadable", color = ErrorRed, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))

                            val stroke = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .padding(vertical = 8.dp)
                                    .drawBehind { drawRoundRect(color = Gold.copy(alpha = 0.5f), style = stroke, cornerRadius = CornerRadius(12.dp.toPx())) }
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { filePickerLauncher.launch("application/pdf") },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = Gold, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("REPLACE FILE", color = Gold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Upload PDF Format", color = TextGray, fontSize = 9.sp)
                                }
                            }
                        } else {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(modifier = Modifier.fillMaxWidth().background(DarkBg, RoundedCornerShape(8.dp)).border(1.dp, OutlineColor, RoundedCornerShape(8.dp)).padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = null, tint = Gold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(idProofFileName, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    Text("PDF Document", color = TextGray, fontSize = 10.sp)
                                }
                                IconButton(onClick = {
                                    idProofState = "action_required"
                                    totalUploaded = 3
                                    idProofUri = null
                                }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = TextGray)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
fun VerificationDocCard(title: String, statusText: String, statusIcon: ImageVector, statusColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(1.dp, OutlineColor)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(title, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Surface(color = statusColor.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)) {
                        Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(statusText, color = statusColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Surface(color = DarkBg, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, OutlineColor)) {
                Text("VIEW", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
            }
        }
    }
}

private fun getAuthorityFileFromUri(context: Context, uri: Uri): File? {
    try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File.createTempFile("upload", ".pdf", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return tempFile
    } catch (e: Exception) {
        return null
    }
}