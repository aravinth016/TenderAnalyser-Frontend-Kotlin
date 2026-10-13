package com.simats.tenderanalyser.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import java.util.Locale

// --- FILE UTILITIES ---
fun prepareFilePart(context: Context, partName: String, fileUri: Uri?): MultipartBody.Part? {
    if (fileUri == null) return null
    val fileName = getFileName(context, fileUri)
    val tempFile = File(context.cacheDir, fileName)
    val inputStream = context.contentResolver.openInputStream(fileUri) ?: return null
    val outputStream = FileOutputStream(tempFile)
    inputStream.copyTo(outputStream)
    inputStream.close()
    outputStream.close()
    val requestBody = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
}

fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { if (it.moveToFirst()) result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME)) }
    }
    return result ?: uri.path?.substringAfterLast('/') ?: "unknown_file"
}

// --- MAIN UI FLOW ---
@Composable
fun BidFlowScreen(
    tenderId: String = "1",
    onNavigateToDashboard: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) }
    val darkBlue = Color(0xFF0A1931)

    // User ID Session
    val sharedPrefs = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)
    val currentUserId = sharedPrefs.getString("USER_ID", "1") ?: "1"

    // Local Files (New Selections)
    var gstPanUri by remember { mutableStateOf<Uri?>(null) }
    var affidavitUri by remember { mutableStateOf<Uri?>(null) }
    var emdUri by remember { mutableStateOf<Uri?>(null) }
    var pastWorkUri by remember { mutableStateOf<Uri?>(null) }
    var finSolvencyUri by remember { mutableStateOf<Uri?>(null) }
    var techUri by remember { mutableStateOf<Uri?>(null) }
    var finUri by remember { mutableStateOf<Uri?>(null) }

    // Server Files (Pre-populated for Edit Mode)
    var serverGst by remember { mutableStateOf<String?>(null) }
    var serverAff by remember { mutableStateOf<String?>(null) }
    var serverEmd by remember { mutableStateOf<String?>(null) }
    var serverPast by remember { mutableStateOf<String?>(null) }
    var serverSol by remember { mutableStateOf<String?>(null) }
    var serverTech by remember { mutableStateOf<String?>(null) }
    var serverFin by remember { mutableStateOf<String?>(null) }

    // Form States
    var companyName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var gstin by remember { mutableStateOf("") }
    var primaryContact by remember { mutableStateOf("") }
    var signatoryName by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var quotedPrice by remember { mutableStateOf("") }

    var generatedBidId by remember { mutableStateOf("Pending...") }
    var tenderTitle by remember { mutableStateOf("Loading Tender Details...") }
    var tenderEstimate by remember { mutableDoubleStateOf(0.0) }
    var isEditMode by remember { mutableStateOf(false) }

    LaunchedEffect(tenderId, currentUserId) {
        try {
            // 1. Fetch Tender Details
            val tenderResponse = ApiClient.apiService.getTenderById(tenderId)
            if (tenderResponse.success && tenderResponse.tender != null) {
                tenderTitle = tenderResponse.tender.projectTitle
                val cleanEst = tenderResponse.tender.estimatedValue.replace(Regex("[^0-9.]"), "")
                tenderEstimate = cleanEst.toDoubleOrNull() ?: 0.0
            }

            // 2. Load User Profile / Company info using multi-key fallbacks
            companyName = sharedPrefs.getString("COMPANY_NAME", null)
                ?: sharedPrefs.getString("companyName", null)
                        ?: sharedPrefs.getString("company", "") ?: ""

            address = sharedPrefs.getString("HQ_ADDRESS", null)
                ?: sharedPrefs.getString("hqAddress", null)
                        ?: sharedPrefs.getString("address", "") ?: ""

            primaryContact = sharedPrefs.getString("USER_EMAIL", null)
                ?: sharedPrefs.getString("businessEmail", null)
                        ?: sharedPrefs.getString("email", "") ?: ""

            signatoryName = sharedPrefs.getString("USER_NAME", null)
                ?: sharedPrefs.getString("fullName", null)
                        ?: sharedPrefs.getString("name", "") ?: ""

            designation = sharedPrefs.getString("JOB_TITLE", null)
                ?: sharedPrefs.getString("jobTitle", null)
                        ?: sharedPrefs.getString("designation", "") ?: ""

            gstin = sharedPrefs.getString("GSTIN", null)
                ?: sharedPrefs.getString("gstin", "") ?: ""

            // 3. Check Existing Bids for Edit Mode & Pre-population
            val bidsResponse = ApiClient.apiService.getMyBids(currentUserId)
            if (bidsResponse.success && bidsResponse.bids != null) {
                val existingBid = bidsResponse.bids.find { it.tenderId == tenderId }
                if (existingBid != null && existingBid.status == "Pending Review") {
                    isEditMode = true
                    val rawPrice = existingBid.quotedPrice.replace("₹", "").replace("Cr", "").replace(",", "").trim()
                    quotedPrice = rawPrice

                    serverGst = existingBid.gstDoc
                    serverAff = existingBid.affidavitDoc
                    serverEmd = existingBid.emdDoc
                    serverPast = existingBid.pastWorkDoc
                    serverSol = existingBid.finSolvencyDoc
                    serverTech = existingBid.techDoc
                    serverFin = existingBid.finDoc
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            tenderTitle = "Tender Details Unavailable"
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(darkBlue).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { if (currentStep > 1 && currentStep < 5) currentStep-- else onNavigateToDashboard() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text(
                        if (isEditMode) "EDITING BID: STEP $currentStep OF 4" else "STEP $currentStep OF 4",
                        color = if (isEditMode) Color(0xFF10B981) else Color.Gray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(48.dp))
                }
                Text(
                    tenderTitle,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                if (currentStep <= 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StepItem(1, currentStep, "Details")
                        StepLine(currentStep > 1)
                        StepItem(2, currentStep, "Documents")
                        StepLine(currentStep > 2)
                        StepItem(3, currentStep, "Price")
                        StepLine(currentStep > 3)
                        StepItem(4, currentStep, "Review")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF8FAFC))) {
            when (currentStep) {
                1 -> CompanyDetailsStep(
                    companyName, { companyName = it }, address, { address = it }, gstin, { gstin = it },
                    primaryContact, { primaryContact = it }, signatoryName, { signatoryName = it }, designation, { designation = it },
                    onNext = { currentStep = 2 }
                )

                2 -> DocumentUploadStep(
                    gstPanUri = gstPanUri, affidavitUri = affidavitUri, emdUri = emdUri, pastWorkUri = pastWorkUri, finSolvencyUri = finSolvencyUri, techUri = techUri, finUri = finUri,
                    serverGst = serverGst, serverAff = serverAff, serverEmd = serverEmd, serverPast = serverPast, serverSol = serverSol, serverTech = serverTech, serverFin = serverFin,
                    isEditMode = isEditMode, onNext = { currentStep = 3 }, onBack = { currentStep = 1 },
                    onGstPanUploaded = { gstPanUri = it }, onAffidavitUploaded = { affidavitUri = it },
                    onEmdUploaded = { emdUri = it }, onPastWorkUploaded = { pastWorkUri = it },
                    onFinSolvencyUploaded = { finSolvencyUri = it }, onTechUploaded = { techUri = it }, onFinUploaded = { finUri = it }
                )

                3 -> PriceQuoteStep(
                    price = quotedPrice,
                    estimatedCost = tenderEstimate,
                    onPriceChange = { quotedPrice = it },
                    onNext = { currentStep = 4 },
                    onBack = { currentStep = 2 }
                )

                4 -> FinalReviewStep(
                    tenderId = tenderId,
                    userId = currentUserId,
                    price = quotedPrice,
                    estimatedCost = tenderEstimate,
                    gstUri = gstPanUri, affUri = affidavitUri, emdUri = emdUri, pastUri = pastWorkUri, solUri = finSolvencyUri, techUri = techUri, finUri = finUri,
                    serverGst = serverGst, serverAff = serverAff, serverEmd = serverEmd, serverPast = serverPast, serverSol = serverSol, serverTech = serverTech, serverFin = serverFin,
                    isEditMode = isEditMode,
                    onSuccess = { newId ->
                        generatedBidId = newId
                        currentStep = 5
                    },
                    onBack = { currentStep = 3 }, onEditPrice = { currentStep = 3 }
                )

                5 -> SuccessStep(bidId = generatedBidId, isEditMode = isEditMode, onNavigateToDashboard = onNavigateToDashboard)
            }
        }
    }
}

// --- STEPPER HELPERS ---
@Composable
fun StepItem(step: Int, currentStep: Int, label: String) {
    val isActive = currentStep == step
    val isDone = currentStep > step
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(12.dp).background(if (isDone) Color(0xFF00E676) else if (isActive) Color.White else Color.DarkGray, CircleShape))
        Text(label, color = if (isActive) Color.White else Color.Gray, fontSize = 8.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun StepLine(isDone: Boolean) {
    Box(modifier = Modifier.width(30.dp).height(2.dp).background(if (isDone) Color(0xFF00E676) else Color.DarkGray).padding(bottom = 12.dp))
}

// --- STEP 1: COMPANY DETAILS ---
@Composable
fun CompanyDetailsStep(
    companyName: String, onCompanyNameChange: (String) -> Unit, address: String, onAddressChange: (String) -> Unit,
    gstin: String, onGstinChange: (String) -> Unit, primaryContact: String, onContactChange: (String) -> Unit,
    signatoryName: String, onSignatoryChange: (String) -> Unit, designation: String, onDesignationChange: (String) -> Unit,
    onNext: () -> Unit
) {
    val isFormComplete = companyName.isNotBlank() && address.isNotBlank() && gstin.isNotBlank() && primaryContact.isNotBlank() && signatoryName.isNotBlank() && designation.isNotBlank()

    Column(modifier = Modifier.padding(16.dp).fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Company Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF0A1931))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = Color(0xFF2962FF), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("ORGANIZATION PROFILE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1931))
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomTextField("COMPANY NAME *", companyName, onCompanyNameChange, placeholder = "Enter company name")
                CustomTextField("REGISTERED ADDRESS *", address, onAddressChange, placeholder = "Enter full address")
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CustomTextField("GSTIN *", gstin, onGstinChange, placeholder = "GST Number", modifier = Modifier.weight(1f))
                    CustomTextField("PRIMARY CONTACT *", primaryContact, onContactChange, placeholder = "Contact No.", modifier = Modifier.weight(1f))
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Icon(Icons.Default.Assignment, contentDescription = null, tint = Color(0xFF2962FF), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("BID SPECIFIC DETAILS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1931))
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CustomTextField("AUTHORIZED SIGNATORY NAME *", signatoryName, onSignatoryChange, placeholder = "Enter full name")
                CustomTextField("DESIGNATION *", designation, onDesignationChange, placeholder = "e.g. Director, Project Manager")
            }
        }

        Button(
            onClick = onNext, enabled = isFormComplete, modifier = Modifier.fillMaxWidth().height(54.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF), disabledContainerColor = Color.LightGray), shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isFormComplete) "Next" else "Fill all fields to continue", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CustomTextField(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String = "", modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, placeholder = { Text(placeholder, fontSize = 13.sp, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF2962FF), unfocusedBorderColor = Color(0xFFE2E8F0),
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = Color.DarkGray)
        )
    }
}

