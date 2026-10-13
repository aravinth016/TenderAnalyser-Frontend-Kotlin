package com.simats.tenderanalyser.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.tenderanalyser.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

// --- Theme Colors ---
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val TextGray = Color(0xFFA0A0A0)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityDepartmentInfoScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPref = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)

    val adminId = sharedPref.getString("ADMIN_ID", "") ?: ""

    // We need these so we don't accidentally overwrite them with blanks in the database
    val adminName = sharedPref.getString("ADMIN_NAME", "") ?: ""
    val designation = sharedPref.getString("ADMIN_DESIGNATION", "") ?: ""
    val signatoryName = sharedPref.getString("ADMIN_SIGNATORY", "") ?: ""

    // Editable State Variables populated from memory
    var deptName by remember { mutableStateOf(sharedPref.getString("ADMIN_DEPT", "") ?: "") }
    var officeCode by remember { mutableStateOf(sharedPref.getString("ADMIN_OFFICE_CODE", "") ?: "") }
    var address by remember { mutableStateOf(sharedPref.getString("ADMIN_ADDRESS", "") ?: "") }
    var landline by remember { mutableStateOf(sharedPref.getString("ADMIN_CONTACT", "") ?: "") }
    var sector by remember { mutableStateOf(sharedPref.getString("ADMIN_SECTOR", "") ?: "") }

    var expandedSector by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val sectorOptions = listOf("Infrastructure & Construction", "Energy & Power", "IT & Telecommunications", "Healthcare & Medical", "Defense & Security", "Education & Training", "Public Works & Logistics")

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Department Info", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Gold) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Surface(color = DarkBg, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                try {
                                    // Helper function for API Body
                                    fun createPart(text: String): RequestBody = text.toRequestBody("text/plain".toMediaTypeOrNull())

                                    // Make the API Call to Update Profile
                                    val response = ApiClient.apiService.updateAuthorityProfile(
                                        adminId = createPart(adminId),
                                        adminName = createPart(adminName),
                                        designation = createPart(designation),
                                        deptName = createPart(deptName),
                                        officeCode = createPart(officeCode),
                                        sector = createPart(sector),
                                        hqAddress = createPart(address),
                                        signatoryName = createPart(signatoryName),
                                        contactNumber = createPart(landline),
                                        profileImage = null // We aren't updating the image from this screen
                                    )

                                    if (response.success) {
                                        // Save back to local memory so it updates instantly
                                        sharedPref.edit().apply {
                                            putString("ADMIN_DEPT", deptName)
                                            putString("ADMIN_OFFICE_CODE", officeCode)
                                            putString("ADMIN_SECTOR", sector)
                                            putString("ADMIN_ADDRESS", address)
                                            putString("ADMIN_CONTACT", landline)
                                            apply()
                                        }
                                        Toast.makeText(context, "Department Info Updated!", Toast.LENGTH_SHORT).show()
                                        onBackClick()
                                    } else {
                                        Toast.makeText(context, response.message ?: "Update Failed", Toast.LENGTH_SHORT).show()
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
                        colors = ButtonDefaults.buttonColors(containerColor = Gold),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                        } else {
                            Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- GENERAL DETAILS ---
            Text("GENERAL DETAILS", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            DeptInputField("Department Name", deptName) { deptName = it }
            Spacer(modifier = Modifier.height(16.dp))

            DeptInputField("Office Code / Division ID", officeCode) { officeCode = it }
            Spacer(modifier = Modifier.height(32.dp))

            // --- LOGISTICS ---
            Text("LOGISTICS", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            DeptInputField("Headquarters Address", address, singleLine = false, minLines = 3) { address = it }
            Spacer(modifier = Modifier.height(16.dp))

            DeptInputField("Authorized Landline", landline) { landline = it }
            Spacer(modifier = Modifier.height(32.dp))

            // --- SECTOR ---
            Text("SECTOR", color = Gold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = expandedSector,
                onExpandedChange = { expandedSector = !expandedSector },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = sector,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Primary Sector Category", color = TextGray, fontSize = 10.sp) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSector) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Gold, unfocusedBorderColor = OutlineColor,
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = CardBg, unfocusedContainerColor = CardBg
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedSector,
                    onDismissRequest = { expandedSector = false },
                    modifier = Modifier.background(CardBg)
                ) {
                    sectorOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = Color.White) },
                            onClick = {
                                sector = option
                                expandedSector = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeptInputField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    minLines: Int = 1,
    trailingIcon: ImageVector? = null,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, color = TextGray, fontSize = 10.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            minLines = minLines,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Gold,
                unfocusedBorderColor = OutlineColor,
                focusedContainerColor = CardBg,
                unfocusedContainerColor = CardBg,
                cursorColor = Gold
            ),
            trailingIcon = trailingIcon?.let {
                { Icon(it, contentDescription = null, tint = Gold) }
            }
        )
    }
}