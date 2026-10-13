package com.simats.tenderanalyser.screens

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

import com.simats.tenderanalyser.ApiClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: String = "1",
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }

    var fullName by remember { mutableStateOf("") }
    var jobTitle by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var regNumber by remember { mutableStateOf("") }
    var businessDesc by remember { mutableStateOf("") }
    var hqAddress by remember { mutableStateOf("") }
    var businessEmail by remember { mutableStateOf("") }

    // Separate states for existing server profile URL vs newly picked local URI
    var profileImageUrl by remember { mutableStateOf<String?>(null) }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    var companyLogoUri by remember { mutableStateOf<Uri?>(null) }

    val profileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            profileImageUri = uri
        }
    }
    val logoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> companyLogoUri = uri }

    // FETCH DATA
    LaunchedEffect(userId) {
        try {
            val response = ApiClient.apiService.getUserProfile(userId)
            if (response.success) {
                fullName = response.fullName ?: ""
                jobTitle = response.jobTitle ?: ""
                companyName = response.companyName ?: ""
                regNumber = response.regNumber ?: ""
                businessDesc = response.businessDesc ?: ""
                hqAddress = response.hqAddress ?: ""
                businessEmail = response.businessEmail ?: ""

                // Store server profile image URL/path safely
                if (!response.profileImageUrl.isNullOrEmpty()) {
                    profileImageUrl = response.profileImageUrl
                }
            } else {
                Toast.makeText(context, "Failed to load profile", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Network error", Toast.LENGTH_SHORT).show()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White).statusBarsPadding()) {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Color(0xFF1E40AF))
                    }
                    Text("Edit Profile", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.Black, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Spacer(modifier = Modifier.size(24.dp))
                }
                HorizontalDivider(color = Color(0xFFF1F5F9))
            }
        },
        bottomBar = {
            Surface(color = Color.White, shadowElevation = 8.dp) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding()) {
                    Button(
                        onClick = {
                            isSaving = true
                            coroutineScope.launch {
                                try {
                                    // 1. Convert Text to RequestBodies
                                    val reqId = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val reqName = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val reqJob = jobTitle.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val reqComp = companyName.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val reqReg = regNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val reqDesc = businessDesc.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val reqHq = hqAddress.toRequestBody("text/plain".toMediaTypeOrNull())
                                    val reqEmail = businessEmail.toRequestBody("text/plain".toMediaTypeOrNull())

                                    // 2. Convert newly selected Image to File Part
                                    var profilePart: MultipartBody.Part? = null
                                    profileImageUri?.let { uri ->
                                        if (uri.scheme == "content" || uri.scheme == "file") {
                                            val file = getFileFromUri(context, uri)
                                            if (file != null) {
                                                val reqFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                                                profilePart = MultipartBody.Part.createFormData("profile_image", file.name, reqFile)
                                            }
                                        }
                                    }

                                    // 3. Send API Call
                                    val response = ApiClient.apiService.updateUserProfile(
                                        reqId, reqName, reqJob, reqComp, reqReg, reqDesc, reqHq, reqEmail, profilePart
                                    )

                                    if (response.success) {
                                        Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                                        onSaveClick()
                                    } else {
                                        Toast.makeText(context, "Failed: ${response.message}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Database connection error", Toast.LENGTH_SHORT).show()
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E40AF),
                            disabledContainerColor = Color(0xFF94A3B8)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isSaving && !isLoading
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Save Changes",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1E40AF))
            }
        } else {
            Column(modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionTitle(icon = Icons.Outlined.Person, title = "Personal Information")

                // Profile Image Section with Supabase/Render support
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.clickable { profileLauncher.launch("image/*") }) {
                        Box(modifier = Modifier.size(80.dp).clip(CircleShape).background(Color(0xFF9CA3AF)), contentAlignment = Alignment.Center) {
                            val imageModel = when {
                                profileImageUri != null -> profileImageUri
                                !profileImageUrl.isNullOrEmpty() -> {
                                    if (profileImageUrl!!.startsWith("http://") || profileImageUrl!!.startsWith("https://")) {
                                        profileImageUrl
                                    } else {
                                        "https://tender-analyser-api.onrender.com$profileImageUrl"
                                    }
                                }
                                else -> null
                            }

                            if (imageModel != null) {
                                AsyncImage(model = imageModel, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            } else {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                            }
                        }
                        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color(0xFF1E40AF)).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Change Photo", color = Color(0xFF1E40AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EditTextField(label = "Full Name", value = fullName, onValueChange = { fullName = it })
                    EditTextField(label = "Job Title", value = jobTitle, onValueChange = { jobTitle = it })
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 16.dp))

                SectionTitle(icon = Icons.Outlined.Business, title = "Company Information")

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd, modifier = Modifier.clickable { logoLauncher.launch("image/*") }) {
                        Box(modifier = Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF1F5F9)).border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                            if (companyLogoUri != null) AsyncImage(model = companyLogoUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                            else Icon(Icons.Default.BarChart, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(32.dp))
                        }
                        Box(modifier = Modifier.size(24.dp).offset(x = 8.dp, y = 8.dp).clip(CircleShape).background(Color(0xFF1E40AF)).border(2.dp, Color.White, CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Outlined.FileUpload, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Company Logo (PNG, JPG)", color = Color.Gray, fontSize = 10.sp)
                }

                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    EditTextField(label = "Company Name", value = companyName, onValueChange = { companyName = it })
                    EditTextField(label = "Registration Number", value = regNumber, onValueChange = { regNumber = it })
                    EditTextField(label = "Business Description", value = businessDesc, onValueChange = { businessDesc = it }, singleLine = false, modifier = Modifier.height(100.dp))
                }

                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 16.dp))

                SectionTitle(icon = Icons.Outlined.Language, title = "Contact Details")

                Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
                    EditTextField(label = "Headquarters Address", value = hqAddress, onValueChange = { hqAddress = it }, leadingIcon = { Icon(Icons.Outlined.LocationOn, null, tint = Color.Gray, modifier = Modifier.size(18.dp)) })
                    EditTextField(label = "Business Email", value = businessEmail, onValueChange = { businessEmail = it }, leadingIcon = { Icon(Icons.Outlined.Email, null, tint = Color.Gray, modifier = Modifier.size(18.dp)) })
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Helper function to extract file from URI
fun getFileFromUri(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val tempFile = File(context.cacheDir, "temp_profile.jpg")
        val outputStream = FileOutputStream(tempFile)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        null
    }
}

@Composable
fun SectionTitle(icon: ImageVector, title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Color(0xFF1E40AF), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E293B))
    }
}

@Composable
fun EditTextField(
    label: String, value: String, onValueChange: (String) -> Unit, leadingIcon: @Composable (() -> Unit)? = null, singleLine: Boolean = true, modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray, modifier = Modifier.padding(bottom = 6.dp))
        OutlinedTextField(
            value = value, onValueChange = onValueChange, modifier = modifier.fillMaxWidth(), singleLine = singleLine, leadingIcon = leadingIcon,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp, color = Color.Black),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0), focusedBorderColor = Color(0xFF1E40AF),
                unfocusedContainerColor = Color.White, focusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        )
    }
}