// --- STEP 2: DOCUMENTS UPLOAD ---
enum class DocType { GST, AFFIDAVIT, EMD, PASTWORK, SOLVENCY, TECHNICAL, FINANCIAL }

@Composable
fun DocumentUploadStep(
    gstPanUri: Uri?, affidavitUri: Uri?, emdUri: Uri?, pastWorkUri: Uri?, finSolvencyUri: Uri?, techUri: Uri?, finUri: Uri?,
    serverGst: String?, serverAff: String?, serverEmd: String?, serverPast: String?, serverSol: String?, serverTech: String?, serverFin: String?,
    isEditMode: Boolean,
    onNext: () -> Unit, onBack: () -> Unit,
    onGstPanUploaded: (Uri) -> Unit, onAffidavitUploaded: (Uri) -> Unit, onEmdUploaded: (Uri) -> Unit, onPastWorkUploaded: (Uri) -> Unit, onFinSolvencyUploaded: (Uri) -> Unit, onTechUploaded: (Uri) -> Unit, onFinUploaded: (Uri) -> Unit
) {
    val context = LocalContext.current
    var currentPicking by remember { mutableStateOf<DocType?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            when (currentPicking) {
                DocType.GST -> onGstPanUploaded(it); DocType.AFFIDAVIT -> onAffidavitUploaded(it); DocType.EMD -> onEmdUploaded(it);
                DocType.PASTWORK -> onPastWorkUploaded(it); DocType.SOLVENCY -> onFinSolvencyUploaded(it); DocType.TECHNICAL -> onTechUploaded(it); DocType.FINANCIAL -> onFinUploaded(it)
                else -> {}
            }
        }; currentPicking = null
    }

    val hasGst = gstPanUri != null || !serverGst.isNullOrBlank()
    val hasAff = affidavitUri != null || !serverAff.isNullOrBlank()
    val hasEmd = emdUri != null || !serverEmd.isNullOrBlank()
    val hasPast = pastWorkUri != null || !serverPast.isNullOrBlank()
    val hasSol = finSolvencyUri != null || !serverSol.isNullOrBlank()
    val hasTech = techUri != null || !serverTech.isNullOrBlank()
    val hasFin = finUri != null || !serverFin.isNullOrBlank()

    val allDocsAttached = hasGst && hasAff && hasEmd && hasPast && hasSol && hasTech && hasFin

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {

        Text("Document Submission", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF0A1931))
        Text("Please upload all 7 required documents specifically for this tender bid.", fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        if (isEditMode) {
            Surface(color = Color(0xFFECFDF5), shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, Color(0xFFA7F3D0))) {
                Text("Edit Mode: Your previously submitted files are shown below. Click Replace to upload a new version.", fontSize = 11.sp, color = Color(0xFF065F46), modifier = Modifier.padding(12.dp), lineHeight = 16.sp)
            }
        }

        Column {
            SectionTitleBlue("LEGAL & SAFETY (MANDATORY)")
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    UploadDocumentRow("GST & PAN Certificates *", "Proof of identity and tax", gstPanUri, serverGst, context) { currentPicking = DocType.GST; launcher.launch("application/pdf") }
                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))
                    UploadDocumentRow("Non-Blacklisting Affidavit *", "Sworn statement", affidavitUri, serverAff, context) { currentPicking = DocType.AFFIDAVIT; launcher.launch("application/pdf") }
                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))
                    UploadDocumentRow("EMD Payment Receipt *", "Proof of security deposit", emdUri, serverEmd, context) { currentPicking = DocType.EMD; launcher.launch("application/pdf") }
                }
            }
        }

        Column {
            SectionTitleBlue("TECHNICAL & EXPERIENCE (EVALUATION)")
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    UploadDocumentRow("Past Completion Certificates *", "Proof of similar projects", pastWorkUri, serverPast, context) { currentPicking = DocType.PASTWORK; launcher.launch("application/pdf") }
                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))
                    UploadDocumentRow("Financial Solvency / IT Returns *", "Proof of cash flow", finSolvencyUri, serverSol, context) { currentPicking = DocType.SOLVENCY; launcher.launch("application/pdf") }
                    HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 8.dp))
                    UploadDocumentRow("Tech Proposal & Manpower *", "Execution plan for this project", techUri, serverTech, context) { currentPicking = DocType.TECHNICAL; launcher.launch("application/pdf") }
                }
            }
        }

        Column {
            SectionTitleBlue("FINANCIAL BID")
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) {
                Column(modifier = Modifier.padding(16.dp)) {
                    UploadDocumentRow("Priced BOQ (Financial Bid) *", "Your final sealed quotation", finUri, serverFin, context) { currentPicking = DocType.FINANCIAL; launcher.launch("application/pdf") }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) { Text("Back", color = Color.DarkGray, fontWeight = FontWeight.Bold) }
            Button(
                onClick = onNext, enabled = allDocsAttached, modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF), disabledContainerColor = Color.LightGray), shape = RoundedCornerShape(12.dp)
            ) { Text("Next", fontWeight = FontWeight.Bold) }
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SectionTitleBlue(title: String) { Text(title, color = Color(0xFF2962FF), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp, modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)) }

