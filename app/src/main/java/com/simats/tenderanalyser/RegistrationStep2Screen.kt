package com.simats.tenderanalyser

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.CloudUpload
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationStep2Screen(
    viewModel: RegistrationViewModel, // NEW: Accepts the ViewModel
    onBackClick: () -> Unit,
    onRegistrationSuccess: () -> Unit // NEW: Renamed for clarity
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    // NEW: Listen to the ViewModel to see if Python says "Success" or "Error"
    val registrationState by viewModel.registrationState.collectAsState()

    LaunchedEffect(registrationState) {
        registrationState?.let { result ->
            isLoading = false
            if (result.isSuccess) {
                viewModel.resetState()
                onRegistrationSuccess() // Go to success screen!
            } else {
                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Upload Failed", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    var gstUri by remember { mutableStateOf<Uri?>(null) }
    var incUri by remember { mutableStateOf<Uri?>(null) }
    var panUri by remember { mutableStateOf<Uri?>(null) }
    var isCertified by remember { mutableStateOf(false) }

    val gstLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> gstUri = uri }
    val incLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> incUri = uri }
    val panLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> panUri = uri }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                TopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth().padding(end = 48.dp)) {
                            Text("STEP 2 OF 2", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("KYC Verification", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF0F172A)) }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp), color = Color(0xFF1B49B5), thickness = 4.dp)
            }
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Upload Documents", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(8.dp))
            Text("Please upload high-quality PDF copies of the following mandatory documents to verify your business profile.", fontSize = 14.sp, color = Color(0xFF64748B), lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(24.dp))

            UploadDocumentCard(title = "GST Certificate", isUploaded = gstUri != null) { gstLauncher.launch("application/pdf") }
            Spacer(modifier = Modifier.height(16.dp))
            UploadDocumentCard(title = "Certificate of Incorporation", isUploaded = incUri != null) { incLauncher.launch("application/pdf") }
            Spacer(modifier = Modifier.height(16.dp))
            UploadDocumentCard(title = "PAN Card Copy", isUploaded = panUri != null) { panLauncher.launch("application/pdf") }
            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.Top) {
                Checkbox(checked = isCertified, onCheckedChange = { isCertified = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFF1B49B5)))
                Text("I certify that all provided company documents are authentic and valid.", fontSize = 13.sp, color = Color(0xFF0F172A), modifier = Modifier.padding(top = 12.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // NEW: Upload button logic
            Button(
                onClick = {
                    if (gstUri != null && incUri != null && panUri != null && isCertified) {
                        isLoading = true
                        // FIRE THE UPLOAD TO PYTHON!
                        viewModel.registerUser(context, gstUri!!, incUri!!, panUri!!)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if (isCertified && !isLoading) Color(0xFF1B49B5) else Color.LightGray),
                enabled = isCertified && !isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Uploading...", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    } else {
                        Text("Complete Registration", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Maximum file size per document is 5MB. Only PDF format is accepted.", fontSize = 11.sp, color = Color(0xFF64748B), textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun UploadDocumentCard(title: String, isUploaded: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFFF8FAFC))
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.CloudUpload, contentDescription = null, tint = Color(0xFF1B49B5), modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text("MANDATORY", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF64748B), letterSpacing = 0.5.sp)
            }
            OutlinedButton(onClick = onClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, if (isUploaded) Color(0xFF10B981) else Color(0xFFE2E8F0))) {
                Text(text = if (isUploaded) "Uploaded" else "Upload PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isUploaded) Color(0xFF10B981) else Color(0xFF1B49B5))
            }
        }
    }
}