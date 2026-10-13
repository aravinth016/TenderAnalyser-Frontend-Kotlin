package com.simats.tenderanalyser.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.AuthorityCreateTenderViewModel
import java.util.Calendar

private val DarkBg = Color(0xFF121212)
private val FieldBg = Color(0xFF1E1E1E)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

// ==========================================
// STEP 1: BASIC INFORMATION
// ==========================================
@Composable
fun CreateTenderStep1Screen(viewModel: AuthorityCreateTenderViewModel, onNextClick: () -> Unit, onBackClick: () -> Unit) {
    var title by remember { mutableStateOf(viewModel.projectTitle) }
    var nit by remember { mutableStateOf(viewModel.nitNumber) }
    var desc by remember { mutableStateOf(viewModel.projectDescription) }

    TenderWizardScaffold(step = 1, title = "New Tender (1/3)", subtitle = "STEP: BASIC INFORMATION", onBackClick = onBackClick) {
        TenderInputField("PROJECT TITLE", "e.g. Smart City Traffic Management", title) { title = it }

        TenderInputField("NIT REFERENCE NUMBER", "E.g. NIT/2023/IT/042", nit) {
            nit = it.uppercase().replace(" ", "")
        }

        Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
            Text("PROJECT DESCRIPTION", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = desc, onValueChange = { desc = it },
                placeholder = { Text("Provide a detailed overview of the project scope...", color = Color.DarkGray) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(8.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold, unfocusedBorderColor = OutlineColor, unfocusedContainerColor = FieldBg, focusedContainerColor = FieldBg)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        WizardNextButton("Continue to Next Step") {
            viewModel.projectTitle = title
            viewModel.nitNumber = nit
            viewModel.projectDescription = desc
            onNextClick()
        }
    }
}

// ==========================================
// STEP 2: PARAMETERS & VALUE
// ==========================================
@Composable
fun CreateTenderStep2Screen(viewModel: AuthorityCreateTenderViewModel, onNextClick: () -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current
    var value by remember { mutableStateOf(viewModel.estimatedValue) }
    var emd by remember { mutableStateOf(viewModel.emdAmount) }
    var location by remember { mutableStateOf(viewModel.location) }
    var deadline by remember { mutableStateOf(viewModel.deadline) }

    var sector by remember { mutableStateOf(viewModel.sector) }
    var sectorExpanded by remember { mutableStateOf(false) }
    val sectorOptions = listOf("Public Works", "Healthcare", "Energy", "IT & Telecom", "Logistics")

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day -> deadline = String.format("%02d/%02d/%04d", day, month + 1, year) },
        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
    )

    TenderWizardScaffold(step = 2, title = "New Tender (2/3)", subtitle = "PARAMETERS & VALUE", onBackClick = onBackClick) {
        SectionHeader("FINANCIAL DETAILS")

        TenderInputField("Estimated Value (INR Cr)", "₹ 0.00", value, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) { value = it }
        TenderInputField("Earnest Money Deposit (EMD)", "₹ Enter EMD amount", emd, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) { emd = it }

        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader("PROJECT CONTEXT")

        Box(modifier = Modifier.fillMaxWidth()) {
            TenderInputField("Sector/Category", "Select Sector", sector, isDropdown = true) { }
            Box(modifier = Modifier.matchParentSize().clickable { sectorExpanded = true })
            DropdownMenu(expanded = sectorExpanded, onDismissRequest = { sectorExpanded = false }, modifier = Modifier.background(FieldBg)) {
                sectorOptions.forEach { option ->
                    DropdownMenuItem(text = { Text(option, color = Color.White) }, onClick = { sector = option; sectorExpanded = false })
                }
            }
        }

        TenderInputField("Project Location", "City, State", location, icon = Icons.Default.LocationOn) { location = it }

        Spacer(modifier = Modifier.height(8.dp))
        SectionHeader("KEY DEADLINES")

        Box(modifier = Modifier.fillMaxWidth()) {
            TenderInputField("Bid Submission Deadline", "DD/MM/YYYY", deadline, isDropdown = true, icon = Icons.Default.CalendarToday) { }
            Box(modifier = Modifier.matchParentSize().clickable { datePickerDialog.show() })
        }

        Spacer(modifier = Modifier.weight(1f))
        WizardNextButton("Next Step") {
            viewModel.estimatedValue = value
            viewModel.emdAmount = emd
            viewModel.sector = sector
            viewModel.location = location
            viewModel.deadline = deadline
            onNextClick()
        }
    }
}