@Composable
fun UploadDocumentRow(title: String, subtitle: String, fileUri: Uri?, serverFileName: String?, context: Context, onBrowse: () -> Unit) {
    val isUploadedLocally = fileUri != null
    val isUploadedOnServer = !serverFileName.isNullOrBlank()
    val isUploaded = isUploadedLocally || isUploadedOnServer

    val fileNameToDisplay = if (isUploadedLocally) {
        getFileName(context, fileUri!!)
    } else if (isUploadedOnServer) {
        serverFileName!!
    } else ""

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(title, color = Color(0xFF0A1931), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, fontSize = 11.sp)
            }
            Button(
                onClick = onBrowse,
                colors = ButtonDefaults.buttonColors(containerColor = if (isUploaded) Color(0xFFECFDF5) else Color(0xFF2962FF)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                Icon(if (isUploaded) Icons.Default.CheckCircle else Icons.Default.CloudUpload, null, tint = if (isUploaded) Color(0xFF00C853) else Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isUploaded) "Uploaded" else "Upload", color = if (isUploaded) Color(0xFF00C853) else Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        if (isUploaded) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp).background(Color(0xFFF8FAFC), RoundedCornerShape(4.dp)).padding(horizontal = 8.dp, vertical = 6.dp).fillMaxWidth()) {
                Icon(Icons.Default.Description, null, tint = Color(0xFFE53935), modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(fileNameToDisplay, color = Color.DarkGray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Text("Replace", fontSize = 11.sp, color = Color(0xFF2962FF), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBrowse() }.padding(start = 8.dp))
            }
        }
    }
}

