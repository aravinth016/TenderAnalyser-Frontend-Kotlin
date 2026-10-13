package com.simats.tenderanalyser.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.simats.tenderanalyser.ApiClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

// Theme Colors
private val DarkBg = Color(0xFF121212)
private val CardBg = Color(0xFF1A1A24)
private val Gold = Color(0xFFFFC107)
private val OutlineColor = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorityEditProfileScreen(onBackClick: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharedPref = context.getSharedPreferences("AppSession", Context.MODE_PRIVATE)

    val adminId = sharedPref.getString("ADMIN_ID", "") ?: ""

    var adminName by remember { mutableStateOf(sharedPref.getString("ADMIN_NAME", "") ?: "") }
    var designation by remember { mutableStateOf(sharedPref.getString("ADMIN_DESIGNATION", "") ?: "") }
    var deptName by remember { mutableStateOf(sharedPref.getString("ADMIN_DEPT", "") ?: "") }
    var officeCode by remember { mutableStateOf(sharedPref.getString("ADMIN_OFFICE_CODE", "") ?: "") }
    var sector by remember { mutableStateOf(sharedPref.getString("ADMIN_SECTOR", "") ?: "") }
    var hqAddress by remember { mutableStateOf(sharedPref.getString("ADMIN_ADDRESS", "") ?: "") }
    var signatoryName by remember { mutableStateOf(sharedPref.getString("ADMIN_SIGNATORY", "") ?: "") }
    var contactNumber by remember { mutableStateOf(sharedPref.getString("ADMIN_CONTACT", "") ?: "") }

    // Image States
    var currentProfileImageUrl by remember { mutableStateOf(sharedPref.getString("ADMIN_PROFILE_PIC", null)) }
    var newImageUri by remember { mutableStateOf<Uri?>(null) }

    var expandedSector by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    val sectorOptions = listOf("Infrastructure & Construction", "Energy & Power", "IT & Telecommunications", "Healthcare & Medical", "Defense & Security", "Education & Training", "Public Works & Logistics")

    val imagePickerLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) newImageUri = uri
    }

    Scaffold(
        containerColor = DarkBg,
        topBar = {
            TopAppBar(
                title = { Text("Edit Administrator Profile", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Gold) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBg)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            isSaving = true
                            try {
                                fun createPart(text: String): RequestBody = text.toRequestBody("text/plain".toMediaTypeOrNull())

                                var imagePart: MultipartBody.Part? = null
                                if (newImageUri != null) {
                                    val file = File(context.cacheDir, "admin_pic_${System.currentTimeMillis()}.jpg")
                                    val inputStream = context.contentResolver.openInputStream(newImageUri!!)
                                    val outputStream = FileOutputStream(file)
                                    inputStream?.copyTo(outputStream)
                                    val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                    imagePart = MultipartBody.Part.createFormData("profile_image", file.name, requestFile)
                                }

                                val response = ApiClient.apiService.updateAuthorityProfile(
                                    createPart(adminId), createPart(adminName), createPart(designation),
                                    createPart(deptName), createPart(officeCode), createPart(sector),
                                    createPart(hqAddress), createPart(signatoryName), createPart(contactNumber), imagePart
                                )

                                if (response.success) {
                                    sharedPref.edit().apply {
                                        putString("ADMIN_NAME", adminName)
                                        putString("ADMIN_DESIGNATION", designation)
                                        putString("ADMIN_DEPT", deptName)
                                        putString("ADMIN_OFFICE_CODE", officeCode)
                                        putString("ADMIN_SECTOR", sector)
                                        putString("ADMIN_ADDRESS", hqAddress)
                                        putString("ADMIN_SIGNATORY", signatoryName)
                                        putString("ADMIN_CONTACT", contactNumber)
                                        if (response.profileImageUrl != null) putString("ADMIN_PROFILE_PIC", response.profileImageUrl)
                                        apply()
                                    }
                                    Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
                                    onBackClick()
                                } else {
                                    Toast.makeText(context, "Error: ${response.message}", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                            } finally {
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    enabled = !isSaving
                ) {
                    if (isSaving) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(24.dp))
                    else Text("SAVE CHANGES", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- PROFILE PICTURE CLICKABLE BOX ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (newImageUri != null) {
                    AsyncImage(model = newImageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else if (!currentProfileImageUrl.isNullOrEmpty()) {
                    AsyncImage(model = "http://192.168.1.2:5000$currentProfileImageUrl", contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(50.dp))
                }
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CameraAlt, contentDescription = "Change Image", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            AdminEditField("Full Name", adminName) { adminName = it }
            AdminEditField("Designation", designation) { designation = it }
            AdminEditField("Department / PSU", deptName) { deptName = it }
            AdminEditField("Office Code", officeCode) { officeCode = it }

            // --- EXPOSED DROPDOWN MENU FOR SECTOR ---
            ExposedDropdownMenuBox(
                expanded = expandedSector,
                onExpandedChange = { expandedSector = !expandedSector },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                OutlinedTextField(
                    value = sector, onValueChange = {}, readOnly = true,
                    label = { Text("Sector Category", color = Color.Gray) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSector) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFFC107), unfocusedBorderColor = Color(0xFF333333),
                        focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                        focusedContainerColor = Color(0xFF1A1A24), unfocusedContainerColor = Color(0xFF1A1A24)
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedSector, onDismissRequest = { expandedSector = false },
                    modifier = Modifier.background(Color(0xFF1A1A24))
                ) {
                    sectorOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = Color.White) },
                            onClick = { sector = option; expandedSector = false }
                        )
                    }
                }
            }

            AdminEditField("Headquarters Address", hqAddress) { hqAddress = it }
            AdminEditField("Authorized Signatory Name", signatoryName) { signatoryName = it }
            AdminEditField("Contact Number", contactNumber) { contactNumber = it }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AdminEditField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value, onValueChange = onValueChange, label = { Text(label, color = Color.Gray) },
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFFFC107), unfocusedBorderColor = Color(0xFF333333),
            focusedTextColor = Color.White, unfocusedTextColor = Color.White,
            focusedContainerColor = Color(0xFF1A1A24), unfocusedContainerColor = Color(0xFF1A1A24)
        )
    )
}