// ==========================================
// STEP 3: UPLOAD & SUBMIT (NEW PROFESSIONAL 7 CHECKS)
// ==========================================
@Composable
fun CreateTenderStep3Screen(viewModel: AuthorityCreateTenderViewModel, onPublishClick: () -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current

    // 🚨 7 Professional Checks
    var reqGstPan by remember { mutableStateOf(viewModel.reqGstPan) }
    var reqAffidavit by remember { mutableStateOf(viewModel.reqAffidavit) }
    var reqEmdReceipt by remember { mutableStateOf(viewModel.reqEmdReceipt) }
    var reqPastWork by remember { mutableStateOf(viewModel.reqPastWork) }
    var reqFinSolvency by remember { mutableStateOf(viewModel.reqFinSolvency) }
    var reqTechProposal by remember { mutableStateOf(viewModel.reqTechProposal) }
    // BoQ is always true

    val docLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        viewModel.tenderDocumentUris = viewModel.tenderDocumentUris + uris
    }

    val publishState by viewModel.publishState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Listen for Navigation Success
    LaunchedEffect(publishState) {
        if (publishState) {
            viewModel.resetState()
            onPublishClick()
        }
    }

    // Listen for Errors from the Server
    LaunchedEffect(Unit) {
        viewModel.errorFlow.collect { errorMessage ->
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        }
    }

    TenderWizardScaffold(step = 3, title = "New Tender (3/3)", subtitle = "UPLOAD & CONFIGURE", onBackClick = onBackClick) {
        Text("Upload Tender Documents", color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = { docLauncher.launch("application/pdf") },
            colors = ButtonDefaults.buttonColors(containerColor = FieldBg),
            modifier = Modifier.fillMaxWidth().height(50.dp).border(1.dp, Gold, RoundedCornerShape(8.dp)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.UploadFile, contentDescription = null, tint = Gold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("+ Select Documents (BoQ, Specs, etc.)", color = Color.White)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Render the list of selected files
        viewModel.tenderDocumentUris.forEachIndexed { index, uri ->
            val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "Document_${index + 1}.pdf"
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                colors = CardDefaults.cardColors(containerColor = FieldBg),
                border = borderStroke()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(fileName, color = Color.White, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    IconButton(
                        onClick = {
                            val updatedList = viewModel.tenderDocumentUris.toMutableList()
                            updatedList.removeAt(index)
                            viewModel.tenderDocumentUris = updatedList
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Mandatory Requirements Checklist", color = Color.White, fontWeight = FontWeight.Bold)
        Text("Select the compliance documents bidders MUST submit.", color = TextGray, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))

        // --- NEW PROFESSIONAL CHECKLIST CATEGORIES ---
        SectionHeader("LEGAL & SAFETY (MANDATORY)")
        RequirementCheckbox("GST & PAN Certificates", "Proof of identity and tax registration.", reqGstPan) { reqGstPan = it }
        RequirementCheckbox("Non-Blacklisting Affidavit", "Legal sworn statement of clean history.", reqAffidavit) { reqAffidavit = it }
        RequirementCheckbox("EMD Payment Receipt", "Proof of Earnest Money Deposit payment.", reqEmdReceipt) { reqEmdReceipt = it }

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader("TECHNICAL & EXPERIENCE (EVALUATION)")
        RequirementCheckbox("Past Completion Certificates", "Proof of similar finished projects.", reqPastWork) { reqPastWork = it }
        RequirementCheckbox("Financial Solvency / IT Returns", "Proof of cash flow to sustain the project.", reqFinSolvency) { reqFinSolvency = it }
        RequirementCheckbox("Tech Proposal & Manpower", "Execution plan and list of engineers/machinery.", reqTechProposal) { reqTechProposal = it }

        Spacer(modifier = Modifier.height(16.dp))
        SectionHeader("FINANCIAL BID")
        RequirementCheckbox("Priced BOQ", "The locked financial quotation.", true, enabled = false) { }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (!isLoading) {
                    viewModel.reqGstPan = reqGstPan
                    viewModel.reqAffidavit = reqAffidavit
                    viewModel.reqEmdReceipt = reqEmdReceipt
                    viewModel.reqPastWork = reqPastWork
                    viewModel.reqFinSolvency = reqFinSolvency
                    viewModel.reqTechProposal = reqTechProposal
                    viewModel.reqBoq = true
                    viewModel.publishTender(context)
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = if (isLoading) Color.DarkGray else Gold)
        ) {
            Text(if (isLoading) "PUBLISHING..." else "Publish Tender", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// ==========================================
// SUCCESS SCREEN (Unchanged)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderPublishSuccessScreen(onViewLiveClick: () -> Unit, onReturnHomeClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("CONFIRMATION", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) }, navigationIcon = { IconButton(onClick = onReturnHomeClick) { Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg))
        },
        containerColor = DarkBg
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF332D00)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Check, contentDescription = null, tint = Gold, modifier = Modifier.size(48.dp)) }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Tender Published!", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Your tender has been successfully listed on the portal and is now visible to all eligible bidders globally.", color = TextGray, fontSize = 14.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = FieldBg), border = borderStroke()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("ACTIVE TENDER", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("New Tender Listed", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = onViewLiveClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                Icon(Icons.Outlined.Visibility, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Live Tender", color = Color.Black, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onReturnHomeClick, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = OutlineColor)) {
                Icon(Icons.Outlined.Dashboard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Return to Dashboard", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==========================================
// REUSABLE COMPONENTS
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TenderWizardScaffold(step: Int, title: String, subtitle: String, onBackClick: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text(title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)) }, containerColor = DarkBg) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
            if (subtitle.isNotEmpty()) { Text(subtitle, color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(12.dp)) }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f).height(4.dp).background(if (step >= 1) Gold else OutlineColor, RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.weight(1f).height(4.dp).background(if (step >= 2) Gold else OutlineColor, RoundedCornerShape(2.dp)))
                Box(modifier = Modifier.weight(1f).height(4.dp).background(if (step >= 3) Gold else OutlineColor, RoundedCornerShape(2.dp)))
            }
            Spacer(modifier = Modifier.height(24.dp)); content(); Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TenderInputField(label: String, placeholder: String, value: String, isDropdown: Boolean = false, keyboardOptions: KeyboardOptions = KeyboardOptions.Default, icon: androidx.compose.ui.graphics.vector.ImageVector? = null, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold); Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, readOnly = isDropdown,
            keyboardOptions = keyboardOptions,
            placeholder = { Text(placeholder, color = Color.DarkGray) },
            leadingIcon = icon?.let { { Icon(it, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp)) } },
            trailingIcon = if (isDropdown) { { Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextGray) } } else null,
            modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp),
            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Gold, unfocusedBorderColor = OutlineColor, unfocusedContainerColor = FieldBg, focusedContainerColor = FieldBg), singleLine = true
        )
    }
}

@Composable
fun SectionHeader(title: String) { Text(title, color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp); Spacer(modifier = Modifier.height(12.dp)) }

@Composable
fun RequirementCheckbox(title: String, subtitle: String, checked: Boolean, enabled: Boolean = true, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = if (enabled) Color.White else Color.Gray, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextGray, fontSize = 11.sp)
        }
        Checkbox(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled, colors = CheckboxDefaults.colors(checkedColor = Gold, checkmarkColor = Color.Black, uncheckedColor = OutlineColor))
    }
}

@Composable
fun WizardNextButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Text(text, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp); Spacer(modifier = Modifier.width(8.dp)); Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black) }
    }
}

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, OutlineColor)