// --- STEP 3: PRICE QUOTE ---
@Composable
fun PriceQuoteStep(
    price: String,
    estimatedCost: Double,
    onPriceChange: (String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val currentPrice = price.replace(",", "").toDoubleOrNull() ?: 0.0
    val diffPercentage = if (currentPrice > 0 && estimatedCost > 0) ((estimatedCost - currentPrice) / estimatedCost) * 100 else 0.0
    val isBelow = diffPercentage >= 0
    val isPriceValid = price.isNotBlank() && currentPrice > 0.0

    val formattedEstCost = if (estimatedCost >= 100000) {
        String.format(Locale.US, "%.2f Cr", estimatedCost / 10000000)
    } else {
        String.format(Locale.US, "%,.2f", estimatedCost)
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Bid Price Quote", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1931))
        Text("Set your competitive price for this tender bid.", fontSize = 13.sp, color = Color.Gray)

        Text("ENTER YOUR QUOTED PRICE (INR) *", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray, modifier = Modifier.fillMaxWidth())

        OutlinedTextField(
            value = price, onValueChange = onPriceChange, modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(fontSize = 32.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            prefix = { Text("₹ ", fontSize = 24.sp, color = Color.Gray) }, singleLine = true,
            shape = RoundedCornerShape(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF2962FF))
        )

        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), modifier = Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("TENDER ESTIMATED COST", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("₹ $formattedEstCost", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("DIFFERENCE", fontSize = 10.sp, color = Color.Gray)
                    val diffColor = if (isBelow) Color(0xFF00C853) else Color(0xFFD50000)
                    val arrow = if (isBelow) "↓" else "↑"
                    Text(String.format(Locale.US, "$arrow %.2f%%", Math.abs(diffPercentage)), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = diffColor)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(1f).height(54.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0))) { Text("Back", color = Color.DarkGray, fontWeight = FontWeight.Bold) }
            Button(
                onClick = onNext, enabled = isPriceValid, modifier = Modifier.weight(1f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF), disabledContainerColor = Color.LightGray), shape = RoundedCornerShape(12.dp)
            ) { Text("Next", fontWeight = FontWeight.Bold) }
        }
    }
}

