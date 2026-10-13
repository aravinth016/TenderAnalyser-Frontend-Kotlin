package com.simats.tenderanalyser.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.AuthorityViewModel

@Composable
fun AuthorityRegStep3Screen(viewModel: AuthorityViewModel, onNextClick: () -> Unit, onBackClick: () -> Unit) {
    val context = LocalContext.current
    var sigName by remember { mutableStateOf(viewModel.signatoryName) }
    var contact by remember { mutableStateOf(viewModel.contactNumber) }
    var isDeclared by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) } // Added a loading state

    var authLetterUri by remember { mutableStateOf<Uri?>(null) }
    var idProofUri by remember { mutableStateOf<Uri?>(null) }
    var dscUri by remember { mutableStateOf<Uri?>(null) }
    var deptRegUri by remember { mutableStateOf<Uri?>(null) }

    val l1 = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { authLetterUri = it }
    val l2 = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { idProofUri = it }
    val l3 = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { dscUri = it }
    val l4 = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { deptRegUri = it }

    // 🚨 ADDED: Listen to the ViewModel for Success/Error
    val registrationState by viewModel.registrationState.collectAsState()

    LaunchedEffect(registrationState) {
        registrationState?.let { result ->
            isLoading = false
            if (result.isSuccess) {
                viewModel.resetState()
                onNextClick() // Goes to success screen automatically upon database success!
            } else {
                Toast.makeText(context, result.exceptionOrNull()?.message ?: "Upload Failed", Toast.LENGTH_LONG).show()
                viewModel.resetState()
            }
        }
    }

    AuthorityRegScaffold(step = 3, title = "Document Checklist", subtitle = "STEP 3 OF 3: MANDATORY SUBMISSIONS", onBackClick = onBackClick) {

        AuthorityUploadBox("Official Authorization Letter", authLetterUri != null) { l1.launch("application/pdf") }
        Spacer(modifier = Modifier.height(16.dp))
        AuthorityUploadBox("Government Identity Proof of Admin", idProofUri != null) { l2.launch("application/pdf") }
        Spacer(modifier = Modifier.height(16.dp))
        AuthorityUploadBox("Digital Signature Certificate (DSC)", dscUri != null) { l3.launch("application/pdf") }
        Spacer(modifier = Modifier.height(16.dp))
        AuthorityUploadBox("Departmental Registration Copy", deptRegUri != null) { l4.launch("application/pdf") }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Authorized Signatory Details", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(16.dp))

        AuthorityTextField("SIGNATORY NAME", "Enter Full Legal Name", sigName) { sigName = it }
        AuthorityTextField("OFFICIAL CONTACT NUMBER", "+91 XXXXX XXXXX", contact) { contact = it }

        Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 16.dp)) {
            Checkbox(checked = isDeclared, onCheckedChange = { isDeclared = it }, colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFC107)))
            Text("I hereby declare that I am authorized by the department to act as the primary nodal officer.", color = Color.Gray, fontSize = 12.sp)
        }

        AuthorityNextButton(if (isLoading) "UPLOADING..." else "FINAL VERIFICATION SUBMISSION") {
            if (isLoading) return@AuthorityNextButton

            viewModel.signatoryName = sigName
            viewModel.contactNumber = contact

            // Validate all documents are uploaded
            if (authLetterUri != null && idProofUri != null && dscUri != null && deptRegUri != null && isDeclared) {
                isLoading = true
                viewModel.submitRegistration(context, authLetterUri!!, idProofUri!!, dscUri!!, deptRegUri!!)
                // Notice: onNextClick() was removed from here because the LaunchedEffect handles it now!
            } else {
                Toast.makeText(context, "Please upload all mandatory documents and check the declaration box.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@Composable
fun AuthorityUploadBox(title: String, isUploaded: Boolean, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(if(isUploaded) "UPLOADED" else "MISSING", color = if(isUploaded) Color.Green else Color.Red, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = if(isUploaded) Color.DarkGray else Color(0xFFFFC107))) {
                Text(if(isUploaded) "REPLACE PDF/JPG" else "UPLOAD PDF/JPG", color = if(isUploaded) Color.White else Color.Black)
            }
        }
    }
}