// --- STEP 4: FINAL REVIEW ---
@Composable
fun FinalReviewStep(
    tenderId: String,
    userId: String,
    price: String,
    estimatedCost: Double,
    gstUri: Uri?, affUri: Uri?, emdUri: Uri?, pastUri: Uri?, solUri: Uri?, techUri: Uri?, finUri: Uri?,
    serverGst: String?, serverAff: String?, serverEmd: String?, serverPast: String?, serverSol: String?, serverTech: String?, serverFin: String?,
    isEditMode: Boolean,
    onSuccess: (String) -> Unit, onBack: () -> Unit, onEditPrice: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isUploading by remember { mutableStateOf(false) }

    val currentPrice = price.replace(",", "").toDoubleOrNull() ?: 0.0
    val diffPercentage = if (currentPrice > 0 && estimatedCost > 0) ((estimatedCost - currentPrice) / estimatedCost) * 100 else 0.0
    val isBelow = diffPercentage >= 0

    val formattedQuote = if (currentPrice >= 10000000) {
        String.format(Locale.US, "%.2f Cr", currentPrice / 10000000)
    } else if (currentPrice >= 100000) {
        String.format(Locale.US, "%.2f L", currentPrice / 100000)
    } else {
        String.format(Locale.US, "%,.2f", currentPrice)
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Final Review", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color(0xFF0A1931))

        Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("FINANCIAL QUOTE", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("Edit", fontSize = 12.sp, color = Color(0xFF2962FF), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onEditPrice() })
                }
                Spacer(modifier = Modifier.height(8.dp))

                Text("₹ $formattedQuote", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1931))
                Spacer(modifier = Modifier.height(8.dp))

                val arrow = if (isBelow) "↘" else "↗"
                val textType = if (isBelow) "below" else "above"
                Text(String.format(Locale.US, "$arrow %.2f%% $textType estimated cost", Math.abs(diffPercentage)), color = if (isBelow) Color(0xFF00C853) else Color(0xFFD50000), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Card(colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(2.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("DOCUMENTS ATTACHED", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))

                val uploadedDocs = listOfNotNull(
                    gstUri?.let { getFileName(context, it) } ?: serverGst,
                    affUri?.let { getFileName(context, it) } ?: serverAff,
                    emdUri?.let { getFileName(context, it) } ?: serverEmd,
                    pastUri?.let { getFileName(context, it) } ?: serverPast,
                    solUri?.let { getFileName(context, it) } ?: serverSol,
                    techUri?.let { getFileName(context, it) } ?: serverTech,
                    finUri?.let { getFileName(context, it) } ?: serverFin
                )

                if (uploadedDocs.isEmpty()) {
                    Text("No documents attached yet.", fontSize = 13.sp, color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                } else {
                    uploadedDocs.forEach { doc ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF00C853), modifier = Modifier.size(16.dp))
                            Text(doc ?: "", fontSize = 12.sp, color = Color.DarkGray, maxLines = 1, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onBack, modifier = Modifier.weight(0.7f).height(54.dp), shape = RoundedCornerShape(12.dp), border = BorderStroke(1.dp, Color(0xFFE2E8F0)), enabled = !isUploading) {
                Text("Back", color = Color.DarkGray, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    isUploading = true
                    coroutineScope.launch {
                        try {
                            val textType = "text/plain".toMediaTypeOrNull()

                            val safePrice = price.replace(Regex("[^0-9.]"), "")
                            val finalTenderId = if (tenderId.isBlank() || tenderId == "Draft") "1" else tenderId

                            val response = ApiClient.apiService.submitBid(
                                tenderId = finalTenderId.toRequestBody(textType),
                                bidderId = userId.toRequestBody(textType),
                                bidAmount = safePrice.toRequestBody(textType),
                                technicalDoc = prepareFilePart(context, "technical_doc", techUri),
                                financialDoc = prepareFilePart(context, "financial_doc", finUri),
                                emdReceipt = prepareFilePart(context, "emd_receipt", emdUri),
                                gstPanDoc = prepareFilePart(context, "gst_pan_doc", gstUri),
                                affidavitDoc = prepareFilePart(context, "affidavit_doc", affUri),
                                pastWorkDoc = prepareFilePart(context, "past_work_doc", pastUri),
                                finSolvencyDoc = prepareFilePart(context, "fin_solvency_doc", solUri)
                            )

                            isUploading = false
                            if (response.success) {
                                onSuccess(response.referenceId ?: "TDR-SUCCESS")
                            } else {
                                Toast.makeText(context, response.message ?: "Server refused upload", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: retrofit2.HttpException) {
                            e.printStackTrace()
                            isUploading = false
                            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown"
                            Toast.makeText(context, "Error 400: $errorBody", Toast.LENGTH_LONG).show()
                        } catch (e: Exception) {
                            e.printStackTrace()
                            isUploading = false
                            Toast.makeText(context, "Upload Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.weight(1.3f).height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isUploading
            ) {
                if (isUploading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                else Text(if (isEditMode) "Confirm Update" else "Confirm & Submit", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- STEP 5: SUCCESS SCREEN ---
@Composable
fun SuccessStep(bidId: String, isEditMode: Boolean, onNavigateToDashboard: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).background(Color(0xFFF8FAFC)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF00C853), modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(24.dp))

        Text(if (isEditMode) "Bid Updated Successfully" else "Submitted Successfully", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0A1931), textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(16.dp))

        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)), shape = RoundedCornerShape(8.dp)) {
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TENDER REFERENCE ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Text(bidId, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onNavigateToDashboard,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2962FF)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Return to Dashboard", fontWeight = FontWeight.Bold)
        }
    }
}